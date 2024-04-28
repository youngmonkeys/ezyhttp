package com.tvd12.ezyhttp.server.core.test.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.util.HttpServletRequests;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;

public class HttpServletRequestsTest extends BaseTest {

    @Override
    public Class<?> getTestClass() {
        return HttpServletRequests.class;
    }

    @Test
    public void getRequestValueTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(request.getAttribute("1")).thenReturn("a");
        when(request.getHeader("2")).thenReturn("b");
        when(request.getParameter("3")).thenReturn("c");

        // then
        Asserts.assertEquals(HttpServletRequests.getRequestValue(request, "1"), "a");
        Asserts.assertEquals(HttpServletRequests.getRequestValue(request, "2"), "b");
        Asserts.assertEquals(HttpServletRequests.getRequestValue(request, "3"), "c");
        Asserts.assertNull(HttpServletRequests.getRequestValue(request, "unknown"));

        verify(request, times(1)).getAttribute("1");
        verify(request, times(1)).getHeader("2");
        verify(request, times(1)).getParameter("3");
        verify(request, times(1)).getCookies();
    }

    @Test
    public void getRequestValueTestWithCookie() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(request.getCookies()).thenReturn(new Cookie[]{
            new Cookie("1", ""), new Cookie("2", "b"), new Cookie("3", "c")
        });

        // then
        Asserts.assertEquals(HttpServletRequests.getRequestValue(request, "1"), "");
        Asserts.assertEquals(HttpServletRequests.getRequestValue(request, "2"), "b");
        Asserts.assertEquals(HttpServletRequests.getRequestValue(request, "3"), "c");
        Asserts.assertNull(HttpServletRequests.getRequestValue(request, "unknown"));

        verify(request, times(1)).getAttribute("1");
        verify(request, times(1)).getHeader("2");
        verify(request, times(1)).getParameter("3");
        verify(request, times(8)).getCookies();
    }

    @Test
    public void getRequestValueAnywayTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        when(request.getAttribute("1")).thenReturn("a");
        when(request.getHeader("2")).thenReturn("b");
        when(request.getParameter("3")).thenReturn("c");
        when(request.getParameter("a")).thenReturn("d");

        // then
        Asserts.assertEquals(HttpServletRequests.getRequestValueAnyway(request, "1"), "a");
        Asserts.assertEquals(HttpServletRequests.getRequestValueAnyway(request, "2"), "b");
        Asserts.assertEquals(HttpServletRequests.getRequestValueAnyway(request, "3"), "c");
        Asserts.assertEquals(HttpServletRequests.getRequestValueAnyway(request, "A"), "d");
        Asserts.assertNull(HttpServletRequests.getRequestValueAnyway(request, "unknown"));

        verify(request, times(1)).getAttribute("1");
        verify(request, times(1)).getHeader("2");
        verify(request, times(1)).getParameter("3");
        verify(request, times(1)).getParameter("A");
        verify(request, times(1)).getParameter("a");
        verify(request, times(3)).getCookies();
    }
}
