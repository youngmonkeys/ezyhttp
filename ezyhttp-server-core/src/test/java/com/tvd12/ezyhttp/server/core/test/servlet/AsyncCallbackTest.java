package com.tvd12.ezyhttp.server.core.test.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletRequest;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.servlet.AsyncCallback;
import static org.mockito.Mockito.*;

public class AsyncCallbackTest {

    @Test
    public void test() throws Exception {
        // given
        AsyncCallback callback = new AsyncCallback() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
            }
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
     
}
