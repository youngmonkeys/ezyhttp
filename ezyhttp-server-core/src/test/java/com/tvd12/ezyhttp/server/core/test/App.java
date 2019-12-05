package com.tvd12.ezyhttp.server.core.test;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ControllerManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.servlet.BlockingServlet;
import com.tvd12.ezyhttp.server.core.test.controller.HomeController;
import com.tvd12.ezyhttp.server.core.test.handler.HomeControllerWelcomeHandler;

public class App {
	
	protected Server server;
	 
    public void start() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[] {connector});
        ServletHandler servletHandler = newServletHandler();
        server.setHandler(servletHandler);
        server.start();
    }
	
    protected ServletHandler newServletHandler() {
    	ComponentManager componentManager = ComponentManager.getInstance();
    	ControllerManager controllerManager = componentManager.getControllerManager();
    	controllerManager.addController("/", new HomeController());
    	RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
    	RequestHandler handler = new HomeControllerWelcomeHandler();
    	handler.setController(controllerManager.getController("/"));
    	requestHandlerManager.addHandler("/", handler);
    	
    	ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(BlockingServlet.class, "/");
        return servletHandler;
    }
    
	public static void main(String[] args) throws Exception {
		App app = new App();
		app.start();
	}
}
