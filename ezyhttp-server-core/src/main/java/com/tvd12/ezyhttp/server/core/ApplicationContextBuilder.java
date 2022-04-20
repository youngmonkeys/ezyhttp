package com.tvd12.ezyhttp.server.core;

import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_PACKAGE_TO_SCAN;
import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_PROPERTIES_FILES;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.annotation.EzyPackagesToScan;
import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.bean.EzyBeanContextBuilder;
import com.tvd12.ezyfox.bean.EzyPackagesToScanProvider;
import com.tvd12.ezyfox.bean.EzyPropertiesMap;
import com.tvd12.ezyfox.bean.impl.EzyBeanKey;
import com.tvd12.ezyfox.bean.impl.EzyBeanNameParser;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.reflect.EzyClasses;
import com.tvd12.ezyfox.reflect.EzyPackages;
import com.tvd12.ezyfox.reflect.EzyReflection;
import com.tvd12.ezyhttp.core.annotation.BodyConvert;
import com.tvd12.ezyhttp.core.annotation.Interceptor;
import com.tvd12.ezyhttp.core.annotation.StringConvert;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.annotation.ComponentClasses;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.ExceptionHandler;
import com.tvd12.ezyhttp.server.core.annotation.PropertiesSources;
import com.tvd12.ezyhttp.server.core.annotation.Service;
import com.tvd12.ezyhttp.server.core.asm.ExceptionHandlersImplementer;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlersImplementer;
import com.tvd12.ezyhttp.server.core.constant.PropertyNames;
import com.tvd12.ezyhttp.server.core.handler.IRequestController;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;
import com.tvd12.ezyhttp.server.core.handler.RequestURIDecorator;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.handler.UnhandledErrorHandler;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ControllerManager;
import com.tvd12.ezyhttp.server.core.manager.ExceptionHandlerManager;
import com.tvd12.ezyhttp.server.core.manager.InterceptorManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.resources.Resource;
import com.tvd12.ezyhttp.server.core.resources.ResourceResolver;
import com.tvd12.ezyhttp.server.core.resources.ResourceResolvers;
import com.tvd12.ezyhttp.server.core.util.InterceptorAnnotations;
import com.tvd12.ezyhttp.server.core.util.ServiceAnnotations;
import com.tvd12.ezyhttp.server.core.view.AbsentMessageResolver;
import com.tvd12.ezyhttp.server.core.view.MessageProvider;
import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.core.view.ViewContextBuilder;
import com.tvd12.ezyhttp.server.core.view.ViewDecorator;
import com.tvd12.ezyhttp.server.core.view.ViewDialect;

@SuppressWarnings({"rawtypes", "unchecked", "MethodCount"})
public class ApplicationContextBuilder implements EzyBuilder<ApplicationContext> {

    protected final Properties properties;
    protected final Set<String> packageToScans;
    protected final Set<Class> componentClasses;
    protected final Set<String> propertiesSources;
    protected final ObjectMapper objectMapper;
    protected final DataConverters dataConverters;
    protected final ComponentManager componentManager;
    protected final ControllerManager controllerManager;
    protected final InterceptorManager interceptorManager;
    protected final RequestHandlerManager requestHandlerManager;
    protected final ExceptionHandlerManager exceptionHandlerManager;
    protected final Map<String, Object> singletonByName;
    protected final Map<EzyBeanKey, Object> singletonByKey;

    public ApplicationContextBuilder() {
        this.properties = defaultProperties();
        this.packageToScans = new HashSet<>();
        this.componentClasses = new HashSet<>();
        this.propertiesSources = new HashSet<>();
        this.singletonByKey = new HashMap<>();
        this.singletonByName = new HashMap<>();
        this.componentManager = ComponentManager.getInstance();
        this.objectMapper = componentManager.getObjectMapper();
        this.dataConverters = componentManager.getDataConverters();
        this.controllerManager = componentManager.getControllerManager();
        this.interceptorManager = componentManager.getInterceptorManager();
        this.requestHandlerManager = componentManager.getRequestHandlerManager();
        this.exceptionHandlerManager = componentManager.getExceptionHandlerManager();
    }

