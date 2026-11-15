package com.tvd12.ezyhttp.server.core.response;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class NoBodyHttpServletResponse extends HttpServletResponseWrapper {

    private PrintWriter writer;
    private final ServletOutputStream outputStream;

    public NoBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
        this.outputStream = new NoBodyServletOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (writer == null) {
            writer = new PrintWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
            );
        }
        return writer;
    }

    private static class NoBodyServletOutputStream extends ServletOutputStream {

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {}

        @Override
        public void write(int b) {}

        @Override
        public void write(byte[] b, int off, int len) {}
    }
}
