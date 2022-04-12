package com.tvd12.ezyhttp.core.resources;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import com.tvd12.ezyfox.concurrent.EzyCallableFutureTask;
import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.concurrent.EzyFuture;
import com.tvd12.ezyfox.concurrent.EzyFutureConcurrentHashMap;
import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyfox.concurrent.EzyFutureTask;
import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyStoppable;
import com.tvd12.ezyhttp.core.concurrent.HttpThreadFactory;
import com.tvd12.ezyhttp.core.exception.MaxResourceUploadCapacity;
import com.tvd12.ezyhttp.core.exception.MaxUploadSizeException;

public class ResourceUploadManager
		extends EzyLoggable
		implements EzyStoppable, EzyDestroyable {

	protected volatile boolean active;
	protected final int capacity;
	protected final int threadPoolSize;
	protected final int bufferSize;
	protected final ExecutorService executorService;
	protected final BlockingQueue<Entry> queue;
	protected final EzyFutureMap<Entry> futureMap;
	protected final static Entry POISON = new Entry();
	
	public static final int DEFAULT_CAPACITY = 100000;
	public static final int DEFAULT_BUFFER_SIZE = 1024;
	public static final int DEFAULT_TIMEOUT = 15 * 60 * 1000;
	public static final long UNLIMIT_UPLOAD_SIZE = -1;
	public static final int DEFAULT_THREAD_POOL_SIZE = 
	        Runtime.getRuntime().availableProcessors() * 2;
	
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
		this.executorService = newExecutorService(threadPoolSize);
		this.start(threadPoolSize);
	}
	
	private ExecutorService newExecutorService(int threadPoolSize) {
	    return EzyExecutors.newFixedThreadPool(
	        threadPoolSize,
	        HttpThreadFactory.create("upload-manager")
        );
	}
	
	private void start(int threadPoolSize) {
		this.active = true;
		for (int i = 0 ; i < threadPoolSize ; ++i) {
		    executorService.execute(this::loop);
		}
	}
	
	private void loop() {
		byte[] buffer = new byte[bufferSize];
		while(active) {
		    Entry entry = null;
            boolean done = true;
            boolean isMaxUploaded = false;
            Exception exception = null;
			try {
				entry = queue.take();
				if(entry == POISON) {
					break;
				}
				InputStream inputStream = entry.inputStream;
				OutputStream outputStream = entry.outputStream;
				int read = inputStream.read(buffer);
				if (entry.increaseUploadedSize(read)) {
                    if(read > 0) {
                        outputStream.write(buffer, 0, read);
                        done = false;
                    }
				} else {
				    isMaxUploaded = true;
				}
			}
			catch (Exception e) {
			    exception = e;
				logger.debug("upload error", e);
			}
			catch (Throwable e) {
			    exception = new IllegalStateException(e);
                logger.debug("upload fatal error", e);
			}
			if (entry == null) {
			    continue;
			}
			
			if (isMaxUploaded) {
			    exception = new MaxUploadSizeException(entry.maxUploadSize);
			}

            if(done) {
                EzyFuture future = futureMap.removeFuture(entry);
                if (future == null) {
                    continue;
                }
                if(exception != null) {
                    future.setException(exception);
                }
                else {
                    future.setResult(Boolean.TRUE);
                }
            }
            else {
                queue.offer(entry);
            }
		}
	}
	
	public void drain(
        InputStream from, 
        OutputStream to,
        long maxUploadSize
    ) throws Exception {
	    Entry entry = new Entry(from, to, maxUploadSize);
        EzyFuture future = new EzyFutureTask();
        drain(from, to, entry, future).get(DEFAULT_TIMEOUT);
	}
	
	public void drain(InputStream from, OutputStream to) throws Exception {
	    drain(from, to, UNLIMIT_UPLOAD_SIZE);
	}
	
	public EzyFuture drainAsync(
        InputStream from, 
        OutputStream to,
        long maxUploadSize,
        EzyResultCallback<Boolean> callback
    ) {
	    Entry entry = new Entry(from, to, maxUploadSize);
        EzyFuture future = new EzyCallableFutureTask(callback);
        return drain(from, to, entry, future);
	}
	
	public EzyFuture drainAsync(
        InputStream from, 
        OutputStream to,
        EzyResultCallback<Boolean> callback
    ) {
	    return drainAsync(from, to, UNLIMIT_UPLOAD_SIZE, callback);
	}
	
	private EzyFuture drain(
        InputStream from, 
        OutputStream to,
        Entry entry,
        EzyFuture future
    ) {
        futureMap.addFuture(entry, future);
        boolean success = this.queue.offer(entry);
        if(!success) {
            futureMap.removeFuture(entry);
            throw new MaxResourceUploadCapacity(capacity);
        }
        return future;
    }
	
	@Override
	public void stop() {
		this.active = false;
		for(int i = 0 ; i < threadPoolSize ; ++i) {
			queue.offer(POISON);
		}
		this.executorService.shutdown();
	}
	
	@Override
	public void destroy() {
		this.stop();
	}

	private static class Entry {
		private final InputStream inputStream;
		private final OutputStream outputStream;
		private final long maxUploadSize;
		private long currentUploadedSize;
		
		public Entry() {
			this(null, null, 0L);
		}
		
		public Entry(
	        InputStream inputStream,
	        OutputStream outputStream,
	        long maxUploadSize
        ) {
		    this.inputStream = inputStream;
		    this.outputStream = outputStream;
		    this.maxUploadSize = maxUploadSize;
		}
		
		public boolean increaseUploadedSize(int uploadedSize) {
		    this.currentUploadedSize += uploadedSize;
		    if (maxUploadSize <= 0) {
		        return true;
		    }
		    return currentUploadedSize <= maxUploadSize;
		}
	}
}