    public Properties defaultProperties() {
        Properties props = new Properties();
        props.put(PropertyNames.SERVER_PORT, 8080);
        return props;
    }

    public ApplicationContextBuilder scan(String packageName) {
        this.packageToScans.add(packageName);
        return this;
    }

    public ApplicationContextBuilder scan(String... packageNames) {
        for (String packageName : packageNames) {
            scan(packageName);
        }
        return this;
    }

    public ApplicationContextBuilder scan(Iterable<String> packageNames) {
        for (String packageName : packageNames) {
            scan(packageName);
        }
        return this;
    }

    public ApplicationContextBuilder addComponentClass(Class<?> componentClass) {
        ComponentsScan componentsScan = componentClass.getAnnotation(ComponentsScan.class);
        if (componentsScan != null) {
            scan(
                    componentsScan.value().length != 0
                            ? componentsScan.value()
                            : new String[]{componentClass.getPackage().getName()}

            );
        }
        EzyPackagesToScan packagesToScan = componentClass.getAnnotation(EzyPackagesToScan.class);
        if (packagesToScan != null) {
            scan(
                    packagesToScan.value().length != 0
                            ? packagesToScan.value()
                            : new String[]{componentClass.getPackage().getName()}

            );
        }
        ComponentClasses componentClasses = componentClass.getAnnotation(ComponentClasses.class);
        if (componentClasses != null) {
            addComponentClasses(componentClasses.value());
        }
        PropertiesSources propertiesSources = componentClass.getAnnotation(PropertiesSources.class);
        if (propertiesSources != null) {
            addPropertiesSources(propertiesSources.value());
        }
        this.componentClasses.add(componentClass);
        return this;
    }

    public ApplicationContextBuilder addComponentClasses(Class<?>... componentClasses) {
        for (Class<?> clazz : componentClasses) {
            addComponentClass(clazz);
        }
        return this;
    }

    public ApplicationContextBuilder addComponentClasses(Iterable<Class<?>> componentClasses) {
        for (Class<?> clazz : componentClasses) {
            addComponentClass(clazz);
        }
        return this;
    }

    public ApplicationContextBuilder addPropertiesSource(String source) {
        this.propertiesSources.add(source);
        return this;
    }

    public ApplicationContextBuilder addPropertiesSources(String... sources) {
        for (String source : sources) {
            addPropertiesSource(source);
        }
        return this;
    }

    public ApplicationContextBuilder addPropertiesSources(Iterable<String> sources) {
        for (String source : sources) {
            addPropertiesSource(source);
        }
        return this;
    }

    public ApplicationContextBuilder addProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public ApplicationContextBuilder addProperties(Properties properties) {
        this.properties.putAll(properties);
        return this;
    }

    public ApplicationContextBuilder addProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public ApplicationContextBuilder addSingleton(Object singleton) {
        return addSingleton(
                EzyBeanNameParser.getSingletonName(singleton.getClass()),
                singleton
        );
    }

    public ApplicationContextBuilder addSingleton(String name, Object singleton) {
        this.singletonByName.put(name, singleton);
        return this;
    }

    public ApplicationContextBuilder addSingleton(Map<String, Object> singletons) {
        this.singletonByName.putAll(singletons);
        return this;
    }

    public ApplicationContextBuilder beanContext(EzyBeanContext beanContext) {
        this.singletonByKey.putAll(beanContext.getSingletonMapByKey());
        return this;
    }

    @Override
    public ApplicationContext build() {
        EzyBeanContext beanContext = createBeanContext();
        SimpleApplicationContext context = new SimpleApplicationContext();
        context.setBeanContext(beanContext);
        return context;
    }

