package com.tvd12.ezyhttp.server.core.test.servlet;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.servlet.BlockingServlet;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class BlockingServletMultipartTest {

    private static final int PORT = 8080;
    private static final String UPLOAD_URI = "/upload";

    @Test
    public void rejectedUploadNeverParsesBodyTest() throws Exception {
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.setServerPort(PORT);
        BlockingServlet sut = new BlockingServlet();
        sut.init();

        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("folder", new String[] {"avatars"});
        fullForm.put("description", new String[] {"my photo"});
        HttpServletRequest container = newUploadContainer(
            HttpMethod.POST,
            "folder=avatars",
            fullForm
        );

        UploadRequestHandler handler = new UploadRequestHandler();
        componentManager.getRequestHandlerManager().addHandler(
            new RequestURI(HttpMethod.POST, UPLOAD_URI, false),
            handler
        );

        Map<String, Object> seenByInterceptor = new HashMap<>();
        RequestInterceptor interceptor = mock(RequestInterceptor.class);
        when(interceptor.preHandle(any(), any())).thenAnswer(it -> {
            RequestArguments arguments = it.getArgumentAt(0, RequestArguments.class);
            HttpServletRequest rawRequest = arguments.getRequest();
            seenByInterceptor.put("folder", arguments.getParameter("folder"));
            seenByInterceptor.put("description", arguments.getParameter("description"));
            seenByInterceptor.put("rawDescription", rawRequest.getParameter("description"));
            seenByInterceptor.put("rawParameterMap", rawRequest.getParameterMap());
            return false;
        });
        componentManager.getInterceptorManager()
            .addRequestInterceptors(Collections.singletonList(interceptor));

        HttpServletResponse response = mock(HttpServletResponse.class);

        sut.service(container, response);

        verify(response, times(1)).setStatus(StatusCodes.NOT_ACCEPTABLE);
        Asserts.assertFalse(handler.called);
        Asserts.assertEquals(seenByInterceptor.get("folder"), "avatars");
        Asserts.assertNull(seenByInterceptor.get("description"));
        Asserts.assertNull(seenByInterceptor.get("rawDescription"));
        Asserts.assertFalse(
            ((Map<?, ?>) seenByInterceptor.get("rawParameterMap"))
                .containsKey("description")
        );
        verify(container, never()).getParameter(anyString());
        verify(container, never()).getParameterNames();
        verify(container, never()).getParameterValues(anyString());
        verify(container, never()).getParameterMap();
        verify(container, never()).getParts();
        verify(container, never()).getPart(anyString());

        componentManager.destroy();
    }

    @Test
    public void acceptedUploadHandlerReadsFormFieldsAndFilesTest() throws Exception {
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.setServerPort(PORT);
        BlockingServlet sut = new BlockingServlet();
        sut.init();

        Map<String, String[]> fullForm = new LinkedHashMap<>();
        fullForm.put("folder", new String[] {"avatars"});
        fullForm.put("description", new String[] {"my photo"});
        HttpServletRequest container = newUploadContainer(
            HttpMethod.POST,
            "folder=avatars",
            fullForm
        );
        Part file = mock(Part.class);
        Collection<Part> parts = Collections.singletonList(file);
        when(container.getParts()).thenReturn(parts);

        UploadRequestHandler handler = new UploadRequestHandler();
        componentManager.getRequestHandlerManager().addHandler(
            new RequestURI(HttpMethod.POST, UPLOAD_URI, false),
            handler
        );

        Map<String, Object> seenByInterceptor = new HashMap<>();
        RequestInterceptor interceptor = mock(RequestInterceptor.class);
        when(interceptor.preHandle(any(), any())).thenAnswer(it -> {
            RequestArguments arguments = it.getArgumentAt(0, RequestArguments.class);
            seenByInterceptor.put("description", arguments.getParameter("description"));
            return true;
        });
        componentManager.getInterceptorManager()
            .addRequestInterceptors(Collections.singletonList(interceptor));

        HttpServletResponse response = mock(HttpServletResponse.class);

        sut.service(container, response);

        verify(response, times(1)).setStatus(StatusCodes.OK);
        Asserts.assertNull(seenByInterceptor.get("description"));
        Asserts.assertTrue(handler.called);
        Asserts.assertEquals(handler.folder, "avatars");
        Asserts.assertEquals(handler.description, "my photo");
        Asserts.assertEquals(handler.firstParameter, "avatars");
        Asserts.assertEquals(handler.secondParameter, "my photo");
        Asserts.assertEquals(handler.parts, parts);
        verify(container, times(1)).getParameterNames();
        verify(interceptor, times(1)).postHandle(any(), any());

        componentManager.destroy();
    }

    @Test
    public void normalFormInterceptorStillSeesBodyFieldsTest() throws Exception {
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.setServerPort(PORT);
        BlockingServlet sut = new BlockingServlet();
        sut.init();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.POST.toString());
        when(request.getRequestURI()).thenReturn(UPLOAD_URI);
        when(request.getServerPort()).thenReturn(PORT);
        when(request.getContentType()).thenReturn(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED);
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(Collections.singletonList("csrf"))
        );
        when(request.getParameterValues("csrf")).thenReturn(new String[] {"token"});

        UploadRequestHandler handler = new UploadRequestHandler();
        componentManager.getRequestHandlerManager().addHandler(
            new RequestURI(HttpMethod.POST, UPLOAD_URI, false),
            handler
        );

        Map<String, Object> seenByInterceptor = new HashMap<>();
        RequestInterceptor interceptor = mock(RequestInterceptor.class);
        when(interceptor.preHandle(any(), any())).thenAnswer(it -> {
            RequestArguments arguments = it.getArgumentAt(0, RequestArguments.class);
            seenByInterceptor.put("csrf", arguments.getParameter("csrf"));
            return false;
        });
        componentManager.getInterceptorManager()
            .addRequestInterceptors(Collections.singletonList(interceptor));

        HttpServletResponse response = mock(HttpServletResponse.class);

        sut.service(request, response);

        Asserts.assertEquals(seenByInterceptor.get("csrf"), "token");
        verify(response, times(1)).setStatus(StatusCodes.NOT_ACCEPTABLE);

        componentManager.destroy();
    }

    private static HttpServletRequest newUploadContainer(
        HttpMethod method,
        String queryString,
        Map<String, String[]> fullForm
    ) {
        HttpServletRequest container = mock(HttpServletRequest.class);
        when(container.getMethod()).thenReturn(method.toString());
        when(container.getRequestURI()).thenReturn(UPLOAD_URI);
        when(container.getServerPort()).thenReturn(PORT);
        when(container.getContentType())
            .thenReturn("multipart/form-data; boundary=----abc");
        when(container.getQueryString()).thenReturn(queryString);
        when(container.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(container.getParameterNames())
            .thenAnswer(it -> Collections.enumeration(fullForm.keySet()));
        when(container.getParameterMap()).thenReturn(fullForm);
        for (Map.Entry<String, String[]> e : fullForm.entrySet()) {
            when(container.getParameterValues(e.getKey())).thenReturn(e.getValue());
            when(container.getParameter(e.getKey())).thenReturn(e.getValue()[0]);
        }
        return container;
    }

    public static class UploadRequestHandler implements RequestHandler {

        private boolean called;
        private String folder;
        private String description;
        private String firstParameter;
        private String secondParameter;
        private Collection<Part> parts;

        @Override
        public Object handle(RequestArguments arguments) throws Exception {
            called = true;
            folder = arguments.getParameter("folder");
            description = arguments.getParameter("description");
            firstParameter = arguments.getParameter(0);
            secondParameter = arguments.getParameter(1);
            parts = arguments.getRequest().getParts();
            return null;
        }

        @Override
        public HttpMethod getMethod() {
            return HttpMethod.POST;
        }

        @Override
        public String getRequestURI() {
            return UPLOAD_URI;
        }

        @Override
        public String getResponseContentType() {
            return null;
        }
    }
}
