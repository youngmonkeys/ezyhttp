package com.tvd12.ezyhttp.server.jetty;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyStoppable;
import com.tvd12.ezyhttp.core.util.FileSizes;
import com.tvd12.ezyhttp.server.core.ApplicationEntry;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import lombok.AccessLevel;
import lombok.Setter;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyStrings.isNotEmpty;
import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;

@Setter
@ApplicationBootstrap
public class JettyApplicationBootstrap
    extends EzyLoggable
    implements ApplicationEntry, EzyStoppable {

    @EzyProperty("server.port")
    protected int port = 8080;

    @EzyProperty("server.host")
    protected String host = "0.0.0.0";

    @EzyProperty("server.max_threads")
    protected int maxThreads = 256;

    @EzyProperty("server.min_threads")
    protected int minThreads = 16;

    @EzyProperty("server.idle_timeout")
    protected int idleTimeout = 150 * 1000;

    @EzyProperty("server.max_request_body_size")
    protected String maxRequestBodySize = "2MB";

    @EzyProperty("server.multipart.location")
    protected String multipartLocation =
        System.getProperty("java.io.tmpdir");

    @EzyProperty("server.multipart.file_size_threshold")
    protected String multipartFileSizeThreshold = "1MB";

    @EzyProperty("server.multipart.max_file_size")
    protected String multipartMaxFileSize = "5MB";

    @EzyProperty("server.multipart.max_request_size")
    protected String multipartMaxRequestSize = "5MB";

    @EzyProperty("server.compression.enable")
    protected boolean compressionEnable = true;

    @EzyProperty("server.compression.min_size")
    protected String compressionMinSize;

    @EzyProperty("server.compression.included_methods")
    protected String[] compressionIncludedMethods;

    @EzyProperty("server.compression.excluded_methods")
    protected String[] compressionExcludedMethods;

    @EzyProperty("server.compression.included_mime_types")
    protected String[] compressionIncludedMimeTypes;

    @EzyProperty("server.compression.excluded_mime_types")
    protected String[] compressionExcludedMimeTypes;

    @EzyProperty("cors.enable")
    protected boolean corsEnable;

    @EzyProperty("cors.allowed_origins")
    protected String allowedOrigins = "*";

    @EzyProperty("cors.allowed_headers")
    protected String allowedHeaders = "*";

    @EzyProperty("management.enable")
    protected boolean managementEnable;

    @EzyProperty("management.host")
    protected String managementHost = "0.0.0.0";

    @EzyProperty("management.port")
    protected int managementPort = 18080;

    @Setter(AccessLevel.NONE)
    protected Server server;

    @Override
    public void start() throws Exception {
        QueuedThreadPool threadPool = new QueuedThreadPool(
            maxThreads,
            minThreads,
            idleTimeout
        );
        server = new Server(threadPool);
        List<Connector> connectors = createConnectors();
        server.setConnectors(connectors.toArray(new Connector[0]));
        Handler servletHandler = newServletHandler();
        if (compressionEnable) {
            GzipHandler gzipHandler = newGzipHandler();
            gzipHandler.setHandler(servletHandler);
            servletHandler = gzipHandler;
        }
        server.setHandler(servletHandler);
        server.start();
        logger.info("http server started on: {}:{}", host, port);
        if (managementEnable) {
            logger.info(
                "management started on: {}:{}",
                managementHost,
                managementPort
            );
        }
    }

    private List<Connector> createConnectors() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        ServerConnector connector = new ServerConnector(
            server,
            new HttpConnectionFactory(httpConfig)
        );
        connector.setHost(host);
        connector.setPort(port);
        List<Connector> connectors = new ArrayList<>();
        connectors.add(connector);
        if (managementEnable) {
            ServerConnector managementConnector = new ServerConnector(server);
            managementConnector.setHost(managementHost);
            managementConnector.setPort(managementPort);
            connectors.add(managementConnector);
        }
        return connectors;
    }

    protected ServletContextHandler newServletHandler() {
        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler
            .addServlet(JettyBlockingServlet.class, "/*")
            .getRegistration()
            .setMultipartConfig(
                new MultipartConfigElement(
                    multipartLocation,
                    FileSizes.toByteSize(multipartMaxFileSize),
                    (int) FileSizes.toByteSize(multipartMaxRequestSize),
                    (int) FileSizes.toByteSize(multipartFileSizeThreshold)
                )
            );
        servletHandler.setMaxFormContentSize(
            (int) FileSizes.toByteSize(maxRequestBodySize)
        );
        logger.info("cors.enable = {}", corsEnable);
        if (corsEnable) {
            addFilter(servletHandler, newCrossOriginFilter());
        }
        return servletHandler;
    }

    protected GzipHandler newGzipHandler() {
        GzipHandler gzipHandler = new GzipHandler();
        if (isNotEmpty(compressionMinSize)) {
            gzipHandler.setMinGzipSize(
                (int) FileSizes.toByteSize(compressionMinSize)
            );
        }
        if (compressionIncludedMethods != null) {
            gzipHandler.setIncludedMethods(compressionIncludedMethods);
        }
        if (compressionExcludedMethods != null) {
            gzipHandler.setExcludedMethods(compressionExcludedMethods);
        }
        if (compressionIncludedMimeTypes != null) {
            gzipHandler.setIncludedMimeTypes(compressionIncludedMimeTypes);
        }
        if (compressionExcludedMimeTypes != null) {
            gzipHandler.setExcludedMimeTypes(compressionExcludedMimeTypes);
        }
        return gzipHandler;
    }

    protected void addFilter(
        ServletContextHandler servletHandler,
        FilterHolder filter
    ) {
        servletHandler.addFilter(
            filter,
            "/*",
            EnumSet.of(DispatcherType.REQUEST)
        );
    }

    protected FilterHolder newCrossOriginFilter() {
        FilterHolder filter = new FilterHolder();
        filter.setName("cross-origin");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, allowedOrigins);
        filter.setInitParameter(
            CrossOriginFilter.ALLOWED_METHODS_PARAM,
            "GET,HEAD,POST,PUT,DELETE,CONNECT,TRACE,PATCH,OPTIONS"
        );
        filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, allowedHeaders);
        filter.setInitParameter(
            CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER,
            allowedOrigins
        );
        filter.setInitParameter(
            CrossOriginFilter.ACCESS_CONTROL_ALLOW_METHODS_HEADER,
            "GET,HEAD,POST,PUT,DELETE,CONNECT,TRACE,PATCH,OPTIONS"
        );
        filter.setInitParameter(
            CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER,
            allowedHeaders
        );
        CrossOriginFilter corsFilter = new CrossOriginFilter();
        filter.setFilter(corsFilter);
        return filter;
    }

    @Override
    public void stop() {
        if (server != null) {
            processWithLogException(server::stop);
        }
    }
}
