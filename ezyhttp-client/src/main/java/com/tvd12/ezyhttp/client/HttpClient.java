package com.tvd12.ezyhttp.client;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.client.request.Request;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.codec.BodySerializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.Headers;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.exception.*;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClient extends EzyLoggable {

    public static final int NO_TIMEOUT = -1;
    protected final int defaultReadTimeout;
    protected final int defaultConnectTimeout;
    protected final DataConverters dataConverters;

    protected HttpClient(Builder builder) {
        this.defaultReadTimeout = builder.readTimeout;
        this.defaultConnectTimeout = builder.connectTimeout;
        this.dataConverters = builder.dataConverters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public <T, R> R call(Request<T> request) throws Exception {
        ResponseEntity<R> response = this.request(
                request.getMethod(),
                request.getURL(),
                request.getEntity(),
                request.getResponseTypes(),
                request.getConnectTimeout(),
                request.getReadTimeout()
        );
        return getResponseBody(response);
    }

    public <T, R> ResponseEntity<R> request(
            HttpMethod method,
            String url,
            RequestEntity<T> entity,
            Map<Integer, Class<?>> responseTypes,
            int connectTimeout,
            int readTimeout)
            throws Exception {
        logger.debug("start: {} - {} - {}", method, url, entity.getHeaders());
        HttpURLConnection connection = connect(url);
        try {
            connection.setConnectTimeout(connectTimeout > 0 ? connectTimeout : defaultConnectTimeout);
            connection.setReadTimeout(readTimeout > 0 ? readTimeout : defaultReadTimeout);
            connection.setRequestMethod(method.toString());
            connection.setDoInput(true);
            connection.setDoOutput(method.hasOutput());
            connection.setInstanceFollowRedirects(method == HttpMethod.GET);
            MultiValueMap requestHeaders = entity.getHeaders();
            if (requestHeaders != null) {
                Map<String, String> encodedHeaders = requestHeaders.toMap();
                for (Entry<String, String> requestHeader : encodedHeaders.entrySet())
                    connection.setRequestProperty(requestHeader.getKey(), requestHeader.getValue());
            }
            Object requestBody = null;
            if (method != HttpMethod.GET) {
                requestBody = entity.getBody();
            }
            byte[] requestBodyBytes = null;
            if (requestBody != null) {
                String requestContentType = connection.getRequestProperty(Headers.CONTENT_TYPE);
                if (requestContentType == null) {
                    requestContentType = ContentTypes.APPLICATION_JSON;
                    connection.setRequestProperty(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
                }
                requestBodyBytes = serializeRequestBody(requestContentType, requestBody);
                int requestContentLength = requestBodyBytes.length;
                connection.setFixedLengthStreamingMode(requestContentLength);
            }

            connection.connect();

            if (requestBody != null) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBodyBytes);
                outputStream.flush();
                outputStream.close();
            }

            int responseCode = connection.getResponseCode();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            MultiValueMap responseHeaders = MultiValueMap.of(headerFields);
            String responseContentType = responseHeaders.getValue(Headers.CONTENT_TYPE);
            if (responseContentType == null)
                responseContentType = ContentTypes.APPLICATION_JSON;
            InputStream inputStream = connection.getErrorStream();
            if (inputStream == null)
                inputStream = connection.getInputStream();
            R responseBody = null;
            if (inputStream != null) {
                try {
                    int responseContentLength = connection.getContentLength();
                    Class<?> responseType = responseTypes.get(responseCode);
                    responseBody =  deserializeResponseBody(
                            responseContentType, responseContentLength, inputStream, responseType);
                } finally {
                    inputStream.close();
                }
            }
            logger.debug("end: {} - {} - {} - {}", method, url, responseCode, responseHeaders);
            return new ResponseEntity<>(responseCode, responseHeaders, responseBody);
        } finally {
            connection.disconnect();
        }
    }

    public HttpURLConnection connect(String url) throws Exception {
        URL requestURL = new URL(url);
        return (HttpURLConnection) requestURL.openConnection();
    }

    protected byte[] serializeRequestBody(
            String contentType, Object requestBody) throws IOException {
        BodySerializer serializer = dataConverters.getBodySerializer(contentType);
        if (serializer == null)
            throw new IOException("has no serializer for: " + contentType);
        return serializer.serialize(requestBody);
    }

    @SuppressWarnings("unchecked")
    protected <R> R deserializeResponseBody(
            String contentType,
            int contentLength,
            InputStream inputStream, Class<?> responseType) throws IOException {
        BodyDeserializer deserializer = dataConverters.getBodyDeserializer(contentType);
        if (deserializer == null)
            throw new IOException("has no deserializer for: " + contentType);
        R body;
        if (responseType != null) {
            if (responseType == String.class)
                body = (R) deserializer.deserializeToString(inputStream, contentLength);
            else
                body = (R) deserializer.deserialize(inputStream, responseType);
        } else {
            body = (R) deserializer.deserializeToString(inputStream, contentLength);
            if (body != null) {
                try {
                    body = (R) deserializer.deserialize((String) body, Map.class);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        return body;
    }

    public <R> R getResponseBody(ResponseEntity<R> entity) {
        int statusCode = entity.getStatus();
        R body = entity.getBody();
        if (statusCode < 400)
            return body;
        if (statusCode == StatusCodes.BAD_REQUEST)
            throw new HttpBadRequestException(body);
        if (statusCode == StatusCodes.NOT_FOUND)
            throw new HttpNotFoundException(body);
        if (statusCode == StatusCodes.UNAUTHORIZED)
            throw new HttpUnauthorizedException(body);
        if (statusCode == StatusCodes.FORBIDDEN)
            throw new HttpForbiddenException(body);
        if (statusCode == StatusCodes.METHOD_NOT_ALLOWED)
            throw new HttpMethodNotAllowedException(body);
        if (statusCode == StatusCodes.NOT_ACCEPTABLE)
            throw new HttpNotAcceptableException(body);
        if (statusCode == StatusCodes.REQUEST_TIMEOUT)
            throw new HttpRequestTimeoutException(body);
        if (statusCode == StatusCodes.CONFLICT)
            throw new HttpConflictException(body);
        if (statusCode == StatusCodes.UNSUPPORTED_MEDIA_TYPE)
            throw new HttpUnsupportedMediaTypeException(body);
        if (statusCode == StatusCodes.INTERNAL_SERVER_ERROR)
            throw new HttpInternalServerErrorException(body);
        throw new HttpRequestException(statusCode, body);
    }

    public static class Builder implements EzyBuilder<HttpClient> {

        protected int readTimeout;
        protected int connectTimeout;
        protected DataConverters dataConverters;

        public Builder() {
            this.readTimeout = 15 * 1000;
            this.connectTimeout = 15 * 1000;
            this.dataConverters = new DataConverters();
        }

        public void readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        public void connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public void setStringConverter(Object converter) {
            this.dataConverters.setStringConverter(converter);
        }

        public void addBodyConverter(Object converter) {
            this.dataConverters.addBodyConverter(converter);
        }

        public void addBodyConverters(List<?> converters) {
            this.dataConverters.addBodyConverters(converters);
        }

        @Override
        public HttpClient build() {
            return new HttpClient(this);
        }
    }

}
