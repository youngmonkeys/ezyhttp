package com.tvd12.ezyhttp.server.core.view;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ViewContext {

	void render(
			ServletContext servletContext,
			HttpServletRequest request,
			HttpServletResponse response, 
			View view) throws IOException;
	
}
