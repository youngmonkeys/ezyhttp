package com.tvd12.ezyhttp.server.core.test.request;

import com.tvd12.ezyhttp.server.core.request.DeferredMultipartHttpServletRequest;
import com.tvd12.ezyhttp.server.core.request.SimpleRequestArguments;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class SimpleRequestArgumentsMultipartTest {

    @Test
    public void interceptorSeesOnlyQueryParametersBeforeAcceptedTest() {
        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("folder", new String[] {"avatars"});
        fullForm.put("description", new String[] {"my photo"});
        HttpServletRequest container = newUploadContainer("folder=avatars", fullForm);
        DeferredMultipartHttpServletRequest request =
            new DeferredMultipartHttpServletRequest(container);
        SimpleRequestArguments sut = newArguments(request);

        Asserts.assertEquals(sut.getParameter("folder"), "avatars");
        Asserts.assertNull(sut.getParameter("description"));
        Asserts.assertEquals(sut.getParameter(0), "avatars");
        Asserts.assertNull(sut.getParameter(1));
        Asserts.assertEquals(
            sut.getParameters(),
            Collections.singletonMap("folder", "avatars"),
            false
        );
        verifyUploadBodyNotParsed(container);
    }

    @Test
    public void handlerSeesBodyFieldsByNameAfterAcceptedTest() {
        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("folder", new String[] {"avatars"});
        fullForm.put("description", new String[] {"my photo"});
        HttpServletRequest container = newUploadContainer("folder=avatars", fullForm);
        DeferredMultipartHttpServletRequest request =
            new DeferredMultipartHttpServletRequest(container);
        SimpleRequestArguments sut = newArguments(request);

        request.allowContentAccess();

        Asserts.assertEquals(sut.getParameter("folder"), "avatars");
        Asserts.assertEquals(sut.getParameter("description"), "my photo");
    }

    @Test
    public void handlerSeesBodyFieldsByIndexEvenWhenQueryAlreadyFillsIndexTest() {
        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("a", new String[] {"1"});
        fullForm.put("b", new String[] {"2"});
        HttpServletRequest container = newUploadContainer("a=1", fullForm);
        DeferredMultipartHttpServletRequest request =
            new DeferredMultipartHttpServletRequest(container);
        SimpleRequestArguments sut = newArguments(request);
        String firstBeforeAccepted = sut.getParameter(0);

        request.allowContentAccess();

        Asserts.assertEquals(firstBeforeAccepted, "1");
        Asserts.assertEquals(sut.getParameter(0), "1");
        Asserts.assertEquals(sut.getParameter(1), "2");
        Asserts.assertNull(sut.getParameter(2));
    }

    @Test
    public void sameNameInQueryAndBodyMergedAfterAcceptedTest() {
        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("tag", new String[] {"java", "http"});
        HttpServletRequest container = newUploadContainer("tag=java", fullForm);
        DeferredMultipartHttpServletRequest request =
            new DeferredMultipartHttpServletRequest(container);
        SimpleRequestArguments sut = newArguments(request);
        String tagBeforeAccepted = sut.getParameter("tag");

        request.allowContentAccess();

        Asserts.assertEquals(tagBeforeAccepted, "java");
        Asserts.assertEquals(sut.getParameter("tag"), "java,http");
    }

    @Test
    public void allParametersIncludeBodyFieldsAfterAcceptedTest() {
        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("folder", new String[] {"avatars"});
        fullForm.put("description", new String[] {"my photo"});
        HttpServletRequest container = newUploadContainer("folder=avatars", fullForm);
        DeferredMultipartHttpServletRequest request =
            new DeferredMultipartHttpServletRequest(container);
        SimpleRequestArguments sut = newArguments(request);

        request.allowContentAccess();
        Map<String, String> parameters = sut.getParameters();

        Asserts.assertEquals(parameters.size(), 2);
        Asserts.assertEquals(parameters.get("folder"), "avatars");
        Asserts.assertEquals(parameters.get("description"), "my photo");
    }

    @Test
    public void uploadBodyParsedOnlyOnceForManyReadsTest() {
        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("folder", new String[] {"avatars"});
        fullForm.put("description", new String[] {"my photo"});
        HttpServletRequest container = newUploadContainer("folder=avatars", fullForm);
        DeferredMultipartHttpServletRequest request =
            new DeferredMultipartHttpServletRequest(container);
        SimpleRequestArguments sut = newArguments(request);

        request.allowContentAccess();
        sut.getParameter("description");
        sut.getParameter("unknown");
        sut.getParameter("unknown");
        sut.getParameter(0);
        sut.getParameter(10);
        sut.getParameters();

        Asserts.assertNull(sut.getParameter("unknown"));
        verify(container, times(1)).getParameterNames();
    }

    @Test
    public void normalRequestKeepsCopiedParametersWithoutReparsingTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setRequest(request);
        sut.setParameter("username", new String[] {"dung"});

        Asserts.assertEquals(sut.getParameter("username"), "dung");
        Asserts.assertEquals(sut.getParameter(0), "dung");
        Asserts.assertNull(sut.getParameter("unknown"));
        Asserts.assertNull(sut.getParameter(1));
        Asserts.assertEquals(
            sut.getParameters(),
            Collections.singletonMap("username", "dung"),
            false
        );
        verify(request, never()).getParameterNames();
        verify(request, never()).getParameterValues(anyString());
    }

    @Test
    public void reusedArgumentsLoadBodyFieldsOfNewUploadTest() {
        Map<String, String[]> firstForm = new LinkedHashMap<>();
        firstForm.put("description", new String[] {"first"});
        HttpServletRequest firstContainer = newUploadContainer(null, firstForm);
        DeferredMultipartHttpServletRequest firstRequest =
            new DeferredMultipartHttpServletRequest(firstContainer);
        SimpleRequestArguments sut = newArguments(firstRequest);
        firstRequest.allowContentAccess();
        String firstDescription = sut.getParameter("description");
        sut.release();

        Map<String, String[]> secondForm = new LinkedHashMap<>();
        secondForm.put("description", new String[] {"second"});
        HttpServletRequest secondContainer = newUploadContainer(null, secondForm);
        DeferredMultipartHttpServletRequest secondRequest =
            new DeferredMultipartHttpServletRequest(secondContainer);
        copyParameters(sut, secondRequest);
        sut.setRequest(secondRequest);
        secondRequest.allowContentAccess();

        Asserts.assertEquals(firstDescription, "first");
        Asserts.assertEquals(sut.getParameter("description"), "second");
    }

    private static HttpServletRequest newUploadContainer(
        String queryString,
        Map<String, String[]> fullForm
    ) {
        HttpServletRequest container = mock(HttpServletRequest.class);
        when(container.getContentType())
            .thenReturn("multipart/form-data; boundary=----abc");
        when(container.getQueryString()).thenReturn(queryString);
        when(container.getParameterNames())
            .thenAnswer(it -> Collections.enumeration(fullForm.keySet()));
        for (Map.Entry<String, String[]> e : fullForm.entrySet()) {
            when(container.getParameterValues(e.getKey()))
                .thenReturn(e.getValue());
        }
        return container;
    }

    private static SimpleRequestArguments newArguments(HttpServletRequest request) {
        SimpleRequestArguments arguments = new SimpleRequestArguments();
        arguments.setRequest(request);
        copyParameters(arguments, request);
        return arguments;
    }

    private static void copyParameters(
        SimpleRequestArguments arguments,
        HttpServletRequest request
    ) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            arguments.setParameter(paramName, request.getParameterValues(paramName));
        }
    }

    private static void verifyUploadBodyNotParsed(HttpServletRequest container) {
        verify(container, never()).getParameter(anyString());
        verify(container, never()).getParameterNames();
        verify(container, never()).getParameterValues(anyString());
        verify(container, never()).getParameterMap();
    }
}
