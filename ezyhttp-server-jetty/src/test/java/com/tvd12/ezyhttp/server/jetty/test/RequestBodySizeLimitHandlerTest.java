package com.tvd12.ezyhttp.server.jetty.test;

import com.tvd12.ezyhttp.core.exception.HttpRequestException;
import com.tvd12.ezyhttp.server.jetty.RequestBodySizeLimitHandler;
import com.tvd12.test.assertion.Asserts;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpInput;
import org.eclipse.jetty.server.Request;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.*;

public class RequestBodySizeLimitHandlerTest {

    private static final String JSON = "application/json";
    private static final String UPLOAD = "multipart/form-data; boundary=----abc";
    private static final int PAYLOAD_TOO_LARGE = 413;
    private static final long UNKNOWN_LENGTH = -1L;
    private static final long HUGE = 10L * 1024 * 1024 * 1024;

    @Test
    public void disabledLimitsAcceptAnySizeWithoutWatchingTest() throws Exception {
        Fixture jsonFixture = new Fixture(-1, -1, JSON, HUGE);
        Fixture uploadFixture = new Fixture(-1, -1, UPLOAD, HUGE);

        jsonFixture.handle();
        uploadFixture.handle();

        jsonFixture.verifyPassedWithoutWatching();
        uploadFixture.verifyPassedWithoutWatching();
    }

    @Test
    public void disabledBodyLimitStillEnforcesUploadLimitTest() throws Exception {
        Fixture json = new Fixture(-1, 16 * 1024, JSON, HUGE);
        Fixture upload = new Fixture(-1, 16 * 1024, UPLOAD, 20 * 1024);

        json.handle();
        Throwable uploadError = Asserts.assertThrows(upload::handle);

        json.verifyPassedWithoutWatching();
        assertPayloadTooLarge(uploadError);
        upload.verifyNotPassed();
    }

    @Test
    public void disabledUploadLimitStillEnforcesBodyLimitTest() throws Exception {
        Fixture json = new Fixture(1024, -1, JSON, 2048);
        Fixture upload = new Fixture(1024, -1, UPLOAD, HUGE);

        Throwable jsonError = Asserts.assertThrows(json::handle);
        upload.handle();

        assertPayloadTooLarge(jsonError);
        json.verifyNotPassed();
        upload.verifyPassedWithoutWatching();
    }

