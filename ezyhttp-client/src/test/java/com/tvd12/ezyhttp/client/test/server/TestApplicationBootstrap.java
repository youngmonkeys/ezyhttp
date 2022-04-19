package com.tvd12.ezyhttp.client.test.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyProcessor;
import com.tvd12.ezyhttp.core.util.FileSizes;

import lombok.AccessLevel;
import lombok.Setter;

@Setter
public class TestApplicationBootstrap extends EzyLoggable {

    @EzyProperty("server.port")
    protected int port = 18081;

    @EzyProperty("server.host")
    protected String host = "0.0.0.0";

    @EzyProperty("server.max_threads")
    protected int maxThreads = 5;

    @EzyProperty("server.min_threads")
    protected int minThreads = 3;

    @EzyProperty("server.idle_timeout")
    protected int idleTimeout = 150 * 1000;

    @EzyProperty("server.multipart.location")
    protected String multipartLocation = "tmp";

    @EzyProperty("server.multipart.file_size_threshold")
    protected String multipartFileSizeThreshold = "5MB";

    @EzyProperty("server.multipart.max_file_size")
    protected String multipartMaxFileSize = "5MB";

    @EzyProperty("server.multipart.max_request_size")
    protected String multipartMaxRequestSize = "5MB";

    @EzyProperty("cors.allowed_origins")
    protected String allowedOrigins = "*";

    @Setter(AccessLevel.NONE)
    protected Server server;

    protected final AtomicBoolean started = new AtomicBoolean();

    private static final  TestApplicationBootstrap INSTANCE = new TestApplicationBootstrap();

    private TestApplicationBootstrap() {}

    public static TestApplicationBootstrap getInstance() {
        return INSTANCE;
    }

    public void start() {
        if (!started.compareAndSet(false, true))
            return;
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
        server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(host);
        connector.setPort(port);
        List<Connector> connectors = new ArrayList<>();
        connectors.add(connector);
        server.setConnectors(connectors.toArray(new Connector[0]));
        ServletContextHandler servletHandler = newServletHandler();
        server.setHandler(servletHandler);
        EzyProcessor.processSilently(server::start);
        logger.info("http server started on: {}:{}", host, port);
    }

    protected ServletContextHandler newServletHandler() {
        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.addServlet(TestBlockingServlet.class, "/*")
            .getRegistration()
            .setMultipartConfig(new MultipartConfigElement(
                multipartLocation,
                FileSizes.toByteSize(multipartMaxFileSize),
                (int)FileSizes.toByteSize(multipartMaxRequestSize),
                (int)FileSizes.toByteSize(multipartFileSizeThreshold)
            ));
        return servletHandler;
    }
}
