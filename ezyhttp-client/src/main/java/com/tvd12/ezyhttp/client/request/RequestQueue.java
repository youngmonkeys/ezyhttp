package com.tvd12.ezyhttp.client.request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.tvd12.ezyfox.util.EzyClearable;

import lombok.Getter;

public class RequestQueue implements EzyClearable {

    @Getter
    protected final int capacity;
    protected final BlockingQueue<Request> queue;
    
    public RequestQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }
    
    public boolean add(Request request) {
        return this.queue.offer(request);
    }
    
    public Request take() throws InterruptedException {
        return queue.take();
    }
    
    @Override
    public void clear() {
        this.queue.clear();
    }
    
}
