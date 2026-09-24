package com.tvd12.ezyhttp.server.core.test.request;

import com.tvd12.ezyhttp.server.core.request.DeferredMultipartHttpServletRequest;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class DeferredMultipartHttpServletRequestTest {

    @Test
    public void detectFormUploadRequestTest() {
        Asserts.assertTrue(isMultipart("multipart/form-data; boundary=----abc"));
        Asserts.assertTrue(isMultipart("multipart/form-data"));
        Asserts.assertTrue(isMultipart("MULTIPART/FORM-DATA; boundary=xyz"));
        Asserts.assertTrue(isMultipart(" multipart/form-data ;boundary=xyz"));
    }

    @Test
    public void notDetectNonFormUploadRequestTest() {
        Asserts.assertFalse(isMultipart(null));
        Asserts.assertFalse(isMultipart("application/json"));
        Asserts.assertFalse(isMultipart("application/x-www-form-urlencoded"));
        Asserts.assertFalse(isMultipart("multipart/mixed; boundary=xyz"));
        Asserts.assertFalse(isMultipart("application/octet-stream"));
    }

    @Test
    public void onlyQueryParametersVisibleBeforeRequestAcceptedTest() {
        HttpServletRequest container = newUploadRequest("folder=avatars");
        when(container.getParameter("description")).thenReturn("my photo");
        when(container.getParameter("folder")).thenReturn("avatars");

        DeferredMultipartHttpServletRequest sut =
            new DeferredMultipartHttpServletRequest(container);

        Asserts.assertFalse(sut.isContentAccessible());
        Asserts.assertEquals(sut.getParameter("folder"), "avatars");
        Asserts.assertNull(sut.getParameter("description"));
        Asserts.assertEquals(
            Collections.list(sut.getParameterNames()),
            Collections.singletonList("folder"),
            false
        );
        Asserts.assertNull(sut.getParameterValues("description"));
        Asserts.assertFalse(sut.getParameterMap().containsKey("description"));
        verifyUploadBodyNotParsed(container);
    }

    @Test
    public void uploadedFilesNotAccessibleBeforeRequestAcceptedTest() throws Exception {
        HttpServletRequest container = newUploadRequest(null);
        DeferredMultipartHttpServletRequest sut =
            new DeferredMultipartHttpServletRequest(container);

        Throwable getPartsError = Asserts.assertThrows(sut::getParts);
        Throwable getPartError = Asserts.assertThrows(() -> sut.getPart("file"));

        Asserts.assertEqualsType(getPartsError, IllegalStateException.class);
        Asserts.assertEqualsType(getPartError, IllegalStateException.class);
        verify(container, never()).getParts();
        verify(container, never()).getPart(anyString());
        verifyUploadBodyNotParsed(container);
    }

    @Test
    public void rejectedRequestNeverParsesUploadBodyTest() throws Exception {
        HttpServletRequest container = newUploadRequest("token=abc&token=def");
        DeferredMultipartHttpServletRequest sut =
            new DeferredMultipartHttpServletRequest(container);

        sut.getParameter("token");
        sut.getParameter("unknown");
        sut.getParameterValues("token");
        Collections.list(sut.getParameterNames());
        sut.getParameterMap();
        Asserts.assertThrows(sut::getParts);

        verifyUploadBodyNotParsed(container);
        verify(container, never()).getParts();
    }

    @Test
    public void fullFormVisibleAfterRequestAcceptedTest() {
        HttpServletRequest container = newUploadRequest("folder=avatars");
        Map<String, String[]> fullForm = new HashMap<>();
        fullForm.put("folder", new String[] {"avatars"});
        fullForm.put("description", new String[] {"my photo"});
        when(container.getParameter("folder")).thenReturn("avatars");
        when(container.getParameter("description")).thenReturn("my photo");
        when(container.getParameterValues("description"))
            .thenReturn(new String[] {"my photo"});
        when(container.getParameterNames())
            .thenReturn(Collections.enumeration(fullForm.keySet()));
        when(container.getParameterMap()).thenReturn(fullForm);

        DeferredMultipartHttpServletRequest sut =
            new DeferredMultipartHttpServletRequest(container);
        sut.allowContentAccess();

        Asserts.assertTrue(sut.isContentAccessible());
        Asserts.assertEquals(sut.getParameter("folder"), "avatars");
        Asserts.assertEquals(sut.getParameter("description"), "my photo");
        Asserts.assertEquals(
            Arrays.asList(sut.getParameterValues("description")),
            Collections.singletonList("my photo"),
            false
        );
        Asserts.assertEquals(
            Collections.list(sut.getParameterNames()).size(),
            2
        );
        Asserts.assertTrue(sut.getParameterMap().containsKey("description"));
    }

    @Test
    public void uploadedFilesAccessibleAfterRequestAcceptedTest() throws Exception {
        HttpServletRequest container = newUploadRequest(null);
        Part file = mock(Part.class);
        Collection<Part> parts = Collections.singletonList(file);
        when(container.getParts()).thenReturn(parts);
        when(container.getPart("file")).thenReturn(file);

        DeferredMultipartHttpServletRequest sut =
            new DeferredMultipartHttpServletRequest(container);
        sut.allowContentAccess();

        Asserts.assertEquals(sut.getParts(), parts);
        Asserts.assertEquals(sut.getPart("file"), file);
    }

    @Test
    public void decodeUrlEncodedQueryParametersTest() {
        DeferredMultipartHttpServletRequest sut = newDeferredRequest(
            "name=Nguy%E1%BB%85n+V%C4%83n+A&email=user%40example.com" +
                "&redirect=%2Fhome%3Ftab%3D1"
        );

        Asserts.assertEquals(sut.getParameter("name"), "Nguyễn Văn A");
        Asserts.assertEquals(sut.getParameter("email"), "user@example.com");
        Asserts.assertEquals(sut.getParameter("redirect"), "/home?tab=1");
    }

    @Test
    public void decodeUrlEncodedQueryParameterNameTest() {
        DeferredMultipartHttpServletRequest sut =
            newDeferredRequest("user%5Bname%5D=dung");

        Asserts.assertEquals(sut.getParameter("user[name]"), "dung");
    }

    @Test
    public void repeatedQueryParameterKeepsAllValuesInOrderTest() {
        DeferredMultipartHttpServletRequest sut =
            newDeferredRequest("tag=java&tag=http&tag=upload");

        Asserts.assertEquals(sut.getParameter("tag"), "java");
        Asserts.assertEquals(
            Arrays.asList(sut.getParameterValues("tag")),
            Arrays.asList("java", "http", "upload")
        );
        Asserts.assertEquals(
            Arrays.asList(sut.getParameterMap().get("tag")),
            Arrays.asList("java", "http", "upload")
        );
    }

    @Test
    public void queryParameterWithoutValueIsEmptyStringTest() {
        DeferredMultipartHttpServletRequest sut =
            newDeferredRequest("debug&lang=");

        Asserts.assertEquals(sut.getParameter("debug"), "");
        Asserts.assertEquals(sut.getParameter("lang"), "");
    }

    @Test
    public void queryParameterWithoutNameIsIgnoredTest() {
        DeferredMultipartHttpServletRequest sut =
            newDeferredRequest("=orphan&&page=2");

        Asserts.assertEquals(
            Collections.list(sut.getParameterNames()),
            Collections.singletonList("page"),
            false
        );
        Asserts.assertEquals(sut.getParameter("page"), "2");
    }

    @Test
    public void malformedEncodingKeepsRawValueTest() {
        DeferredMultipartHttpServletRequest sut =
            newDeferredRequest("discount=100%&code=%zz");

        Asserts.assertEquals(sut.getParameter("discount"), "100%");
        Asserts.assertEquals(sut.getParameter("code"), "%zz");
    }

    @Test
    public void noQueryStringMeansNoParametersTest() {
        DeferredMultipartHttpServletRequest nullQuery = newDeferredRequest(null);
        DeferredMultipartHttpServletRequest blankQuery = newDeferredRequest("  ");

        Asserts.assertFalse(nullQuery.getParameterNames().hasMoreElements());
        Asserts.assertTrue(nullQuery.getParameterMap().isEmpty());
        Asserts.assertNull(nullQuery.getParameter("any"));
        Asserts.assertNull(nullQuery.getParameterValues("any"));
        Asserts.assertFalse(blankQuery.getParameterNames().hasMoreElements());
    }

    @Test
    public void callerCannotTamperQueryParametersBeforeAcceptedTest() {
        DeferredMultipartHttpServletRequest sut =
            newDeferredRequest("role=user");

        sut.getParameterValues("role")[0] = "admin";
        Throwable putError = Asserts.assertThrows(() ->
            sut.getParameterMap().put("role", new String[] {"admin"})
        );

        Asserts.assertEqualsType(putError, UnsupportedOperationException.class);
        Asserts.assertEquals(sut.getParameter("role"), "user");
        Asserts.assertEquals(
            Arrays.asList(sut.getParameterValues("role")),
            Collections.singletonList("user"),
            false
        );
    }

    @Test
    public void callerCannotTamperQueryParametersViaParameterMapValuesTest() {
        DeferredMultipartHttpServletRequest sut =
            newDeferredRequest("role=user&role=guest");

        sut.getParameterMap().get("role")[0] = "admin";

        Asserts.assertEquals(sut.getParameter("role"), "user");
        Asserts.assertEquals(
            Arrays.asList(sut.getParameterValues("role")),
            Arrays.asList("user", "guest")
        );
        Asserts.assertEquals(
            Arrays.asList(sut.getParameterMap().get("role")),
            Arrays.asList("user", "guest")
        );
    }

    private static boolean isMultipart(String contentType) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContentType()).thenReturn(contentType);
        return DeferredMultipartHttpServletRequest.isMultipartRequest(request);
    }

    private static HttpServletRequest newUploadRequest(String queryString) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContentType())
            .thenReturn("multipart/form-data; boundary=----abc");
        when(request.getQueryString()).thenReturn(queryString);
        return request;
    }

    private static DeferredMultipartHttpServletRequest newDeferredRequest(
        String queryString
    ) {
        return new DeferredMultipartHttpServletRequest(
            newUploadRequest(queryString)
        );
    }

    private static void verifyUploadBodyNotParsed(HttpServletRequest container) {
        verify(container, never()).getParameter(anyString());
        verify(container, never()).getParameterNames();
        verify(container, never()).getParameterValues(anyString());
        verify(container, never()).getParameterMap();
    }
}
