package com.tvd12.ezyhttp.client.concurrent;

import com.tvd12.ezyfox.concurrent.EzyFutureTask;
import com.tvd12.ezyhttp.client.callback.RequestCallback;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RequestFutureTask extends EzyFutureTask {

    protected final RequestCallback callback;

    public RequestFutureTask() {
        this(null);
    }

    public RequestFutureTask(RequestCallback callback) {
        this.callback = callback;
    }

    @Override
    public void setResult(Object result) {
        if (result == null)
            throw new NullPointerException("result is null");
        if (callback != null)
            callback.onResponse(result);
        synchronized (this) {
            this.result = result;
            notify();
        }
    }

    @Override
    public void setException(Exception exception) {
        if (exception == null)
            throw new NullPointerException("exception is null");
        if (callback != null)
            callback.onException(exception);
        synchronized (this) {
            this.exception = exception;
            notify();
        }
    }

}
