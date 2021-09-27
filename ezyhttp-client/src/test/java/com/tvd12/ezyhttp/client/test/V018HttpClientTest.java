package com.tvd12.ezyhttp.client.test;

import java.util.List;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.DeleteRequest;
import com.tvd12.ezyhttp.client.request.GetRequest;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.RequestEntity;

public class V018HttpClientTest {

	public static void main(String[] args) throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		PostRequest loveRequest = new PostRequest()
				.setURL("http://localhost:8083/love");
		System.out.println(client.request(loveRequest));
		
		DeleteRequest deleteRequest = new DeleteRequest()
				.setURL("http://localhost:8083/api/v1/customer/delete")
				.setEntity(
					RequestEntity.builder()
						.header("token", "123456")
						.build()
				);
		System.out.println(client.request(deleteRequest));
		System.out.println(client.request(deleteRequest).getBody().toString());
		
		GetRequest textRequest = new GetRequest()
				.setURL("http://localhost:8083/text");
		System.out.println((String)client.call(textRequest));
		
		GetRequest listRequest = new GetRequest()
				.setResponseType(List.class)
				.setURL("http://localhost:8083/list");
		System.out.println(client.call(listRequest).toString());
	}
	
}
