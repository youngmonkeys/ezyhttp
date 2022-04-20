package com.tvd12.ezyhttp.server.core.test.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.handler.UnhandledErrorHandler;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.test.assertion.Asserts;

public class UnhandledErrorHandlerTest {

    @Test
    public void returnResponseEntity() {
        // given
        ResponseEntity result = ResponseEntity.ok();
        UnhandledErrorHandler sut = new UnhandledErrorHandler() {
            @Override
            public Object processError(
                HttpMethod method,
                HttpServletRequest request,
                HttpServletResponse response,
                int errorStatusCode,
                Exception exception
            ) {
                return result;
            }
        };

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        Object actual = sut.handleError(
            HttpMethod.GET,
            request,
            response,
            StatusCodes.BAD_REQUEST,
            null
        );

        // then
        Asserts.assertEquals(result, actual);

        verify(response, times(1)).setContentType(null);
        verify(response, times(1)).setContentType(ContentTypes.APPLICATION_JSON);
    }

    @Test
    public void returnView() {
        // given
        View result = View.builder()
            .template("foo")
            .build();
        UnhandledErrorHandler sut = new UnhandledErrorHandler() {
            @Override
            public Object processError(
                HttpMethod method,
                HttpServletRequest request,
                HttpServletResponse response,
                int errorStatusCode,
                Exception exception
            ) {
                return result;
            }
        };

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getContentType()).thenReturn(ContentTypes.TEXT_HTML_UTF8);

        // when
        Object actual = sut.handleError(
            HttpMethod.GET,
            request,
            response,
            StatusCodes.BAD_REQUEST,
            null
        );

        // then
        Asserts.assertEquals(result, actual);

        verify(response, times(1)).setContentType(ContentTypes.TEXT_HTML_UTF8);
    }

    @Test
    public void returnString() {
        // given
        String result = "bar";
        UnhandledErrorHandler sut = new UnhandledErrorHandler() {
            @Override
            public Object processError(
                HttpMethod method,
                HttpServletRequest request,
                HttpServletResponse response,
                int errorStatusCode,
                Exception exception
            ) {
                return result;
            }
        };

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        Object actual = sut.handleError(
            HttpMethod.GET,
            request,
            response,
            StatusCodes.BAD_REQUEST,
            null
        );

        // then
        Asserts.assertEquals(result, actual);

        verify(response, times(1)).setContentType(ContentTypes.APPLICATION_JSON);
    }

    @Test
    public void defaultNull() {
        // given
        UnhandledErrorHandler sut = new UnhandledErrorHandler() {
        };

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        Object actual = sut.handleError(
            HttpMethod.GET,
            request,
            response,
            StatusCodes.BAD_REQUEST,
            null
        );

        // then
        Asserts.assertNull(actual);
    }
}
