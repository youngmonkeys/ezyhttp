package com.tvd12.ezyhttp.server.tomcat;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.concurrent.HttpThreadFactory;
import com.tvd12.ezyhttp.core.util.FileSizes;
import com.tvd12.ezyhttp.server.core.ApplicationEntry;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.catalina.Context;
import org.apache.catalina.Server;
import org.apache.catalina.Wrapper;
import org.apache.catalina.filters.CorsFilter;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadFactory;

@Setter
@ApplicationBootstrap
public class TomcatApplicationBootstrap
    extends EzyLoggable
    implements ApplicationEntry {

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

    @EzyProperty("server.context.path")
    protected String contextPath;

    @EzyProperty("server.multipart.location")
    protected String multipartLocation = "tmp";

    @EzyProperty("server.multipart.file_size_threshold")
    protected String multipartFileSizeThreshold = "5MB";

    @EzyProperty("server.multipart.max_file_size")
    protected String multipartMaxFileSize = "5MB";

    @EzyProperty("server.multipart.max_request_size")
    protected String multipartMaxRequestSize = "5MB";

    @EzyProperty("cors.enable")
    protected boolean corsEnable;

    @EzyProperty("cors.allowed_origins")
    protected String allowedOrigins = "*";

    @EzyProperty("cors.allowed_headers")
    protected String allowedHeaders = "*";

    @EzyProperty("management.enable")
    protected boolean managementEnable = true;

    @EzyProperty("management.host")
    protected String managementHost = "0.0.0.0";

    @EzyProperty("management.port")
    protected int managementPort = 18080;

    @Setter(AccessLevel.NONE)
    protected Tomcat tomcat;

    @Setter(AccessLevel.NONE)
    protected Tomcat managementTomcat;

    protected final ThreadFactory startTomcatThreadFactory =
        HttpThreadFactory.create("start-tomcat");

    @Override
    public void start() throws Exception {
        tomcat = newTomcat(host, port);
        if (managementEnable) {
            managementTomcat = newTomcat(managementHost, managementPort);
        }
        startTomcat(tomcat);
        logger.info("http server started on: {}:{}", host, port);
        if (managementEnable) {
            startTomcat(managementTomcat);
            logger.info("management started on: {}:{}", managementHost, managementPort);
        }
    }

    protected Tomcat newTomcat(String host, int port) throws Exception {
        final Tomcat answer = new Tomcat();
        answer.setHostname(host);
        answer.setPort(port);
        final String actualContextPath = new File(
            contextPath != null
                ? contextPath
                : System.getProperty("java.io.tmpdir")
        ).getAbsolutePath();
        final Context context = answer.addContext(
            "",
            actualContextPath
        );
        final Wrapper wrapper = answer.addServlet(
            "",
            "Servlet",
            new TomcatBlockingServlet()
        );
        wrapper.setAsyncSupported(true);
        final String actualMultipartLocation = multipartLocation != null
            ? multipartLocation
            : "tmp";
        final Path multipartPath = actualMultipartLocation.startsWith("/")
            ? Paths.get(actualMultipartLocation)
            : Paths.get(actualContextPath, actualMultipartLocation);
        if (!Files.exists(multipartPath)) {
            Files.createDirectories(multipartPath);
        }
        wrapper.setMultipartConfigElement(
            new MultipartConfigElement(
                multipartPath.toString(),
                FileSizes.toByteSize(multipartMaxFileSize),
                (int) FileSizes.toByteSize(multipartMaxRequestSize),
                (int) FileSizes.toByteSize(multipartFileSizeThreshold)
            )
        );
        context.addServletMappingDecoded("/*", "Servlet");
        if (corsEnable) {
            addCorsFilter(context);
        }
        return answer;
    }

    protected void addCorsFilter(Context context) {
        final FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("cross-origin");
        filterDef.setFilterClass(CorsFilter.class.getName());
        filterDef.addInitParameter(
            CorsFilter.PARAM_CORS_ALLOWED_ORIGINS,
            allowedOrigins
        );
        filterDef.addInitParameter(
            CorsFilter.PARAM_CORS_ALLOWED_HEADERS,
            allowedHeaders
        );
        context.addFilterDef(filterDef);

        final FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("cross-origin");
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);
    }

    protected void startTomcat(Tomcat tomcatToStart) throws Exception {
        tomcatToStart.start();
        final Server server = tomcatToStart.getServer();
        startTomcatThreadFactory.newThread(server::await).start();
    }
}
