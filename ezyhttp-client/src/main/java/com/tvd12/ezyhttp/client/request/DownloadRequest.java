package com.tvd12.ezyhttp.client.request;

import java.net.URI;
import java.net.URL;

import com.tvd12.ezyhttp.core.data.MultiValueMap;

import lombok.Getter;

@Getter
public class DownloadRequest {

    protected String fileURL;
    protected int readTimeout;
    protected int connectTimeout;
    protected MultiValueMap headers;
    
    public DownloadRequest() {}
    
    public DownloadRequest(String fileURL) {
        this.fileURL = fileURL;
    }
    
    public DownloadRequest setFileURL(String url) {
        this.fileURL = url;
        return this;
    }
    
    public DownloadRequest setFileURL(URI uri) {
        return setFileURL(uri.toString());
    }
    
    public DownloadRequest setFileURL(URL url) {
        return setFileURL(url.toString());
    }
    
    public DownloadRequest setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
    
    public DownloadRequest setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }
    
    public DownloadRequest setHeaders(MultiValueMap headers) {
        this.headers = headers;
        return this;
    }
}
