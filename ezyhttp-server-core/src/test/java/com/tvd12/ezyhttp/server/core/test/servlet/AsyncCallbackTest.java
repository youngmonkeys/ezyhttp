package com.tvd12.ezyhttp.server.core.test.servlet;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.server.core.servlet.AsyncCallback;
import org.testng.annotations.Test;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class AsyncCallbackTest {

    @Test
    public void test() throws Exception {
        // given
        AsyncCallback callback = event -> {
        };
        ServletRequest request = mock(ServletRequest.class);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(asyncContext.getRequest()).thenReturn(request);

        AsyncEvent event = new AsyncEvent(asyncContext);

        // when
        // then
        callback.onComplete(event);
        callback.onError(event);
        callback.onTimeout(event);
        callback.onStartAsync(event);
    }

    @Test
    public void onErrorWithHttpServletResponse() {
        // given
        AsyncCallback callback = event -> {
        };

        AsyncContext asyncContext = mock(AsyncContext.class);

        ServletRequest request = mock(ServletRequest.class);
        when(asyncContext.getRequest()).thenReturn(request);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        AsyncEvent event = new AsyncEvent(asyncContext);

        // when
        callback.onError(event);

        // then
        verify(asyncContext, times(1)).getRequest();
        verify(asyncContext, times(2)).getResponse();
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void onErrorFailed() {
        // given
        AsyncCallback callback = event -> {
        };

        AsyncContext asyncContext = mock(AsyncContext.class);
        doThrow(RuntimeException.class).when(asyncContext).complete();

        ServletRequest request = mock(ServletRequest.class);
        when(asyncContext.getRequest()).thenReturn(request);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        AsyncEvent event = new AsyncEvent(asyncContext);

        // when
        callback.onError(event);

        // then
        verify(asyncContext, times(1)).getRequest();
        verify(asyncContext, times(2)).getResponse();
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void onTimeoutWithHttpServletResponse() {
        // given
        AsyncCallback callback = event -> {
        };

        AsyncContext asyncContext = mock(AsyncContext.class);

        ServletRequest request = mock(ServletRequest.class);
        when(asyncContext.getRequest()).thenReturn(request);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        AsyncEvent event = new AsyncEvent(asyncContext);

        // when
        callback.onTimeout(event);

        // then
        verify(asyncContext, times(1)).getRequest();
        verify(asyncContext, times(2)).getResponse();
        verify(response, times(1)).setStatus(StatusCodes.REQUEST_TIMEOUT);
    }

    @Test
    public void onTimeoutFailed() {
        // given
        AsyncCallback callback = event -> {
        };

        AsyncContext asyncContext = mock(AsyncContext.class);
        doThrow(RuntimeException.class).when(asyncContext).complete();

        ServletRequest request = mock(ServletRequest.class);
        when(asyncContext.getRequest()).thenReturn(request);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        AsyncEvent event = new AsyncEvent(asyncContext);

        // when
        callback.onTimeout(event);

        // then
        verify(asyncContext, times(1)).getRequest();
        verify(asyncContext, times(2)).getResponse();
        verify(response, times(1)).setStatus(StatusCodes.REQUEST_TIMEOUT);
    }
}
