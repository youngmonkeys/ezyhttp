package com.tvd12.ezyhttp.client.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyfox.exception.BadRequestException;
import com.tvd12.ezyfox.util.EzyProcessor;
import com.tvd12.ezyfox.util.EzyWrap;
import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.HttpClientProxy;
import com.tvd12.ezyhttp.client.callback.RequestCallback;
import com.tvd12.ezyhttp.client.exception.ClientNotActiveException;
import com.tvd12.ezyhttp.client.exception.RequestQueueFullException;
import com.tvd12.ezyhttp.client.request.DeleteRequest;
import com.tvd12.ezyhttp.client.request.GetRequest;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.PutRequest;
import com.tvd12.ezyhttp.client.request.Request;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.client.request.RequestQueue;
import com.tvd12.ezyhttp.client.test.request.HelloRequest;
import com.tvd12.ezyhttp.client.test.server.TestApplicationBootstrap;
import com.tvd12.ezyhttp.core.annotation.BodyConvert;
import com.tvd12.ezyhttp.core.codec.BodyConverter;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HttpClientProxyTest {

	public static void main(String[] args) throws Exception {
		HttpClientProxy client = HttpClientProxy.builder()
				.build();
		client.start();
		postTest(client);
		new Thread(() -> {
			for(int i = 0 ; i < 100 ; ++i) {
				try {
					postTest(client);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		Thread.sleep(3);
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
	public void fireExceptionTest() throws Exception {
		// given
		HttpClientProxy sut = newClientProxy();
		
		GetRequest request = new GetRequest()
				.setConnectTimeout(15000)
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL("http://127.0.0.1:18081/greet");
		
		// when
		CountDownLatch countDownLatch = new CountDownLatch(1);
		EzyWrap<Exception> wrap = new EzyWrap<>();
		sut.fire(request, new RequestCallback<TestResponse>() {
			@Override
			public void onResponse(TestResponse response) {
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
	public void excecuteJsonTest() throws Exception {
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
	public void excecuteExceptionTest() throws Exception {
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
	public void postWithExceptionTest() throws Exception {
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
		TestResponse actual = sut.call(request, 15000);
		
		// then
		TestResponse expectation = new TestResponse("Greet Monkey!");
		Asserts.assertEquals(expectation, actual);
		sut.close();
		sut.stop();
	}
	
	@Test
	public void startTest() throws Exception {
		// given
		HttpClientProxy proxy = HttpClientProxy.builder().build();
		
		// when
		proxy.start();
		
		// then
		proxy.stop();
	}
	
	@Test
	public void closeWithRemainTasks() throws Exception {
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
	public void clientWasNotActive() throws Exception {
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
	public void clientWasNotActiveAtExecute() throws Exception {
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
			};
			public void onResponse(ResponseEntity response) {
			};
		}));
		
		// then
		Asserts.assertThat(e).isEqualsType(ClientNotActiveException.class);
		sut.close();
		sut.stop();
	}
	
	@Test
	public void maxCapacity() throws Exception {
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
		
		RequestQueue queue = FieldUtil.getFieldValue(sut, "requestQueue");
		for(int i = 0 ; i < 1000; ++i)
			queue.add(request);
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request, 150000));
		
		// then
		Asserts.assertThat(e).isEqualsType(RequestQueueFullException.class);
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
				.addBodyConverters(Arrays.asList(new TestBodyConverter()))
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
		public byte[] serialize(Object body) throws IOException {
			return body.toString().getBytes();
		}
		
	}
}
