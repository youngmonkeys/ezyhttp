package com.tvd12.ezyhttp.server.core.test.handler;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager;
import com.tvd12.test.assertion.Asserts;

import static org.mockito.Mockito.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ResourceRequestHandlerTest {

	@Test
	public void test() throws Exception {
		// given
		String resourcePath = "static/index.html";
	    String resourceURI = "/index.html";
	    String resourceExtension = "html";
	    ResourceDownloadManager downloadManager = new ResourceDownloadManager();
		ResourceRequestHandler sut = new ResourceRequestHandler(
			resourcePath,
			resourceURI,
			resourceExtension,
			downloadManager
		);
		
		RequestArguments arguments = mock(RequestArguments.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(arguments.getResponse()).thenReturn(response);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.handle(arguments);
		
		// then
		Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
		Asserts.assertEquals("/index.html", sut.getRequestURI());
		Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
		downloadManager.stop();
	}
}