    @Test
    public void normalRequestOverBodyLimitRejectedBeforeReadingTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, 1025);

        Throwable error = Asserts.assertThrows(fixture::handle);

        assertPayloadTooLarge(error);
        fixture.verifyNotPassed();
    }

    @Test
    public void normalRequestAtBodyLimitAcceptedAndWatchedTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, 1024);

        fixture.handle();

        fixture.verifyPassedAndWatched();
    }

    @Test
    public void uploadUsesUploadLimitInsteadOfBodyLimitTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, UPLOAD, 8 * 1024);

        fixture.handle();

        fixture.verifyPassedAndWatched();
    }

    @Test
    public void uploadContentTypeDetectedCaseInsensitivelyTest() throws Exception {
        Fixture fixture = new Fixture(
            1024,
            16 * 1024,
            "MULTIPART/FORM-DATA; boundary=xyz",
            8 * 1024
        );

        fixture.handle();

        fixture.verifyPassedAndWatched();
    }

    @Test
    public void uploadOverUploadLimitRejectedBeforeReadingTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, UPLOAD, 16 * 1024 + 1);

        Throwable error = Asserts.assertThrows(fixture::handle);

        assertPayloadTooLarge(error);
        fixture.verifyNotPassed();
    }

    @Test
    public void requestWithoutContentTypeUsesBodyLimitTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, null, 8 * 1024);

        Throwable error = Asserts.assertThrows(fixture::handle);

        assertPayloadTooLarge(error);
    }

    @Test
    public void unknownLengthRequestStillWatchedTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH);

        fixture.handle();

        fixture.verifyPassedAndWatched();
    }

    @Test
    public void zeroLimitAllowsOnlyEmptyBodyTest() throws Exception {
        Fixture empty = new Fixture(0, 0, JSON, 0);
        Fixture notEmpty = new Fixture(0, 0, JSON, 1);

        empty.handle();
        Throwable error = Asserts.assertThrows(notEmpty::handle);

        empty.verifyPassedAndWatched();
        assertPayloadTooLarge(error);
        Throwable streamError = Asserts.assertThrows(() ->
            empty.watcher().readFrom(content(1))
        );
        assertPayloadTooLarge(streamError);
    }

    @Test
    public void streamedBodyWithinLimitPassesThroughUnchangedTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH);
        fixture.handle();
        HttpInput.Interceptor watcher = fixture.watcher();
        HttpInput.Content first = content(400);
        HttpInput.Content second = content(624);

        Asserts.assertTrue(watcher.readFrom(first) == first);
        Asserts.assertTrue(watcher.readFrom(second) == second);
    }

    @Test
    public void streamedBodyOverLimitRejectedTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH);
        fixture.handle();
        HttpInput.Interceptor watcher = fixture.watcher();

        watcher.readFrom(content(600));
        Throwable error = Asserts.assertThrows(() -> watcher.readFrom(content(425)));

        assertPayloadTooLarge(error);
    }

    @Test
    public void sameChunkReadInManyStepsCountedOnceTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, UPLOAD, UNKNOWN_LENGTH);
        fixture.handle();
        HttpInput.Interceptor watcher = fixture.watcher();
        HttpInput.Content chunk = content(800);

        for (int i = 0; i < 10; ++i) {
            watcher.readFrom(chunk);
        }
        watcher.readFrom(content(200));
    }

    @Test
    public void partiallyConsumedChunkNotRecountedTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH);
        fixture.handle();
        HttpInput.Interceptor watcher = fixture.watcher();
        HttpInput.Content chunk = content(1000);

        watcher.readFrom(chunk);
        chunk.get(new byte[300], 0, 300);
        watcher.readFrom(chunk);
        chunk.get(new byte[300], 0, 300);
        watcher.readFrom(chunk);
        Throwable error = Asserts.assertThrows(() -> watcher.readFrom(content(25)));

        assertPayloadTooLarge(error);
    }

    @Test
    public void emptyChunksNotCountedTest() throws Exception {
        Fixture fixture = new Fixture(1024, 16 * 1024, JSON, UNKNOWN_LENGTH);
        fixture.handle();
        HttpInput.Interceptor watcher = fixture.watcher();

        for (int i = 0; i < 10; ++i) {
            watcher.readFrom(content(0));
        }
        watcher.readFrom(content(1024));
        Throwable error = Asserts.assertThrows(() -> watcher.readFrom(content(1)));

        assertPayloadTooLarge(error);
    }

    private static HttpInput.Content content(int size) {
        return new HttpInput.Content(ByteBuffer.wrap(new byte[size]));
    }

    private static void assertPayloadTooLarge(Throwable error) {
        if (error instanceof BadMessageException) {
            Asserts.assertEquals(((BadMessageException) error).getCode(), PAYLOAD_TOO_LARGE);
            return;
        }
        Asserts.assertEqualsType(error, HttpRequestException.class);
        Asserts.assertEquals(((HttpRequestException) error).getCode(), PAYLOAD_TOO_LARGE);
    }

    private static class Fixture {

        private final RequestBodySizeLimitHandler sut;
        private final Handler next;
        private final Request request;
        private final HttpInput httpInput;
        private final HttpServletResponse response;

        Fixture(
            long maxRequestBodySize,
            long maxMultipartRequestSize,
            String contentType,
            long contentLength
        ) {
            sut = new RequestBodySizeLimitHandler(
                maxRequestBodySize,
                maxMultipartRequestSize
            );
            next = mock(Handler.class);
            sut.setHandler(next);
            httpInput = mock(HttpInput.class);
            request = mock(Request.class);
            when(request.getContentType()).thenReturn(contentType);
            when(request.getContentLengthLong()).thenReturn(contentLength);
            when(request.getHttpInput()).thenReturn(httpInput);
            response = mock(HttpServletResponse.class);
        }

        void handle() throws Exception {
            sut.handle("/", request, request, response);
        }

        HttpInput.Interceptor watcher() {
            ArgumentCaptor<HttpInput.Interceptor> captor =
                ArgumentCaptor.forClass(HttpInput.Interceptor.class);
            verify(httpInput, times(1)).addInterceptor(captor.capture());
            return captor.getValue();
        }

        void verifyPassedWithoutWatching() throws Exception {
            verify(httpInput, never()).addInterceptor(any());
            verify(next, times(1)).handle("/", request, request, response);
        }

        void verifyPassedAndWatched() throws Exception {
            verify(httpInput, times(1)).addInterceptor(any());
            verify(next, times(1)).handle("/", request, request, response);
        }

        void verifyNotPassed() throws Exception {
            verify(httpInput, never()).addInterceptor(any());
            verify(next, never()).handle(any(), any(), any(), any());
        }
    }
}