    protected EzyBeanContext createBeanContext() {
        if (packageToScans.isEmpty()) {
            throw new IllegalStateException("must scan at least one package");
        }
        Set<String> allPackageToScans = new HashSet<>();
        allPackageToScans.add(DEFAULT_PACKAGE_TO_SCAN);
        allPackageToScans.addAll(packageToScans);
        EzyReflection reflection = EzyPackages.scanPackages(allPackageToScans);
        addComponentClassesFromReflection(reflection);
        allPackageToScans.addAll(packageToScans);
        allPackageToScans.addAll(getPackagesToScanFromProviders(reflection));
        reflection = EzyPackages.scanPackages(allPackageToScans);
        Set controllerClasses = reflection.getAnnotatedClasses(Controller.class);
        Set interceptorClases = reflection.getAnnotatedClasses(Interceptor.class);
        Set exceptionHandlerClasses = reflection.getAnnotatedClasses(ExceptionHandler.class);
        Set bodyConverterClasses = reflection.getAnnotatedClasses(BodyConvert.class);
        Set stringConverterClasses = reflection.getAnnotatedClasses(StringConvert.class);
        Set bootstrapClasses = reflection.getAnnotatedClasses(ApplicationBootstrap.class);
        Map<String, Class> serviceClasses = getServiceClasses(reflection);
        EzyPropertiesMap propertiesMap = getPropertiesMap(reflection);
        EzyBeanContext beanContext = newBeanContextBuilder()
                .scan(allPackageToScans)
                .addSingletonClasses(componentClasses)
                .addSingletonClasses(serviceClasses)
                .addSingletonClasses(controllerClasses)
                .addSingletonClasses(interceptorClases)
                .addSingletonClasses(exceptionHandlerClasses)
                .addSingletonClasses(bodyConverterClasses)
                .addSingletonClasses(stringConverterClasses)
                .addSingletonClasses(bootstrapClasses)
                .propertiesMap(propertiesMap)
                .addSingleton("systemObjectMapper", objectMapper)
                .addSingleton("componentManager", componentManager)
                .addSingleton("requestHandlerManager", requestHandlerManager)
                .addSingleton("featureURIManager", requestHandlerManager.getFeatureURIManager())
                .addSingleton("requestURIManager", requestHandlerManager.getRequestURIManager())
                .addAllClasses(EzyPackages.scanPackage(DEFAULT_PACKAGE_TO_SCAN))
                .build();
        setComponentProperties(beanContext);
        registerComponents(beanContext);
        addRequestHandlers(beanContext);
        addResourceRequestHandlers(beanContext);
        addExceptionHandlers();
        return beanContext;
    }

    private Set<String> getPackagesToScanFromProviders(EzyReflection reflection) {
        Set<String> answer = new HashSet<>();
        Set<Class<?>> providerClasses = reflection
                .getExtendsClasses(EzyPackagesToScanProvider.class);
        for (Class<?> clazz : providerClasses) {
            EzyPackagesToScanProvider provider =
                    (EzyPackagesToScanProvider) EzyClasses.newInstance(clazz);
            answer.addAll(provider.provide());
        }
        return answer;
    }

    protected void addComponentClassesFromReflection(EzyReflection reflection) {
        Set<Class> classes = new HashSet<>();
        classes.addAll(reflection.getAnnotatedClasses(ComponentsScan.class));
        classes.addAll(reflection.getAnnotatedClasses(ComponentClasses.class));
        classes.addAll(reflection.getAnnotatedClasses(PropertiesSources.class));
        classes.addAll(reflection.getAnnotatedClasses(EzyPackagesToScan.class));
        for (Class clazz : classes) {
            addComponentClass(clazz);
        }
    }

