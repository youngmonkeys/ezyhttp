package com.tvd12.ezyhttp.server.core.resources;

public interface FileUploadCallback {

    void onSuccess();

    void onFailure(Exception e);
}
