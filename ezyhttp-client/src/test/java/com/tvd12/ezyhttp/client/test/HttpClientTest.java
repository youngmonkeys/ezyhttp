package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.GetRequest;

public class HttpClientTest {

	public static void main(String[] args) throws Exception {
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
	
}
