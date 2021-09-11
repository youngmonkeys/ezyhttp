package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.PostRequest;

public class V018HttpClientTest {

	public static void main(String[] args) throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		PostRequest loveRequest = new PostRequest()
				.setURL("http://localhost:8083/love");
		client.call(loveRequest);
	}
	
}
