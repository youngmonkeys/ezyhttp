package com.tvd12.ezyhttp.server.core.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.tvd12.ezyfox.concurrent.EzyFuture;
import com.tvd12.ezyfox.concurrent.EzyFutureConcurrentHashMap;
import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyfox.concurrent.EzyThreadList;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyStoppable;
import com.tvd12.ezyhttp.core.concurrent.HttpThreadFactory;
import com.tvd12.ezyhttp.server.core.exception.MaxResourceUploadCapacity;

import lombok.AllArgsConstructor;

public class ResourceUploadManager
		extends EzyLoggable
		implements EzyStoppable, EzyDestroyable {

	protected volatile boolean active;
	protected final int capacity;
	protected final int threadPoolSize;
	protected final int bufferSize;
	protected final EzyThreadList threadList;
	protected final BlockingQueue<Entry> queue;
	protected final EzyFutureMap<Entry> futureMap;
	protected final static Entry POISON = new Entry();
	
	public static final int DEFAULT_CAPACITY = 100000;
	public static final int DEFAULT_THREAD_POOL_SIZE = 16;
	public static final int DEFAULT_BUFFER_SIZE = 1024;
	
	public ResourceUploadManager() {
		this(
			DEFAULT_CAPACITY, 
			DEFAULT_THREAD_POOL_SIZE, 
			DEFAULT_BUFFER_SIZE
		);
	}
	
	public ResourceUploadManager(
			int capacity, 
			int threadPoolSize, int bufferSize) {
		this.capacity = capacity;
		this.threadPoolSize = threadPoolSize;
		this.bufferSize = bufferSize;
		this.queue = new LinkedBlockingQueue<>(capacity);
		this.futureMap = new EzyFutureConcurrentHashMap<>();
		this.threadList = newThreadList(threadPoolSize);
		this.start(threadPoolSize);
	}
	
	private EzyThreadList newThreadList(int threadPoolSize) {
		return new EzyThreadList(
				threadPoolSize, 
				() -> loop(), 
				HttpThreadFactory.create("upload-manager"));
	}
	
	private void start(int threadPoolSize) {
		this.active = true;
		this.threadList.execute();
	}
	
	private void loop() {
		byte[] buffer = new byte[bufferSize];
		while(active) {
			try {
				Entry entry = queue.take();
				InputStream inputStream = entry.inputStream;
				OutputStream outputStream = entry.outputStream;
				boolean done = false;
				IOException exception = null;
				while(true) {
					int read = inputStream.read(buffer);
					if(read <= 0) {
						done = true;
						break;
					}
					try {
						outputStream.write(buffer, 0, read);
					}
					catch (IOException e) {
						done = true;
						exception = e;
						break;
					}
					queue.offer(entry);
					break;
				}
				if(done) {
					EzyFuture future = futureMap.removeFuture(entry);
					if(exception == null)
						future.setResult(Boolean.TRUE);
					else
						future.setException(exception);
				}
			}
			catch (Throwable e) {
				logger.debug("download error", e);
			}
		}
	}
	
	public void drain(InputStream from, OutputStream to) throws Exception {
		Entry entry = new Entry(from, to);
		EzyFuture future = futureMap.putFuture(entry);
		boolean success = this.queue.offer(entry);
		if(!success) {
			futureMap.removeFuture(entry);
			throw new MaxResourceUploadCapacity(capacity);
		}
		future.get();
	}
	
	@Override
	public void stop() {
		this.active = false;
		for(int i = 0 ; i < threadPoolSize ; ++i) {
			queue.offer(POISON);
		}
	}
	
	@Override
	public void destroy() {
		this.stop();
	}

	@AllArgsConstructor
	private static class Entry {
		private final InputStream inputStream;
		private final OutputStream outputStream;
		
		public Entry() {
			this(null, null);
		}
	}
}
