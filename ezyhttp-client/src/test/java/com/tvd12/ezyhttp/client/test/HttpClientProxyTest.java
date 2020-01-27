package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.HttpClientProxy;
import com.tvd12.ezyhttp.client.request.GetRequest;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.client.test.request.HelloRequest;

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
	
}