    protected EzyBeanContextBuilder newBeanContextBuilder() {
        EzyBeanContextBuilder beanContextBuilder = EzyBeanContext.builder()
                .addProperties(properties)
                .addSingletons(singletonByName)
                .addSingletonsByKey(singletonByKey);
        List<String> propertiesFiles = new ArrayList<>();
        propertiesFiles.addAll(Arrays.asList(DEFAULT_PROPERTIES_FILES));
        propertiesFiles.addAll(propertiesSources);
        for (String propertiesFile : propertiesFiles) {
            beanContextBuilder.addProperties(propertiesFile);
        }
        return beanContextBuilder;
    }

    protected EzyPropertiesMap getPropertiesMap(EzyReflection reflection) {
        Class propertiesMapClass = reflection.getExtendsClass(EzyPropertiesMap.class);
        if (propertiesMapClass == null) {
            return null;
        }
        return (EzyPropertiesMap) EzyClasses.newInstance(propertiesMapClass);
    }

    protected Map<String, Class> getServiceClasses(EzyReflection reflection) {
        Set<Class<?>> classes = reflection.getAnnotatedClasses(Service.class);
        Map<String, Class> answer = new HashMap<>();
        for (Class<?> clazz : classes) {
            String serviceName = ServiceAnnotations.getServiceName(clazz);
            if (answer.containsKey(serviceName)) {
                serviceName = clazz.getName();
            }
            answer.put(serviceName, clazz);
        }
        return answer;
    }

    protected void setComponentProperties(EzyBeanContext beanContext) {
        componentManager.setDebug(
                beanContext.getProperty(PropertyNames.DEBUG, boolean.class, false)
        );
        requestHandlerManager.setAllowOverrideURI(
                beanContext.getProperty(PropertyNames.ALLOW_OVERRIDE_URI, boolean.class, false)
        );
    }

    protected void registerComponents(EzyBeanContext beanContext) {
        Set controllers = new HashSet<>();
        controllers.addAll(beanContext.getSingletons(Controller.class));
        controllers.addAll(beanContext.getSingletonsOf(IRequestController.class));
        controllerManager.addControllers(controllers);
        List exceptionHandlers = beanContext.getSingletons(ExceptionHandler.class);
        exceptionHandlerManager.addExceptionHandlers(exceptionHandlers);
        List<RequestInterceptor> requestInterceptors = beanContext.getSingletons(Interceptor.class);
        requestInterceptors.sort(InterceptorAnnotations.comparator());
        interceptorManager.addRequestInterceptors(requestInterceptors);
        List bodyConverters = beanContext.getSingletons(BodyConvert.class);
        dataConverters.addBodyConverters(bodyConverters);
        List stringConverters = beanContext.getSingletons(StringConvert.class);
        List uncaughtErrorHandlers = beanContext.getSingletonsOf(UnhandledErrorHandler.class);
        List requestResponseWathcers = beanContext.getSingletonsOf(RequestResponseWatcher.class);
        dataConverters.setStringConverters(stringConverters);
        componentManager.setViewContext(buildViewContext(beanContext));
        componentManager.setServerPort(getServerPort(beanContext));
        componentManager.setExposeManagementURIs(isExposeManagementURIs(beanContext));
        componentManager.setManagementPort(getManagementPort(beanContext));
        componentManager.setAsyncDefaultTimeout(getAsyncDefaultTimeout(beanContext));
        componentManager.setUnhandledErrorHandler(uncaughtErrorHandlers);
        componentManager.addRequestResponseWatchers(requestResponseWathcers);
    }

    private int getServerPort(EzyBeanContext beanContext) {
        return beanContext.getProperty(SERVER_PORT, int.class, 0);
    }

    private boolean isExposeManagementURIs(EzyBeanContext beanContext) {
        return beanContext.getProperty(MANAGEMENT_URIS_EXPOSE, boolean.class, false);
    }

    private int getManagementPort(EzyBeanContext beanContext) {
        boolean managementEnable = beanContext.getProperty(
                MANAGEMENT_ENABLE, boolean.class, false);
        return managementEnable
                ? beanContext.getProperty(MANAGEMENT_PORT, int.class, 18080)
                : 0;
    }

