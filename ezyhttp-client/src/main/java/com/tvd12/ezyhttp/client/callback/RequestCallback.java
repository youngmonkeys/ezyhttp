package com.tvd12.ezyhttp.client.callback;

public interface RequestCallback<T> {

    void onResponse(T response);

    void onException(Exception e);
}
