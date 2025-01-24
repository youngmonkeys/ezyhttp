package com.tvd12.ezyhttp.client;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.concurrent.*;
import com.tvd12.ezyfox.util.*;
import com.tvd12.ezyhttp.client.callback.RequestCallback;
import com.tvd12.ezyhttp.client.concurrent.DownloadCancellationToken;
import com.tvd12.ezyhttp.client.concurrent.RequestFutureTask;
import com.tvd12.ezyhttp.client.concurrent.UploadCancellationToken;
import com.tvd12.ezyhttp.client.data.DownloadFileResult;
import com.tvd12.ezyhttp.client.exception.ClientNotActiveException;
import com.tvd12.ezyhttp.client.exception.RequestQueueFullException;
import com.tvd12.ezyhttp.client.request.DownloadRequest;
import com.tvd12.ezyhttp.client.request.Request;
import com.tvd12.ezyhttp.client.request.RequestQueue;
import com.tvd12.ezyhttp.client.request.UploadRequest;
import com.tvd12.ezyhttp.core.concurrent.HttpThreadFactory;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tvd12.ezyfox.util.EzyProcessor.processWithException;

public class HttpClientProxy
    extends EzyLoggable
    implements EzyStartable, EzyStoppable, EzyCloseable {

    protected volatile boolean active;
    protected final HttpClient client;
    protected final int threadPoolSize;
    protected final AtomicBoolean started;
    protected final RequestQueue requestQueue;
    protected ExecutorService executorService;
    protected final EzyFutureMap<Request> futures;

    public HttpClientProxy(
        int threadPoolSize,
        int requestQueueCapacity,
        HttpClient client
    ) {
        this(threadPoolSize, requestQueueCapacity, false, client);
    }

    public HttpClientProxy(
        int threadPoolSize,
        int requestQueueCapacity,
        boolean autoStart,
        HttpClient client
    ) {
        this.client = client;
        this.threadPoolSize = threadPoolSize;
        this.started = new AtomicBoolean(false);
        this.futures = new EzyFutureConcurrentHashMap<>();
        this.requestQueue = new RequestQueue(requestQueueCapacity);
        this.doStart(autoStart);
    }

    private void doStart(boolean autoStart) {
        if (autoStart) {
            processWithException(this::start);
        }
    }

    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        this.active = true;
        this.executorService = EzyExecutors.newFixedThreadPool(
            threadPoolSize,
            HttpThreadFactory.create("client")
        );
        for (int i = 0; i < threadPoolSize; ++i) {
            this.executorService.execute(this::loop);
        }
    }

    @Override
    public void stop() {
        EzyProcessor.processWithLogException(this::close);
    }

    @Override
    public void close() {
        this.active = false;
        this.requestQueue.clear();
        Map<Request, EzyFuture> undoneTasks = futures.clear();
        for (Request undoneRequest : undoneTasks.keySet()) {
            EzyFuture undoneTask = undoneTasks.get(undoneRequest);
            undoneTask.cancel(
                "HttpClientProxy close, request to: " +
                    undoneRequest.getURL() + " has cancelled"
            );
        }
        if (executorService != null) {
            this.executorService.shutdownNow();
        }
    }

    protected void loop() {
        while (active) {
            handleRequests();
        }
    }

    protected void handleRequests() {
        Request request = null;
        EzyFuture future = null;
        Exception exception = null;
        ResponseEntity response = null;
        try {
            request = requestQueue.take();
            future = futures.removeFuture(request);
            response = client.request(request);
        } catch (Exception e) {
            exception = e;
        }
        try {
            if (future != null) {
                if (exception != null) {
                    future.setException(exception);
                } else {
                    future.setResult(response);
                }
            } else {
                if (exception != null) {
                    logger.info(
                        "handled request: {} with exception, but there is no future",
                        request,
                        exception
                    );
                } else {
                    logger.info("handled request: {} with response: {}, but there is no future",
                        request,
                        response
                    );
                }
            }
        } catch (Throwable e) {
            logger.info("handle request result error", e);
        }
    }

    public <T> T call(Request request, int timeout) throws Exception {
        ResponseEntity entity = request(request, timeout);
        return client.getResponseBody(entity);
    }

    public ResponseEntity request(Request request, int timeout) throws Exception {
        EzyFuture future = new EzyFutureTask();
        futures.addFuture(request, future);
        try {
            addRequest(request);
        } catch (Exception e) {
            futures.removeFuture(request);
            throw e;
        }
        return future.get(timeout);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void fire(Request request, RequestCallback callback) {
        execute(request, new RequestCallback<ResponseEntity>() {
            @Override
            public void onResponse(ResponseEntity response) {
                callback.onResponse(response.getBody());
            }

            @Override
            public void onException(Exception e) {
                callback.onException(e);
            }
        });
    }

    public void execute(Request request, RequestCallback<ResponseEntity> callback) {
        EzyFuture future = new RequestFutureTask(callback);
        futures.addFuture(request, future);
        try {
            addRequest(request);
        } catch (Exception e) {
            futures.removeFuture(request);
            throw e;
        }
    }

    protected void addRequest(Request request) {
        if (!active) {
            throw new ClientNotActiveException();
        }
        if (!requestQueue.add(request)) {
            throw new RequestQueueFullException(requestQueue.getCapacity());
        }
    }

    /**
     * Downloads a file from a URL and store to a file.
     *
     * @param fileURL       HTTP URL of the file to be downloaded
     * @param storeLocation path of the directory to save the file
     * @return the downloaded file name
     * @throws IOException when there is any I/O error
     */
    public String download(String fileURL, File storeLocation) throws Exception {
        return client.download(fileURL, storeLocation);
    }

    /**
     * Downloads a file from a URL and store to a file.
     *
     * @param fileURL           HTTP URL of the file to be downloaded
     * @param storeLocation     path of the directory to save the file
     * @param cancellationToken the token to cancel
     * @return the downloaded file name
     * @throws IOException when there is any I/O error
     */
    public String download(
        String fileURL,
        File storeLocation,
        DownloadCancellationToken cancellationToken
    ) throws Exception {
        return client.download(fileURL, storeLocation, cancellationToken);
    }

    /**
     * Downloads a file from a URL and store to a file.
     *
     * @param request       the request of the file to be downloaded
     * @param storeLocation path of the directory to save the file
     * @return the downloaded file name
     * @throws IOException when there is any I/O error
     */
    public String download(
        DownloadRequest request,
        File storeLocation
    ) throws Exception {
        return client.download(request, storeLocation);
    }

    /**
     * Downloads a file from a URL and store to a file.
     *
     * @param request           the request of the file to be downloaded
     * @param storeLocation     path of the directory to save the file
     * @param cancellationToken the token to cancel
     * @return the downloaded file name
     * @throws IOException when there is any I/O error
     */
    public String download(
        DownloadRequest request,
        File storeLocation,
        DownloadCancellationToken cancellationToken
    ) throws Exception {
        return client.download(request, storeLocation, cancellationToken);
    }

    /**
     * Downloads a file from a URL and store to an output stream.
     *
     * @param fileURL      HTTP URL of the file to be downloaded
     * @param outputStream the output stream to save the file
     * @throws IOException when there is any I/O error
     */
    public void download(
        String fileURL,
        OutputStream outputStream
    ) throws Exception {
        client.download(fileURL, outputStream);
    }

    /**
     * Downloads a file from a URL and store to an output stream.
     *
     * @param fileURL           HTTP URL of the file to be downloaded
     * @param outputStream      the output stream to save the file
     * @param cancellationToken the token to cancel
     * @throws IOException when there is any I/O error
     */
    public void download(
        String fileURL,
        OutputStream outputStream,
        DownloadCancellationToken cancellationToken
    ) throws Exception {
        client.download(fileURL, outputStream, cancellationToken);
    }

    /**
     * Downloads a file from a URL and store to an output stream.
     *
     * @param request      the request of the file to be downloaded
     * @param outputStream the output stream to save the file
     * @throws IOException when there is any I/O error
     */
    public void download(
        DownloadRequest request,
        OutputStream outputStream
    ) throws Exception {
        client.download(request, outputStream);
    }

    /**
     * Downloads a file from a URL and store to an output stream.
     *
     * @param request           the request of the file to be downloaded
     * @param outputStream      the output stream to save the file
     * @param cancellationToken the token to cancel
     * @throws IOException when there is any I/O error
     */
    public void download(
        DownloadRequest request,
        OutputStream outputStream,
        DownloadCancellationToken cancellationToken
    ) throws Exception {
        client.download(request, outputStream, cancellationToken);
    }

    /**
     * Downloads a file from a URL and store to
     * <code>storeLocation/fileName.extension</code> file.
     *
     * @param fileUrl           HTTP URL of the file to be downloaded
     * @param storeLocation     path of the directory to save the file
     * @param fileName          the output file name
     * @return the downloaded result
     * @throws IOException when there is any I/O error
     */
    public DownloadFileResult download(
        String fileUrl,
        File storeLocation,
        String fileName
    ) throws Exception {
        return client.download(
            fileUrl,
            storeLocation,
            fileName
        );
    }

    /**
     * Downloads a file from a URL and store to
     * <code>storeLocation/fileName.extension</code> file.
     *
     * @param fileUrl           HTTP URL of the file to be downloaded
     * @param storeLocation     path of the directory to save the file
     * @param fileName          the output file name
     * @param cancellationToken the token to cancel
     * @return the downloaded result
     * @throws IOException when there is any I/O error
     */
    public DownloadFileResult download(
        String fileUrl,
        File storeLocation,
        String fileName,
        DownloadCancellationToken cancellationToken
    ) throws Exception {
        return client.download(
            fileUrl,
            storeLocation,
            fileName,
            cancellationToken
        );
    }

    /**
     * Downloads a file from a URL and store to
     * <code>storeLocation/fileName.extension</code> file.
     *
     * @param request           the request of the file to be downloaded
     * @param storeLocation     path of the directory to save the file
     * @param fileName          the output file name
     * @return the downloaded result
     * @throws IOException when there is any I/O error
     */
    public DownloadFileResult download(
        DownloadRequest request,
        File storeLocation,
        String fileName
    ) throws Exception {
        return client.download(
            request,
            storeLocation,
            fileName
        );
    }

    /**
     * Downloads a file from a URL and store to
     * <code>storeLocation/fileName.extension</code> file.
     *
     * @param request           the request of the file to be downloaded
     * @param storeLocation     path of the directory to save the file
     * @param fileName          the output file name
     * @param cancellationToken the token to cancel
     * @return the downloaded result
     * @throws IOException when there is any I/O error
     */
    public DownloadFileResult download(
        DownloadRequest request,
        File storeLocation,
        String fileName,
        DownloadCancellationToken cancellationToken
    ) throws Exception {
        return client.download(
            request,
            storeLocation,
            fileName,
            cancellationToken
        );
    }

    /**
     * Upload a file and get the result as object.
     *
     * @param request the upload request.
     * @return  the upload result
     * @throws Exception when there is any I/O error
     */
    public <T> T callUpload(
        UploadRequest request
    ) throws Exception {
        return client.callUpload(request);
    }

    /**
     * Upload a file and get the result as object.
     *
     * @param request the upload request.
     * @param cancellationToken the token to cancel
     * @return  the upload result
     * @throws Exception when there is any I/O error
     */
    public <T> T callUpload(
        UploadRequest request,
        UploadCancellationToken cancellationToken
    ) throws Exception {
        return client.callUpload(request, cancellationToken);
    }

    /**
     * Upload a file and get ResponseEntity result.
     *
     * @param request the upload request.
     * @return  the upload result
     * @throws Exception when there is any I/O error
     */
    public ResponseEntity upload(
        UploadRequest request
    ) throws Exception {
        return client.upload(request);
    }

    /**
     * Upload a file and get ResponseEntity result.
     *
     * @param request the upload request
     * @param cancellationToken the token to cancel
     * @return the upload result
     * @throws Exception when there is any I/O error
     */
    public ResponseEntity upload(
        UploadRequest request,
        UploadCancellationToken cancellationToken
    ) throws Exception {
        return client.upload(request, cancellationToken);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<HttpClientProxy> {
        protected boolean autoStart;
        protected int threadPoolSize;
        protected int requestQueueCapacity;
        protected final HttpClient.Builder clientBuilder;

        public Builder() {
            this.threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;
            this.requestQueueCapacity = 10000;
            this.clientBuilder = HttpClient.builder();
        }

        public Builder autoStart(boolean autoStart) {
            this.autoStart = autoStart;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            clientBuilder.readTimeout(readTimeout);
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            clientBuilder.connectTimeout(connectTimeout);
            return this;
        }

        public Builder setStringConverter(Object converter) {
            clientBuilder.setStringConverter(converter);
            return this;
        }

        public Builder addBodyConverter(Object converter) {
            clientBuilder.addBodyConverter(converter);
            return this;
        }

        public Builder addBodyConverter(String contentType, Object converter) {
            this.clientBuilder.addBodyConverter(contentType, converter);
            return this;
        }

        public Builder addBodyConverters(List<?> converters) {
            clientBuilder.addBodyConverters(converters);
            return this;
        }

        public Builder addBodyConverters(Map<String, Object> converterByContentType) {
            this.clientBuilder.addBodyConverters(converterByContentType);
            return this;
        }

        public Builder threadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder requestQueueCapacity(int requestQueueCapacity) {
            this.requestQueueCapacity = requestQueueCapacity;
            return this;
        }

        @Override
        public HttpClientProxy build() {
            return new HttpClientProxy(
                threadPoolSize,
                requestQueueCapacity,
                autoStart,
                clientBuilder.build()
            );
        }
    }
}