    private int getAsyncDefaultTimeout(EzyBeanContext beanContext) {
        return beanContext.getProperty(ASYNC_DEFAULT_TIMEOUT, int.class, 0);
    }

    protected ViewContext buildViewContext(EzyBeanContext beanContext) {
        ViewContext viewContext = beanContext.getSingleton(ViewContext.class);
        if (viewContext == null) {
            ViewContextBuilder viewContextBuilder = beanContext
                    .getSingleton(ViewContextBuilder.class);
            if (viewContextBuilder != null) {
                TemplateResolver templateResolver = beanContext
                        .getSingleton(TemplateResolver.class);
                if (templateResolver == null) {
                    templateResolver = TemplateResolver.of(beanContext);
                }
                viewContext = viewContextBuilder
                        .templateResolver(templateResolver)
                        .viewDialects(beanContext.getSingletonsOf(ViewDialect.class))
                        .viewDecorators(beanContext.getSingletonsOf(ViewDecorator.class))
                        .messageProviders(beanContext.getSingletonsOf(MessageProvider.class))
                        .absentMessageResolver(beanContext
                                .getSingleton(AbsentMessageResolver.class))
                        .build();
            }
        }
        if (viewContext != null) {
            beanContext.getSingletonFactory().addSingleton(viewContext);
        }
        return viewContext;
    }

    protected void addRequestHandlers(EzyBeanContext beanContext) {
        List<Object> controllerList = controllerManager.getControllers();
        RequestHandlersImplementer implementer = newRequestHandlersImplementer();
        implementer.setRequestURIDecorator(beanContext.getSingleton(RequestURIDecorator.class));
        Map<RequestURI, List<RequestHandler>> requestHandlers =
                implementer.implement(controllerList);
        requestHandlerManager.addHandlers(requestHandlers);
    }

    protected void addResourceRequestHandlers(EzyBeanContext beanContext) {
        ResourceResolver resourceResolver = getResourceResolver(beanContext);
        if (resourceResolver == null) {
            return;
        }
        ResourceDownloadManager downloadManager = beanContext
                .getSingleton(ResourceDownloadManager.class);
        Map<String, Resource> resources = resourceResolver.getResources();
        for (String resourceURI : resources.keySet()) {
            Resource resource = resources.get(resourceURI);
            RequestURI requestURI = new RequestURI(
                    HttpMethod.GET,
                    resourceURI,
                    false,
                    true,
                    true,
                    resource.getFullPath());
            RequestHandler requestHandler = new ResourceRequestHandler(
                    resource.getPath(),
                    resource.getUri(),
                    resource.getExtension(),
                    downloadManager);
            requestHandlerManager.addHandler(requestURI, requestHandler);
        }
    }

    protected ResourceResolver getResourceResolver(EzyBeanContext beanContext) {
        ResourceResolver resourceResolver =
                beanContext.getSingleton(ResourceResolver.class);
        if (resourceResolver == null) {
            resourceResolver = ResourceResolvers.createResourdeResolver(beanContext);
            if (resourceResolver != null) {
                beanContext.getSingletonFactory().addSingleton(resourceResolver);
            }
        }
        return resourceResolver;
    }

    protected void addExceptionHandlers() {
        List<Object> exceptionHandlerList = exceptionHandlerManager.getExceptionHandlerList();
        ExceptionHandlersImplementer implementer = newExceptionHandlersImplementer();
        Map<Class<?>, UncaughtExceptionHandler> exceptionHandlers = implementer
                .implement(exceptionHandlerList);
        exceptionHandlerManager.addUncaughtExceptionHandlers(exceptionHandlers);
    }

    protected RequestHandlersImplementer newRequestHandlersImplementer() {
        return new RequestHandlersImplementer();
    }

    protected ExceptionHandlersImplementer newExceptionHandlersImplementer() {
        return new ExceptionHandlersImplementer();
    }

}
