package com.tvd12.ezyhttp.server.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import com.tvd12.ezyhttp.server.core.ApplicationEntry;
import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.servlet.BlockingServlet;

@ApplicationBootstrap
public class JettyApplicationBootstrap implements ApplicationEntry {
	
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
    	ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(BlockingServlet.class, "/*");
        return servletHandler;
    }
    
	public static void main(String[] args) throws Exception {
		EzyHttpApplication.start(JettyApplicationBootstrap.class);
	}
}
