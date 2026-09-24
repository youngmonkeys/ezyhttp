package com.tvd12.ezyhttp.server.tomcat.test;

import com.tvd12.ezyhttp.core.exception.HttpRequestException;
import com.tvd12.ezyhttp.server.tomcat.RequestBodySizeLimitFilter;
import com.tvd12.test.assertion.Asserts;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

public class RequestBodySizeLimitFilterTest {

    private static final String JSON = "application/json";
    private static final String UPLOAD = "multipart/form-data; boundary=----abc";
    private static final int PAYLOAD_TOO_LARGE = 413;
    private static final long UNKNOWN_LENGTH = -1L;
    private static final long HUGE = 10L * 1024 * 1024 * 1024;

    @Test
    public void nonHttpRequestPassesThroughUntouchedTest() throws Exception {
        RequestBodySizeLimitFilter sut = new RequestBodySizeLimitFilter(1024, 16 * 1024);
        ServletRequest request = mock(ServletRequest.class);
        when(request.getContentLengthLong()).thenReturn(HUGE);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        sut.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    public void disabledBodyLimitPassesNormalRequestUntouchedTest() throws Exception {
        Fixture fixture = new Fixture(-1, 16 * 1024, JSON, HUGE, new byte[0]);

        fixture.filter();

        Asserts.assertTrue(fixture.passedRequest() == fixture.request);
        verify(fixture.response, never()).setStatus(anyInt());
    }

    @Test
    public void disabledUploadLimitPassesUploadUntouchedTest() throws Exception {
        Fixture fixture = new Fixture(1024, -1, UPLOAD, HUGE, new byte[0]);

        fixture.filter();

        Asserts.assertTrue(fixture.passedRequest() == fixture.request);
        verify(fixture.response, never()).setStatus(anyInt());
    }

    @Test
    public void disabledBodyLimitStillRejectsOversizedUploadTest() throws Exception {
        Fixture fixture = new Fixture(-1, 16 * 1024, UPLOAD, 16 * 1024 + 1, new byte[0]);

        fixture.filter();

        fixture.verifyRejected();
    }

    @Test
    public void normalRequestOverBodyLimitRejectedBeforeReadingTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, 1025, new byte[0]);

        fixture.filter();

