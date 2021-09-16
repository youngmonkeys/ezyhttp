package com.tvd12.ezyhttp.client.test;

import java.net.URI;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.GetRequest;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.client.test.request.HelloRequest;
import com.tvd12.ezyhttp.client.test.server.TestApplicationBootstrap;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.test.assertion.Asserts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HttpClientTest {

	public static void main(String[] args) throws Exception {
		postTest();
	}
	
	protected static void getTest() throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		GetRequest request = new GetRequest()
				.setURL("http://localhost:8081/bye?messages=a,b,c&numbers=1,2,3")
				.setEntity(null)
				.setResponseType(String.class)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request);
		System.out.println(response);
	}
	
	protected static void postTest() throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		HelloRequest body = new HelloRequest();
		body.setWho("dzung");
		RequestEntity entity = RequestEntity.body(body);
		PostRequest request = new PostRequest()
				.setURL("http://localhost:8081/")
				.setEntity(entity)
				.setResponseType(String.class)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request);
		System.out.println(response);
	}
	
	@Test
	public void callTest() throws Exception {
		// given
		HttpClient sut = HttpClient.builder()
				.objectMapper(new Object())
				.objectMapper(new ObjectMapper())
				.build();
		
		PostRequest request = new PostRequest()
				.setConnectTimeout(-1)
				.setReadTimeout(15000)
				.setEntity(
					RequestEntity.builder()
						.body(new TestRequest("Monkey"))
						.header("hello", "world")
						.header("foo", "bar")
						.build()
				)
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/greet"));
		
		// when
		TestResponse actual = sut.call(request);
		
		// then
		TestResponse expectation = new TestResponse("Greet Monkey!");
		Asserts.assertEquals(expectation, actual);
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
}
