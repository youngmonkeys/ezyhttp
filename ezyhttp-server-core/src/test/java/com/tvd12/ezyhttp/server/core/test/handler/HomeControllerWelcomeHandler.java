package com.tvd12.ezyhttp.server.core.test.handler;

import com.tvd12.ezyhttp.server.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.test.controller.HomeController;

public class HomeControllerWelcomeHandler implements RequestHandler {

	protected HomeController controller;
	
	@Override
	public void setController(Object controller) {
		this.controller = (HomeController)controller;
	}

	@Override
	public Object handle(RequestArguments arguments) {
		String arg0 = arguments.getParameter(0);
		return controller.welcome(arg0);
	}
	
	@Override
	public String getResponseContentType() {
		return ContentTypes.APPLICATION_JSON;
	}
	

}