        fixture.verifyRejected();
        verify(fixture.request, never()).getInputStream();
    }

    @Test
    public void uploadOverUploadLimitRejectedBeforeReadingTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, UPLOAD, 16 * 1024 + 1, new byte[0]);

        fixture.filter();

        fixture.verifyRejected();
        verify(fixture.request, never()).getInputStream();
    }

    @Test
    public void uploadWithinUploadLimitPassedToContainerUnwrappedTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, UPLOAD, 8 * 1024, new byte[0]);

        fixture.filter();

        Asserts.assertTrue(fixture.passedRequest() == fixture.request);
        verify(fixture.response, never()).setStatus(anyInt());
    }

    @Test
    public void uploadContentTypeDetectedCaseInsensitivelyTest() throws Exception {
        Fixture fixture = new Fixture(
            1024,
            16 * 1024,
            "MULTIPART/FORM-DATA; boundary=xyz",
            8 * 1024,
            new byte[0]
        );

        fixture.filter();

        Asserts.assertTrue(fixture.passedRequest() == fixture.request);
    }

    @Test
    public void requestWithoutContentTypeUsesBodyLimitTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, null, 8 * 1024, new byte[0]);

        fixture.filter();

        fixture.verifyRejected();
    }

    @Test
    public void normalRequestAtBodyLimitPassedWithWatchedBodyTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, 1024, new byte[1024]);

        fixture.filter();

        ServletRequest passed = fixture.passedRequest();
        Asserts.assertTrue(passed != fixture.request);
        Asserts.assertTrue(((HttpServletRequestWrapper) passed).getRequest() == fixture.request);
        verify(fixture.response, never()).setStatus(anyInt());
    }

    @Test
    public void streamedBodyWithinLimitReadFullyTest() throws Exception {
        byte[] body = bytes(1024);
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH, body);
        fixture.filter();

        byte[] read = readAll(fixture.passedHttpRequest().getInputStream());

        Asserts.assertEquals(new String(read, StandardCharsets.ISO_8859_1),
            new String(body, StandardCharsets.ISO_8859_1));
    }

    @Test
    public void streamedBodyOverLimitStopsWithPayloadTooLargeTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH, bytes(1025));
        fixture.filter();
        InputStream inputStream = fixture.passedHttpRequest().getInputStream();

        Throwable error = Asserts.assertThrows(() -> readAll(inputStream));

        assertPayloadTooLarge(error);
    }

    @Test
    public void byteByByteReadingCannotBypassLimitTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH, bytes(1025));
        fixture.filter();
        InputStream inputStream = fixture.passedHttpRequest().getInputStream();

        for (int i = 0; i < 1024; ++i) {
            Asserts.assertTrue(inputStream.read() >= 0);
        }
        Throwable error = Asserts.assertThrows(inputStream::read);

        assertPayloadTooLarge(error);
    }

    @Test
    public void endOfBodyNotCountedAsDataTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH, bytes(1024));
        fixture.filter();
        InputStream inputStream = fixture.passedHttpRequest().getInputStream();
        readAll(inputStream);

        Asserts.assertEquals(inputStream.read(), -1);
        Asserts.assertEquals(inputStream.read(), -1);
        Asserts.assertEquals(inputStream.read(new byte[16], 0, 16), -1);
    }

    @Test
    public void repeatedGetInputStreamCannotBypassLimitTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH, bytes(1200));
        fixture.filter();
        HttpServletRequest passed = fixture.passedHttpRequest();

        passed.getInputStream().read(new byte[600], 0, 600);
        Throwable error = Asserts.assertThrows(() ->
            passed.getInputStream().read(new byte[600], 0, 600)
        );

        Asserts.assertTrue(passed.getInputStream() == passed.getInputStream());
        assertPayloadTooLarge(error);
    }

    @Test
    public void readerDecodesBodyWithRequestCharsetTest() throws Exception {
        byte[] body = "Nguyễn Văn A".getBytes(StandardCharsets.UTF_8);
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, body.length, body);
        when(fixture.request.getCharacterEncoding()).thenReturn("UTF-8");
        fixture.filter();

        BufferedReader reader = fixture.passedHttpRequest().getReader();

        Asserts.assertEquals(reader.readLine(), "Nguyễn Văn A");
    }

    @Test
    public void readerDefaultsToIso88591WithoutCharsetTest() throws Exception {
        byte[] body = new byte[] {'c', 'a', 'f', (byte) 0xE9};
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, body.length, body);
        fixture.filter();

        BufferedReader reader = fixture.passedHttpRequest().getReader();

        Asserts.assertEquals(reader.readLine(), "café");
    }

    @Test
    public void readerCannotBypassLimitTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH, bytes(2048));
        fixture.filter();
        BufferedReader reader = fixture.passedHttpRequest().getReader();

        Throwable error = Asserts.assertThrows(() -> {
            char[] buffer = new char[256];
            while (reader.read(buffer) != -1) {
                continue;
            }
        });

        assertPayloadTooLarge(error);
    }

    @Test
    public void asyncReadingAndCloseDelegatedToContainerStreamTest() throws Exception {
        ServletInputStream containerStream = mock(ServletInputStream.class);
        when(containerStream.isFinished()).thenReturn(true);
        when(containerStream.isReady()).thenReturn(false);
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH, containerStream);
        fixture.filter();
        ServletInputStream inputStream = fixture.passedHttpRequest().getInputStream();
        ReadListener listener = mock(ReadListener.class);

        boolean finished = inputStream.isFinished();
        boolean ready = inputStream.isReady();
        inputStream.setReadListener(listener);
        inputStream.close();

        Asserts.assertTrue(finished);
        Asserts.assertFalse(ready);
        verify(containerStream, times(1)).setReadListener(listener);
        verify(containerStream, times(1)).close();
    }

    @Test
    public void initAndDestroyDoNotAffectFilteringTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, 1025, new byte[0]);

        fixture.sut.init(mock(FilterConfig.class));
        fixture.filter();
        fixture.sut.destroy();

        fixture.verifyRejected();
    }

    private static byte[] bytes(int size) {
        byte[] answer = new byte[size];
        for (int i = 0; i < size; ++i) {
            answer[i] = (byte) ('a' + (i % 26));
        }
        return answer;
    }

    private static byte[] readAll(InputStream inputStream) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[100];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    private static void assertPayloadTooLarge(Throwable error) {
        Asserts.assertEqualsType(error, HttpRequestException.class);
        Asserts.assertEquals(((HttpRequestException) error).getCode(), PAYLOAD_TOO_LARGE);
    }

    private static class Fixture {

        private final RequestBodySizeLimitFilter sut;
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final FilterChain chain;

        Fixture(
            long maxRequestBodySize,
            long maxMultipartRequestSize,
            String contentType,
            long contentLength,
            byte[] body
        ) throws Exception {
            this(
                maxRequestBodySize,
                maxMultipartRequestSize,
                contentType,
                contentLength,
                new BytesServletInputStream(body)
            );
        }

        Fixture(
            long maxRequestBodySize,
            long maxMultipartRequestSize,
            String contentType,
            long contentLength,
            ServletInputStream body
        ) throws Exception {
            sut = new RequestBodySizeLimitFilter(maxRequestBodySize, maxMultipartRequestSize);
            request = mock(HttpServletRequest.class);
            when(request.getContentType()).thenReturn(contentType);
            when(request.getContentLengthLong()).thenReturn(contentLength);
            when(request.getInputStream()).thenReturn(body);
            response = mock(HttpServletResponse.class);
            chain = mock(FilterChain.class);
        }

        void filter() throws Exception {
            sut.doFilter(request, response, chain);
        }

        ServletRequest passedRequest() throws Exception {
            ArgumentCaptor<ServletRequest> captor = ArgumentCaptor.forClass(ServletRequest.class);
            verify(chain, times(1)).doFilter(captor.capture(), eq(response));
            return captor.getValue();
        }

        HttpServletRequest passedHttpRequest() throws Exception {
            return (HttpServletRequest) passedRequest();
        }

        void verifyRejected() throws Exception {
            verify(response, times(1)).setStatus(PAYLOAD_TOO_LARGE);
            verify(chain, never()).doFilter(any(), any());
        }
    }

    private static class BytesServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        BytesServletInputStream(byte[] body) {
            this.inputStream = new ByteArrayInputStream(body);
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return inputStream.read(b, off, len);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {}
    }
}
