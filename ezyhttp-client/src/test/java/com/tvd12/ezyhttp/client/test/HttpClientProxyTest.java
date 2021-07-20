package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.HttpClientProxy;
import com.tvd12.ezyhttp.client.request.AbstractRequest;
import com.tvd12.ezyhttp.client.request.GetRequest;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.client.test.request.HelloRequest;

public class HttpClientProxyTest {

	public static void main(String[] args) throws Exception {
		HttpClientProxy<Object, String> client = new HttpClientProxy<>(HttpClientProxy.builder());
		client.start();
		getTest(client);
		new Thread(() -> {
			for(int i = 0 ; i < 1 ; ++i) {
				try {
					getTest(client);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		Thread.sleep(3);
		client.close();
	}
	
	protected static void getTest(HttpClientProxy<Object, String> client) throws Exception {
		RequestEntity<?> entity = new RequestEntity<>();
		AbstractRequest<GetRequest, Object> request =  new GetRequest()
				.setURL("http://localhost:3000/profile")
				.setResponseType(String.class)
				.setEntity(entity)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request, 1000);
		System.out.println(response);
	}
	
	protected static void postTest(HttpClientProxy<HelloRequest, String> client) throws Exception {
		HelloRequest body = new HelloRequest();
		body.setWho("dzung");
		RequestEntity<HelloRequest> entity = new RequestEntity<>(body);
		AbstractRequest<PostRequest<HelloRequest>, HelloRequest> request = new PostRequest<HelloRequest>()
				.setURL("http://localhost:8081/")
				.setEntity(entity)
				.setResponseType(String.class)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request, 1000);
		System.out.println(response);
	}
	
}
