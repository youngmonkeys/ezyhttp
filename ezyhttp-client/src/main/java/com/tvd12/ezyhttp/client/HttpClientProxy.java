package com.tvd12.ezyhttp.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.concurrent.EzyFuture;
import com.tvd12.ezyfox.concurrent.EzyFutureConcurrentHashMap;
import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyfox.concurrent.EzyFutureTask;
import com.tvd12.ezyfox.concurrent.EzyThreadList;
import com.tvd12.ezyfox.util.EzyCloseable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyStartable;
import com.tvd12.ezyfox.util.EzyStoppable;
import com.tvd12.ezyhttp.client.callback.RequestCallback;
import com.tvd12.ezyhttp.client.concurrent.RequestFutureTask;
import com.tvd12.ezyhttp.client.exception.ClientNotActiveException;
import com.tvd12.ezyhttp.client.exception.RequestQueueFullException;
import com.tvd12.ezyhttp.client.request.Request;
import com.tvd12.ezyhttp.client.request.RequestQueue;
import com.tvd12.ezyhttp.core.concurrent.HttpThreadFactory;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

public class HttpClientProxy 
		extends EzyLoggable
		implements EzyStartable, EzyStoppable, EzyCloseable {

	protected EzyThreadList threadList;
	protected volatile boolean active;
	protected final HttpClient client;
	protected final int threadPoolSize;
	protected final RequestQueue requestQueue;
	protected final EzyFutureMap<Request> futures;
	
	public HttpClientProxy(
			int threadPoolSize,
			int requestQueueCapacity, HttpClient client) {
		this.client = client;
		this.threadPoolSize = threadPoolSize;
		this.futures = new EzyFutureConcurrentHashMap<>();
		this.requestQueue = new RequestQueue(requestQueueCapacity);
	}
	
	@Override
	public void start() throws Exception {
		this.active = true;
		this.threadList = new EzyThreadList(
				threadPoolSize, 
				() -> loop(), HttpThreadFactory.create("client"));
		this.threadList.execute();
	}
	
	@Override
	public void stop() {
		this.active = false;
	}
	
	@Override
	public void close() throws IOException {
		this.active = false;
		this.requestQueue.clear();
		Map<Request, EzyFuture> undoneTasks = futures.clear();
		for(Request undoneRequest : undoneTasks.keySet()) {
			EzyFuture undoneTask = undoneTasks.get(undoneRequest);
			undoneTask.cancel("HttpClientProxy close, request to: " + undoneRequest.getURL() + " has cancelled");
		}
		this.threadList.interrupt();
	}
	
	protected void loop() {
		while(active) {
			handleRequests();
		}
	}
	
	protected void handleRequests() {
		EzyFuture future = null;
		try {
			Request request = requestQueue.take();
			future = futures.removeFuture(request);
			ResponseEntity response = client.request(request);
			future.setResult(response);
		}
		catch (Exception e) {
			if(future != null)
				future.setException(e);
		}
	}
	
	public <T> T call(Request request, int timeout) throws Exception {
		ResponseEntity entity = request(request, timeout);
		return client.getResponseBody(entity);
	}
	
	public ResponseEntity request(Request request, int timeout) throws Exception {
		EzyFuture future = new EzyFutureTask();
		addRequest(request);
		futures.addFuture(request, future);
		ResponseEntity response = future.get(timeout);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void fire(Request request, RequestCallback callback) {
		execute(request, new RequestCallback<ResponseEntity>() {
			@Override
			public void onResponse(ResponseEntity response) {
				try {
					Object body = response.getBody();
					callback.onResponse(body);
				}
				catch (Exception e) {
					onException(e);
				}
			}
			@Override
			public void onException(Exception e) {
				callback.onResponse(e);
			}
		});
	}
	
	public void execute(Request request, RequestCallback<ResponseEntity> callback) {
		EzyFuture future = new RequestFutureTask(callback);
		addRequest(request);
		futures.addFuture(request, future);
	}
	
	protected void addRequest(Request request) {
		if(!active)
			throw new ClientNotActiveException();
		if(!requestQueue.add(request))
			throw new RequestQueueFullException(requestQueue.getCapacity());
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<HttpClientProxy> {
		protected int threadPoolSize;
		protected int requestQueueCapacity;
		protected HttpClient.Builder clientBuilder;
		
		public Builder() {
			this.threadPoolSize = 16;
			this.requestQueueCapacity = 10000;
			this.clientBuilder = HttpClient.builder();
			
		}
		
		public Builder readTimeout(int readTimeout) {
			clientBuilder.readTimeout(readTimeout);
			return this;
		}
		
		public Builder connectTimeout(int connectTimeout) {
			clientBuilder.connectTimeout(connectTimeout);
			return this;
		}
		
		public Builder setStringConverter(Object converter) {
			clientBuilder.setStringConverter(converter);
			return this;
		}
		
		public Builder addBodyConverter(Object converter) {
			clientBuilder.addBodyConverter(converter);
			return this;
		}
		
		public Builder addBodyConverters(List<?> converters) {
			clientBuilder.addBodyConverters(converters);
			return this;
		}
		
		public Builder threadPoolSize(int threadPoolSize) {
			this.threadPoolSize = threadPoolSize;
			return this;
		}
		
		protected Builder requestQueueCapacity(int requestQueueCapacity) {
			this.requestQueueCapacity = requestQueueCapacity;
			return this;
		}
		
		@Override
		public HttpClientProxy build() {
			HttpClientProxy proxy = new HttpClientProxy(
					threadPoolSize,
					requestQueueCapacity,
					clientBuilder.build()
			);
			return proxy;
		}
	}
	
}
