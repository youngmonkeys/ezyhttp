package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

public class HttpClientTest {

	public static void main(String[] args) throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		ResponseEntity<String> response = client.request(
				HttpMethod.GET,
				"http://localhost:8081/bye?messages=a,b,c&numbers=1,2,3",
				null, 
				String.class, 
				HttpClient.NO_TIMEOUT, 
				HttpClient.NO_TIMEOUT);
		System.out.println(response);
	}
	
}
