package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import lombok.Getter;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Getter
public class UploadRequest {

    protected String url;
    protected HttpMethod method;
    protected String fileName;
    protected String filePath;
    protected InputStream inputStream;
    protected int readTimeout;
    protected int connectTimeout;
    protected MultiValueMap headers;
    protected Map<Integer, Class<?>> responseTypes;

    public UploadRequest() {
        this.method = HttpMethod.POST;
        this.responseTypes = new HashMap<>();
    }

    public String getURL() {
        return this.url;
    }

    public UploadRequest setURL(URI uri) {
        this.url = uri.toString();
        return this;
    }

    public UploadRequest setURL(URL url) {
        this.url = url.toString();
        return this;
    }

    public UploadRequest setURL(String url) {
        this.url = url;
        return this;
    }

    public UploadRequest setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public UploadRequest setMethod(HttpMethod method) {
        if (method != null) {
            this.method = method;
        }
        return this;
    }

    public UploadRequest setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public UploadRequest setFilePath(String url) {
        this.filePath = url;
        return this;
    }

    public UploadRequest setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public UploadRequest setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public UploadRequest setHeaders(MultiValueMap headers) {
        this.headers = headers;
        return this;
    }

    public UploadRequest setResponseType(Class<?> responseType) {
        return setResponseType(StatusCodes.OK, responseType);
    }

    public UploadRequest setResponseType(int statusCode, Class<?> responseType) {
        this.responseTypes.put(statusCode, responseType);
        return this;
    }
}
