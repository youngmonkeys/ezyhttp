package com.tvd12.ezyhttp.client.test;

import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.client.test.request.Customer;

public class CustomerApisTest {

	public static void main(String[] args) throws Exception {
		addCustomerTest();
	}
	
	protected static void addCustomerTest() throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		Customer body = new Customer();
		body.setName("dung");
		body.setAge(28);
		RequestEntity entity = RequestEntity.of(body)
				.header("token", "123")
				.build();
		PostRequest request = new PostRequest()
				.setURL("http://localhost:8081/api/v1/customer/add")
				.setEntity(entity)
				.setResponseType(String.class)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request);
		System.out.println(response);
	}
	
}
