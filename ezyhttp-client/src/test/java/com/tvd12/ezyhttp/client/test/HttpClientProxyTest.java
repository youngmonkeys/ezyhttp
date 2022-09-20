package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyfox.exception.BadRequestException;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyfox.util.EzyProcessor;
import com.tvd12.ezyfox.util.EzyThreads;
import com.tvd12.ezyfox.util.EzyWrap;
import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.HttpClientProxy;
import com.tvd12.ezyhttp.client.callback.RequestCallback;
import com.tvd12.ezyhttp.client.concurrent.DownloadCancellationToken;
import com.tvd12.ezyhttp.client.data.DownloadFileResult;
import com.tvd12.ezyhttp.client.exception.ClientNotActiveException;
import com.tvd12.ezyhttp.client.exception.DownloadCancelledException;
import com.tvd12.ezyhttp.client.exception.RequestQueueFullException;
import com.tvd12.ezyhttp.client.request.*;
import com.tvd12.ezyhttp.client.test.request.HelloRequest;
import com.tvd12.ezyhttp.client.test.server.TestApplicationBootstrap;
import com.tvd12.ezyhttp.core.annotation.BodyConvert;
import com.tvd12.ezyhttp.core.codec.BodyConverter;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

public class HttpClientProxyTest extends BaseTest {

