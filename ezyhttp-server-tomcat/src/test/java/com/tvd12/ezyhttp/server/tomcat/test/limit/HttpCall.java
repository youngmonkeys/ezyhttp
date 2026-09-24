package com.tvd12.ezyhttp.server.tomcat.test.limit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public final class HttpCall {

    public static final String BOUNDARY = "----ezyhttp-limit-test";

    private HttpCall() {}

    public static Response get(
        String url,
        Map<String, String> headers
    ) throws IOException {
        HttpURLConnection connection = open(url, "GET", headers);
        return readResponse(connection);
    }

    public static Response post(
        String url,
        String contentType,
        byte[] body,
        boolean chunked
    ) throws IOException {
        return post(url, contentType, body, chunked, Collections.emptyMap());
    }

    public static Response post(
        String url,
        String contentType,
        byte[] body,
        boolean chunked,
        Map<String, String> headers
    ) throws IOException {
        HttpURLConnection connection = open(url, "POST", headers);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType);
        if (chunked) {
            connection.setChunkedStreamingMode(256);
        } else {
            connection.setFixedLengthStreamingMode(body.length);
        }
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body);
        } catch (IOException e) {
            return readResponseOrClosed(connection);
        }
        return readResponse(connection);
    }

    public static byte[] multipart(
        Map<String, String> fields,
        String fileField,
        byte[] fileContent
    ) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (Map.Entry<String, String> field : fields.entrySet()) {
            write(body, "--" + BOUNDARY + "\r\n");
            write(
                body,
                "Content-Disposition: form-data; name=\"" + field.getKey() + "\"\r\n\r\n"
            );
            write(body, field.getValue() + "\r\n");
        }
        write(body, "--" + BOUNDARY + "\r\n");
        write(
            body,
            "Content-Disposition: form-data; name=\"" + fileField +
                "\"; filename=\"avatar.png\"\r\n"
        );
        write(body, "Content-Type: application/octet-stream\r\n\r\n");
        body.write(fileContent, 0, fileContent.length);
        write(body, "\r\n--" + BOUNDARY + "--\r\n");
        return body.toByteArray();
    }

    public static String multipartContentType() {
        return "multipart/form-data; boundary=" + BOUNDARY;
    }

    public static byte[] bytes(int size) {
        byte[] answer = new byte[size];
        for (int i = 0; i < size; ++i) {
            answer[i] = (byte) ('a' + (i % 26));
        }
        return answer;
    }

    public static String repeat(char ch, int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            builder.append(ch);
        }
        return builder.toString();
    }

    private static HttpURLConnection open(
        String url,
        String method,
        Map<String, String> headers
    ) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        connection.setUseCaches(false);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        return connection;
    }

    private static Response readResponseOrClosed(
        HttpURLConnection connection
    ) {
        try {
            return readResponse(connection);
        } catch (IOException e) {
            return new Response(Response.CONNECTION_CLOSED, "");
        }
    }

    private static Response readResponse(
        HttpURLConnection connection
    ) throws IOException {
        int status = connection.getResponseCode();
        InputStream inputStream = status >= 400
            ? connection.getErrorStream()
            : connection.getInputStream();
        String body = "";
        if (inputStream != null) {
            try (InputStream in = inputStream) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                body = new String(out.toByteArray(), StandardCharsets.UTF_8);
            }
        }
        connection.disconnect();
        return new Response(status, body);
    }

    private static void write(ByteArrayOutputStream out, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        out.write(bytes, 0, bytes.length);
    }

    public static class Response {

        public static final int CONNECTION_CLOSED = -1;

        public final int status;
        public final String body;

        public Response(int status, String body) {
            this.status = status;
            this.body = body;
        }

        @Override
        public String toString() {
            return status + " " + body;
        }
    }
}
