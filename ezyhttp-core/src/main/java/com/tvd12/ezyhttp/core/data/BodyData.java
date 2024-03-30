package com.tvd12.ezyhttp.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface BodyData {

    Map<String, String> getHeaders();

    Map<String, String> getParameters();

    String getContentType();

    String getRequestContentType();

    int getContentLength();

    InputStream getInputStream() throws IOException;
}