    public static void main(String[] args) throws Exception {
        HttpClientProxy client = HttpClientProxy.builder()
            .build();
        client.start();
        postTest(client);
        new Thread(() -> {
            for (int i = 0; i < 100; ++i) {
                try {
                    postTest(client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(3);
        getTest(client);
        client.close();
    }

    protected static void getTest(HttpClientProxy client) throws Exception {
        GetRequest request = new GetRequest()
            .setURL("http://localhost:8081/bye?messages=a,b,c&numbers=1,2,3")
            .setEntity(null)
            .setResponseType(String.class)
            .setReadTimeout(HttpClient.NO_TIMEOUT)
            .setConnectTimeout(HttpClient.NO_TIMEOUT);
        String response = client.call(request, 1000);
        System.out.println(response);
    }

    protected static void postTest(HttpClientProxy client) throws Exception {
        HelloRequest body = new HelloRequest();
        body.setWho("dzung");
        RequestEntity entity = RequestEntity.body(body);
        PostRequest request = new PostRequest()
            .setURL("http://localhost:8081/")
            .setEntity(entity)
            .setResponseType(String.class)
            .setReadTimeout(HttpClient.NO_TIMEOUT)
            .setConnectTimeout(HttpClient.NO_TIMEOUT);
        String response = client.call(request, 1000);
        System.out.println(response);
    }

    @Test
    public void getJsonTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        GetRequest request = new GetRequest()
            .setConnectTimeout(15000)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet?who=Monkey")
            .setURL(new URL("http://127.0.0.1:18081/greet?who=Monkey"))
            .setURL(URI.create("http://127.0.0.1:18081/greet?who=Monkey"));

        // when
        TestResponse actual = sut.call(request, 15000);

        // then
        TestResponse expectation = new TestResponse("Greet Monkey!");
        Asserts.assertEquals(expectation, actual);
        sut.close();
        sut.stop();
    }

    @Test
    public void fireJsonTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        GetRequest request = new GetRequest()
            .setConnectTimeout(15000)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet?who=Monkey");

        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        EzyWrap<TestResponse> wrap = new EzyWrap<>();
        sut.fire(request, new RequestCallback<TestResponse>() {
            @Override
            public void onResponse(TestResponse response) {
                wrap.setValue(response);
                countDownLatch.countDown();
            }

            @Override
            public void onException(Exception e) {
            }
        });
        countDownLatch.await();

        // then
        TestResponse expectation = new TestResponse("Greet Monkey!");
        Asserts.assertEquals(expectation, wrap.getValue());
        sut.close();
        sut.stop();
    }

    @Test
    public void fireJsonButExceptionInCallbackTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        GetRequest request = new GetRequest()
            .setConnectTimeout(15000)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet?who=Monkey");

        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        EzyWrap<TestResponse> wrap = new EzyWrap<>();
        sut.fire(request, new RequestCallback<TestResponse>() {
            @Override
            public void onResponse(TestResponse response) {
                wrap.setValue(response);
                countDownLatch.countDown();
                throw new RuntimeException("just test");
            }

            @Override
            public void onException(Exception e) {
            }
        });
        countDownLatch.await();
        Thread.sleep(100);

        // then
        TestResponse expectation = new TestResponse("Greet Monkey!");
        Asserts.assertEquals(expectation, wrap.getValue());
        sut.close();
        sut.stop();
    }

    @Test
    public void fireExceptionTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        GetRequest request = new GetRequest()
            .setConnectTimeout(15000)
            .setResponseType(String.class)
            .setResponseType(StatusCodes.OK, String.class)
            .setURL("http://unknow-host:18081/greet");

        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        EzyWrap<Exception> wrap = new EzyWrap<>();
        sut.fire(request, new RequestCallback<String>() {
            @Override
            public void onResponse(String response) {
            }

            @Override
            public void onException(Exception e) {
                wrap.setValue(e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        // then
        Asserts.assertEquals(BadRequestException.class, wrap.getValue().getClass());
        sut.close();
        sut.stop();
    }

    @Test
    public void executeJsonTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        GetRequest request = new GetRequest()
            .setConnectTimeout(15000)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet?who=Monkey");

        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        EzyWrap<TestResponse> wrap = new EzyWrap<>();
        sut.execute(request, new RequestCallback<ResponseEntity>() {
            @Override
            public void onResponse(ResponseEntity response) {
                wrap.setValue(response.getBody());
                countDownLatch.countDown();
            }

            @Override
            public void onException(Exception e) {
            }
        });
        countDownLatch.await();

        // then
        TestResponse expectation = new TestResponse("Greet Monkey!");
        Asserts.assertEquals(expectation, wrap.getValue());
        sut.close();
        sut.stop();
    }

    @Test
    public void executeExceptionTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        GetRequest request = new GetRequest()
            .setConnectTimeout(15000)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1.0:18081/greet");

        // when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        EzyWrap<Exception> wrap = new EzyWrap<>();
        sut.execute(request, new RequestCallback<ResponseEntity>() {
            @Override
            public void onResponse(ResponseEntity response) {
                System.out.println(response);
            }

            @Override
            public void onException(Exception e) {
                wrap.setValue(e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        // then
        Asserts.assertEquals(UnknownHostException.class, wrap.getValue().getClass());
        sut.close();
        sut.stop();
    }

    @Test
    public void postJsonTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        PostRequest request = new PostRequest()
            .setConnectTimeout(15000)
            .setEntity(new TestRequest("Monkey"))
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet")
            .setURL(new URL("http://127.0.0.1:18081/greet"))
            .setURL(URI.create("http://127.0.0.1:18081/greet"));

        // when
        TestResponse actual = sut.call(request, 15000);

        // then
        TestResponse expectation = new TestResponse("Greet Monkey!");
        Asserts.assertEquals(expectation, actual);
        sut.close();
        sut.stop();
    }

    @Test
    public void postWithExceptionTest() {
        // given
        HttpClientProxy sut = newClientProxy();

        PostRequest request = new PostRequest()
            .setConnectTimeout(15000)
            .setEntity(boolean.class)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1.0:18081/greet");

        // when
        Throwable e = Asserts.assertThrows(() -> sut.call(request, 150000));

        // then
        Asserts.assertThat(e).isEqualsType(UnknownHostException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void putJsonTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        PutRequest request = new PutRequest()
            .setConnectTimeout(15000)
            .setEntity(new TestRequest("Monkey"))
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet")
            .setURL(new URL("http://127.0.0.1:18081/greet"))
            .setURL(URI.create("http://127.0.0.1:18081/greet"));

        // when
        TestResponse actual = sut.call(request, 15000);

        // then
        TestResponse expectation = new TestResponse("Greet Monkey!");
        Asserts.assertEquals(expectation, actual);
        sut.close();
        sut.stop();
    }

    @Test
    public void deleteJsonTest() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();

        DeleteRequest request = new DeleteRequest()
            .setConnectTimeout(15000)
            .setEntity(new TestRequest("Monkey"))
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet")
            .setURL(new URL("http://127.0.0.1:18081/greet"))
            .setURL(URI.create("http://127.0.0.1:18081/greet"));

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.call(request, 15000)
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void startTest() {
        // given
        HttpClientProxy proxy = HttpClientProxy.builder().build();

        // when
        proxy.start();

        // then
        proxy.stop();
    }

    @Test
    public void closeWithRemainTasks() {
        // given
        HttpClientProxy sut = new HttpClientProxy(1, 100, HttpClient.builder().build());
        EzyFutureMap<Request> futures = FieldUtil.getFieldValue(sut, "futures");

        Request request = mock(Request.class);
        futures.addFuture(request);

        sut.start();

        // when
        sut.close();

        // then
        verify(request, times(1)).getURL();
    }

    @Test
    public void handleRequestsNoFutureWhenException() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();
        RequestQueue queue = FieldUtil.getFieldValue(sut, "requestQueue");

        Request request = mock(Request.class);

        // when
        queue.add(request);
        Thread.sleep(100);

        // then
        sut.close();
    }

    @Test
    public void handleRequestsNoFutureWhenResponse() throws Exception {
        // given
        HttpClientProxy sut = newClientProxy();
        RequestQueue queue = FieldUtil.getFieldValue(sut, "requestQueue");

        GetRequest request = new GetRequest()
            .setConnectTimeout(15000)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet?who=Monkey");

        // when
        queue.add(request);
        Thread.sleep(100);

        // then
        sut.close();
    }

    @Test
    public void clientWasNotActive() {
        // given
        HttpClientProxy sut = HttpClientProxy.builder().build();

        PostRequest request = new PostRequest()
            .setConnectTimeout(15000)
            .setEntity(String.class)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet");

        // when
        Throwable e = Asserts.assertThrows(() -> sut.call(request, 150000));

        // then
        Asserts.assertThat(e).isEqualsType(ClientNotActiveException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void clientWasNotActiveAtExecute() {
        // given
        HttpClientProxy sut = HttpClientProxy.builder().build();

        PostRequest request = new PostRequest()
            .setConnectTimeout(15000)
            .setEntity(String.class)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet");

        // when
        Throwable e = Asserts.assertThrows(() -> sut.execute(request, new RequestCallback<ResponseEntity>() {
            public void onException(Exception e) {
            }

            public void onResponse(ResponseEntity response) {
            }
        }));

        // then
        Asserts.assertThat(e).isEqualsType(ClientNotActiveException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void maxCapacity() {
        // given
        HttpClientProxy sut = HttpClientProxy.builder()
            .autoStart(true)
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        PostRequest request = new PostRequest()
            .setConnectTimeout(15000)
            .setEntity(String.class)
            .setResponseType(TestResponse.class)
            .setResponseType(StatusCodes.OK, TestResponse.class)
            .setURL("http://127.0.0.1:18081/greet");

        // when
        AtomicReference<Throwable> ref = new AtomicReference<>();
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                while (ref.get() == null) {
                    try {
                        sut.call(request, 150000);
                    } catch (Exception e) {
                        ref.set(e);
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        while (ref.get() == null) {
            EzyThreads.sleep(3);
        }

        // then
        Asserts.assertThat(ref.get()).isEqualsType(RequestQueueFullException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToFileTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        // when
        String fileName = sut.download(fileUrl, new File("test-output/no-commit"));

        // then
        Asserts.assertEquals(fileName, "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/ezy-settings-1.0.0.xsd").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToFileWithCancellationTokenTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        // when
        String fileName = sut.download(
            fileUrl,
            new File("test-output/no-commit"),
            new DownloadCancellationToken()
        );

        // then
        Asserts.assertEquals(fileName, "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/ezy-settings-1.0.0.xsd").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToFileButCancelTest() {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        // when
        DownloadCancellationToken cancellationToken = new DownloadCancellationToken();
        cancellationToken.cancel();
        Throwable e = Asserts.assertThrows(() ->
            sut.download(
                fileUrl,
                new File("test-output/no-commit"),
                cancellationToken
            ));

        // then
        Asserts.assertEqualsType(e, DownloadCancelledException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToFileByRequestTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";
        DownloadRequest request = new DownloadRequest()
            .setFileURL(fileUrl)
            .setConnectTimeout(5000)
            .setReadTimeout(5000)
            .setHeaders(
                MultiValueMap.builder()
                    .setValue("hello", "world")
                    .build()
            );

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        // when
        String fileName = sut.download(request, new File("test-output/no-commit"));

        // then
        Asserts.assertEquals(fileName, "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/ezy-settings-1.0.0.xsd").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToFileByRequestWithCancellationTokenTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";
        DownloadRequest request = new DownloadRequest()
            .setFileURL(fileUrl)
            .setConnectTimeout(5000)
            .setReadTimeout(5000)
            .setHeaders(
                MultiValueMap.builder()
                    .setValue("hello", "world")
                    .build()
            );

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        // when
        String fileName = sut.download(
            request,
            new File("test-output/no-commit"),
            new DownloadCancellationToken()
        );

        // then
        Asserts.assertEquals(fileName, "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/ezy-settings-1.0.0.xsd").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputStreamTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        File outFile = new File("test-output/no-commit/download-test.xml");
        EzyFileUtil.createFileIfNotExists(outFile);
        OutputStream outputStream = new FileOutputStream(outFile);

        // when
        sut.download(fileUrl, outputStream);

        // then
        Asserts.assertTrue(new File("test-output/no-commit/download-test.xml").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputStreamWithCancellationTokenTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        File outFile = new File("test-output/no-commit/download-test.xml");
        EzyFileUtil.createFileIfNotExists(outFile);
        OutputStream outputStream = new FileOutputStream(outFile);

        // when
        sut.download(fileUrl, outputStream, new DownloadCancellationToken());

        // then
        Asserts.assertTrue(new File("test-output/no-commit/download-test.xml").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputStreamButCancelTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        File outFile = new File("test-output/no-commit/download-test.xml");
        EzyFileUtil.createFileIfNotExists(outFile);
        OutputStream outputStream = new FileOutputStream(outFile);

        DownloadCancellationToken cancelledException = new DownloadCancellationToken();
        cancelledException.cancel();

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.download(fileUrl, outputStream, cancelledException)
        );

        // then
        Asserts.assertEqualsType(e, DownloadCancelledException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputStreamByRequestTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";
        DownloadRequest request = new DownloadRequest()
            .setFileURL(fileUrl)
            .setConnectTimeout(5000)
            .setReadTimeout(5000)
            .setHeaders(
                MultiValueMap.builder()
                    .setValue("hello", "world")
                    .build()
            );

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        File outFile = new File("test-output/no-commit/download-test.xml");
        EzyFileUtil.createFileIfNotExists(outFile);
        OutputStream outputStream = new FileOutputStream(outFile);

        // when
        sut.download(request, outputStream);

        // then
        Asserts.assertTrue(new File("test-output/no-commit/download-test.xml").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputStreamByRequestWithCancellationTokenTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";
        DownloadRequest request = new DownloadRequest()
            .setFileURL(fileUrl)
            .setConnectTimeout(5000)
            .setReadTimeout(5000)
            .setHeaders(
                MultiValueMap.builder()
                    .setValue("hello", "world")
                    .build()
            );

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        File outFile = new File("test-output/no-commit/download-test.xml");
        EzyFileUtil.createFileIfNotExists(outFile);
        OutputStream outputStream = new FileOutputStream(outFile);

        // when
        sut.download(request, outputStream, new DownloadCancellationToken());

        // then
        Asserts.assertTrue(new File("test-output/no-commit/download-test.xml").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputFileTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        String outFileName = RandomUtil.randomShortAlphabetString();

        // when
        DownloadFileResult result = sut.download(
            fileUrl,
            new File("test-output/no-commit"),
            outFileName
        );

        // then
        Asserts.assertEquals(result.getNewFileName(), outFileName + ".xsd");
        Asserts.assertEquals(result.getOriginalFileName(), "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/" + outFileName + ".xsd").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputFileWithCancelledTokenTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        String outFileName = RandomUtil.randomShortAlphabetString();

        // when
        DownloadFileResult result = sut.download(
            fileUrl,
            new File("test-output/no-commit"),
            outFileName,
            DownloadCancellationToken.ALWAYS_RUN
        );

        // then
        Asserts.assertEquals(result.getNewFileName(), outFileName + ".xsd");
        Asserts.assertEquals(result.getOriginalFileName(), "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/" + outFileName + ".xsd").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputFileButCancelTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        File outFile = new File("test-output/no-commit/download-test.xml");
        EzyFileUtil.createFileIfNotExists(outFile);
        String outFileName = RandomUtil.randomShortAlphabetString();

        DownloadCancellationToken cancelledException = new DownloadCancellationToken();
        cancelledException.cancel();

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.download(
                fileUrl,
                outFile.getParentFile(),
                outFileName,
                cancelledException
            )
        );

        // then
        Asserts.assertEqualsType(e, DownloadCancelledException.class);
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputFileWithRequestTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        String outFileName = RandomUtil.randomShortAlphabetString();

        // when
        DownloadFileResult result = sut.download(
            new DownloadRequest(fileUrl),
            new File("test-output/no-commit"),
            outFileName
        );

        // then
        Asserts.assertEquals(result.getNewFileName(), outFileName + ".xsd");
        Asserts.assertEquals(result.getOriginalFileName(), "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/" + outFileName + ".xsd").exists());
        sut.close();
        sut.stop();
    }

    @Test
    public void downloadToOutputFileWithRequestAndCancelledTokenTest() throws Exception {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd";

        HttpClientProxy sut = HttpClientProxy.builder()
            .requestQueueCapacity(1)
            .threadPoolSize(1)
            .build();

        String outFileName = RandomUtil.randomShortAlphabetString();

        // when
        DownloadFileResult result = sut.download(
            new DownloadRequest(fileUrl),
            new File("test-output/no-commit"),
            outFileName,
            DownloadCancellationToken.ALWAYS_RUN
        );

        // then
        Asserts.assertEquals(result.getNewFileName(), outFileName + ".xsd");
        Asserts.assertEquals(result.getOriginalFileName(), "ezy-settings-1.0.0.xsd");
        Asserts.assertTrue(new File("test-output/no-commit/" + outFileName + ".xsd").exists());
        sut.close();
        sut.stop();
    }

    private HttpClientProxy newClientProxy() {
        HttpClientProxy sut = HttpClientProxy.builder()
            .autoStart(true)
            .readTimeout(15000)
            .connectTimeout(15000)
            .setStringConverter(SingletonStringDeserializer.getInstance())
            .addBodyConverter(new TestBodyConverter())
            .addBodyConverters(Collections.singletonList(new TestBodyConverter()))
            .addBodyConverter("world", new TestBodyConverter())
            .addBodyConverters(Collections.singletonMap("foo", new TestBodyConverter()))
            .threadPoolSize(1)
            .requestQueueCapacity(10)
            .build();
        EzyProcessor.processWithLogException(sut::start);
        return sut;
    }

    @BeforeTest
    public void setUp() {
        TestApplicationBootstrap.getInstance().start();
    }

    @Getter
    @AllArgsConstructor
    public static class TestRequest {
        private String who;
    }

    @Data
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestResponse {
        private String message;
    }

    @BodyConvert("hello")
    public static class TestBodyConverter implements BodyConverter {

        @Override
        public byte[] serialize(Object body) {
            return body.toString().getBytes();
        }
    }
}
