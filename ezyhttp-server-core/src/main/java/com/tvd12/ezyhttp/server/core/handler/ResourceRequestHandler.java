package com.tvd12.ezyhttp.server.core.handler;

import java.io.InputStream;
import java.io.OutputStream;

import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyhttp.core.constant.ContentType;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager;

public class ResourceRequestHandler implements RequestHandler {
	
	private final String resourcePath;
	private final String resourceURI;
	private final String resourceExtension;
	private final EzyInputStreamLoader inputStreamLoader;
	private final ResourceDownloadManager downloadManager;
	
	public ResourceRequestHandler(
			String resourcePath, 
			String resourceURI,
			String resourceExtension,
			ResourceDownloadManager downloadManager) {
		this.resourcePath = resourcePath;
		this.resourceURI = resourceURI;
		this.resourceExtension = resourceExtension;
		this.downloadManager = downloadManager;
		this.inputStreamLoader = new EzyAnywayInputStreamLoader();
	}
	

	@Override
	public Object handle(RequestArguments arguments) throws Exception {
		InputStream inputStream = inputStreamLoader.load(resourcePath);
		OutputStream outputStream = arguments.getResponse().getOutputStream();
		try {
			downloadManager.drain(inputStream, outputStream);
		}
		finally {
			inputStream.close();
		}
		return null;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.GET;
	}

	@Override
	public String getRequestURI() {
		return resourceURI;
	}

	@Override
	public String getResponseContentType() {
		return ContentType.ofExtension(resourceExtension).getValue();
	}
}
