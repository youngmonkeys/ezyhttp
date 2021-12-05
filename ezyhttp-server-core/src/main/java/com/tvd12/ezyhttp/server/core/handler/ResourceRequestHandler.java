package com.tvd12.ezyhttp.server.core.handler;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyfox.util.EzyProcessor;
import com.tvd12.ezyhttp.core.constant.ContentType;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

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
	    HttpServletRequest servletRequest = arguments.getRequest();
        AsyncContext asyncContext = servletRequest.getAsyncContext();
        HttpServletResponse servletResponse = arguments.getResponse();
        InputStream inputStream = inputStreamLoader.load(resourcePath);
        OutputStream outputStream = servletResponse.getOutputStream();
        try {
            downloadManager.drainAsync(
                inputStream, 
                outputStream, 
                new EzyResultCallback<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        servletResponse.setStatus(StatusCodes.OK);
                        asyncContext.complete();
                        EzyProcessor.processWithLogException(() -> inputStream.close());
                    }
                    public void onException(Exception e) {
                        servletResponse.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
                        asyncContext.complete();
                        EzyProcessor.processWithLogException(() -> inputStream.close());
                    }
                }
            );
        }
        catch (Exception e) {
            EzyProcessor.processWithLogException(() -> inputStream.close());
            servletResponse.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
            asyncContext.complete();
        }
        return ResponseEntity.ASYNC;
	}
	
	@Override
	public boolean isAsync() {
	    return true;
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
