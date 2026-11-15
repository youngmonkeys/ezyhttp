package com.tvd12.ezyhttp.server.core.test.response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.server.core.response.NoBodyHttpServletResponse;
import com.tvd12.test.assertion.Asserts;

public class NoBodyHttpServletResponseTest {

    @Test
    public void getOutputStreamTest() throws Exception {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        NoBodyHttpServletResponse sut = new NoBodyHttpServletResponse(response);

        // when
        ServletOutputStream outputStream = sut.getOutputStream();
        outputStream.write(1);
        outputStream.write(new byte[]{1, 2, 3});
        outputStream.write(new byte[]{1, 2, 3}, 0, 2);
        outputStream.setWriteListener(mock(WriteListener.class));
        outputStream.flush();
        outputStream.close();

        // then
        Asserts.assertTrue(outputStream.isReady());
        Asserts.assertEquals(sut.getOutputStream(), outputStream);
        verify(response, never()).getOutputStream();
    }

    @Test
    public void getWriterTest() throws Exception {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        NoBodyHttpServletResponse sut = new NoBodyHttpServletResponse(response);

        // when
        PrintWriter writer = sut.getWriter();
        writer.write("Hello World");
        writer.flush();

        // then
        Asserts.assertEquals(sut.getWriter(), writer);
        verify(response, never()).getWriter();
        verify(response, never()).getOutputStream();
    }

    @Test
    public void delegateToWrappedResponseTest() {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        NoBodyHttpServletResponse sut = new NoBodyHttpServletResponse(response);

        // when
        sut.setStatus(StatusCodes.OK);
        sut.addHeader("X-Total", "10");
        sut.setContentLength(100);
        String contentType = sut.getContentType();

        // then
        Asserts.assertEquals(contentType, ContentTypes.APPLICATION_JSON);
        Asserts.assertEquals(sut.getResponse(), response);
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(response, times(1)).addHeader("X-Total", "10");
        verify(response, times(1)).setContentLength(100);
        verify(response, times(1)).getContentType();
    }
}
