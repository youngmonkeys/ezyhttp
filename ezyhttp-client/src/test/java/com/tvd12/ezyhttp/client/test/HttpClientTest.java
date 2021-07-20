package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.AbstractRequest;
import com.tvd12.ezyhttp.client.request.GetRequest;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.client.test.request.HelloRequest;

public class HttpClientTest {

	public static void main(String[] args) throws Exception {
		getTest();
	}
	
	protected static void getTest() throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		RequestEntity<?> entity = new RequestEntity<>();
		AbstractRequest<GetRequest, ?> request =  new GetRequest()
				.setURL("http://localhost:3000/profile")
				.setEntity(entity)
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
		RequestEntity<HelloRequest> entity = new RequestEntity<>( body);
		AbstractRequest<PostRequest<HelloRequest>, HelloRequest> request =  new PostRequest<HelloRequest>()
				.setURL("http://localhost:8081/")
				.setEntity(entity)
				.setResponseType(String.class)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request);
		System.out.println(response);
	}
	
}
