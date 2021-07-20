package com.tvd12.ezyhttp.client;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.concurrent.*;
import com.tvd12.ezyfox.util.EzyCloseable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyStartable;
import com.tvd12.ezyfox.util.EzyStoppable;
import com.tvd12.ezyhttp.client.callback.RequestCallback;
import com.tvd12.ezyhttp.client.concurrent.RequestFutureTask;
import com.tvd12.ezyhttp.client.exception.ClientNotActiveException;
import com.tvd12.ezyhttp.client.exception.RequestQueueFullException;
import com.tvd12.ezyhttp.client.request.Request;
import com.tvd12.ezyhttp.client.request.RequestQueue;
import com.tvd12.ezyhttp.core.concurrent.HttpThreadFactory;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

import java.util.List;
import java.util.Map;

public class HttpClientProxy<T, R>
        extends EzyLoggable
        implements EzyStartable, EzyStoppable, EzyCloseable {

    protected final HttpClient client;
    protected final int threadPoolSize;
    protected final RequestQueue<T> requestQueue;
    protected final EzyFutureMap<Request<T>> futures;
    protected EzyThreadList threadList;
    protected volatile boolean active;

    public HttpClientProxy(
            Builder<T, R> builder
    ) {
        this.client = builder.clientBuilder.build();
        this.threadPoolSize = builder.threadPoolSize;
        this.futures = new EzyFutureConcurrentHashMap<>();
        this.requestQueue = new RequestQueue<>(builder.requestQueueCapacity);
    }

    public static <T, R> Builder<T, R> builder() {
        return new Builder<>();
    }

    @Override
    public void start() {
        this.active = true;
        this.threadList = new EzyThreadList(
                threadPoolSize,
                this::loop, HttpThreadFactory.create("client"));
        this.threadList.execute();
    }

    @Override
    public void stop() {
        this.active = false;
    }

    @Override
    public void close() {
        this.active = false;
        this.requestQueue.clear();
        Map<Request<T>, EzyFuture> undoneTasks = futures.clear();
        for (Request<T> undoneRequest : undoneTasks.keySet()) {
            EzyFuture undoneTask = undoneTasks.get(undoneRequest);
            undoneTask.cancel("HttpClientProxy close, request to: " + undoneRequest.getURL() + " has cancelled");
        }
        this.threadList.interrupt();
    }

    protected void loop() {
        while (active) {
            handleRequests();
        }
    }

    protected void handleRequests() {
        EzyFuture future = null;
        try {
            Request<T> request =  requestQueue.take();
            future = futures.removeFuture(request);
            ResponseEntity<R> response = client.request(
                    request.getMethod(),
                    request.getURL(),
                    request.getEntity(),
                    request.getResponseTypes(),
                    request.getConnectTimeout(),
                    request.getReadTimeout()
            );
            future.setResult(response);
        } catch (Exception e) {
            if (future != null)
                future.setException(e);
        }
    }

    public R call(Request<T> request, int timeout) throws Exception {
        ResponseEntity<R> entity = request(request, timeout);
        return client.getResponseBody(entity);
    }

    public ResponseEntity<R> request(Request<T> request, int timeout) throws Exception {
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

    public void fire(Request<T> request, RequestCallback<R> callback) {
        execute(request, new RequestCallback<ResponseEntity<R>>() {
            @Override
            public void onResponse(ResponseEntity<R> response) {
                try {
                    R body = response.getBody();
                    callback.onResponse(body);
                } catch (Exception e) {
                    onException(e);
                }
            }

            @Override
            public void onException(Exception e) {
                logger.error(e.getMessage());
                callback.onResponse(null);
            }
        });
    }

    public void execute(Request<T> request, RequestCallback<ResponseEntity<R>> callback) {
        EzyFuture future = new RequestFutureTask(callback);
        futures.addFuture(request, future);
        try {
            addRequest(request);
        } catch (Exception e) {
            futures.removeFuture(request);
            throw e;
        }
    }

    protected void addRequest(Request<T> request) {
        if (!active)
            throw new ClientNotActiveException();
        if (!requestQueue.add(request))
            throw new RequestQueueFullException(requestQueue.getCapacity());
    }

    public static class Builder<T, R> implements EzyBuilder<HttpClientProxy<T, R>> {
        protected int threadPoolSize;
        protected int requestQueueCapacity;
        protected HttpClient.Builder clientBuilder;

        public Builder() {
            this.threadPoolSize = 16;
            this.requestQueueCapacity = 10000;
            this.clientBuilder = HttpClient.builder();

        }

        public Builder<T, R> readTimeout(int readTimeout) {
            clientBuilder.readTimeout(readTimeout);
            return this;
        }

        public Builder<T, R> connectTimeout(int connectTimeout) {
            clientBuilder.connectTimeout(connectTimeout);
            return this;
        }

        public Builder<T, R> setStringConverter(Object converter) {
            clientBuilder.setStringConverter(converter);
            return this;
        }

        public Builder<T, R> addBodyConverter(Object converter) {
            clientBuilder.addBodyConverter(converter);
            return this;
        }

        public Builder<T, R> addBodyConverters(List<?> converters) {
            clientBuilder.addBodyConverters(converters);
            return this;
        }

        public Builder<T, R> threadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        protected Builder<T, R> requestQueueCapacity(int requestQueueCapacity) {
            this.requestQueueCapacity = requestQueueCapacity;
            return this;
        }

        @Override
        public HttpClientProxy<T, R> build() {
            return new HttpClientProxy<>(this);
        }
    }

}
