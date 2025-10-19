package com.tvd12.ezyhttp.server.core.reflect;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.Api;
import com.tvd12.ezyhttp.server.core.annotation.Authenticatable;
import com.tvd12.ezyhttp.server.core.annotation.Authenticated;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.handler.*;
import com.tvd12.ezyhttp.server.core.util.ControllerAnnotations;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.reflect.EzyClasses.isAnnotationPresentIncludeSuper;
import static com.tvd12.ezyhttp.server.core.annotation.Annotations.REQUEST_HANDLER_ANNOTATIONS;

@Getter
@SuppressWarnings({"unchecked", "rawtypes"})
public class ControllerProxy {

    protected final EzyClass clazz;
    protected final Object instance;
    protected final String requestURI;
    protected final List<RequestHandlerMethod> requestHandlerMethods;
    protected final List<ExceptionHandlerMethod> exceptionHandlerMethods;
    protected final Map<Class<?>, ExceptionHandlerMethod> exceptionHandlerMethodMap;

    public ControllerProxy(Object instance) {
        this.instance = instance;
        this.clazz = new EzyClass(instance.getClass());
        this.requestURI = getRequestURI();
        this.requestHandlerMethods = fetchRequestHandlerMethods();
        this.exceptionHandlerMethods = fetchExceptionHandlerMethods();
        this.exceptionHandlerMethodMap = fetchExceptionHandlerMethodMap();
    }

    protected String getRequestURI() {
        return ControllerAnnotations.getURI(instance);
    }

    protected List<RequestHandlerMethod> fetchRequestHandlerMethods() {
        List<RequestHandlerMethod> list = new ArrayList<>();
        List<EzyMethod> methods = clazz.getPublicMethods(this::isRequestHandlerMethod);
        for (EzyMethod method : methods) {
            RequestHandlerMethod m = new RequestHandlerMethod(requestURI, method);
            list.add(m);
        }
        return list;
    }

    public boolean isManagement() {
        boolean answer = instance instanceof ManagementController
            || isAnnotationPresent(EzyManagement.class);
        if (!answer && instance instanceof ManageableController) {
            answer = ((ManageableController) instance).isManagement();
        }
        return answer;
    }

    public boolean isApi() {
        boolean answer = isAnnotationPresent(Api.class);
        if (!answer && instance instanceof ApiController) {
            answer = ((ApiController) instance).isApi();
        }
        return answer;
    }

    public boolean isAuthenticated() {
        boolean answer = isAnnotationPresent(Authenticated.class);
        if (!answer && instance instanceof AuthenticatedController) {
            answer = ((AuthenticatedController) instance).isAuthenticated();
        }
        return answer;
    }

    public boolean isAuthenticatable() {
        boolean answer = isAnnotationPresent(Authenticatable.class);
        if (!answer && instance instanceof AuthenticatableController) {
            answer = ((AuthenticatableController) instance).isAuthenticatable();
        }
        return answer;
    }

    public boolean isPayment() {
        boolean answer = isAnnotationPresent(EzyPayment.class);
        if (!answer && instance instanceof ManageableController) {
            answer = ((PaymentController) instance).isPayment();
        }
        return answer;
    }

    public String getFeature() {
        EzyFeature annotation = clazz.getAnnotation(EzyFeature.class);
        String answer = annotation != null ? annotation.value() : null;
        if (isBlank(answer) && instance instanceof ManageableController) {
            answer = ((FeatureController) instance).getFeature();
        }
        return answer;
    }

    public List<ExceptionHandlerMethod> fetchExceptionHandlerMethods() {
        List<ExceptionHandlerMethod> list = new ArrayList<>();
        List<EzyMethod> methods = clazz.getMethods(m -> m.isAnnotated(TryCatch.class));
        for (EzyMethod method : methods) {
            ExceptionHandlerMethod m = new ExceptionHandlerMethod(method);
            list.add(m);
        }
        return list;
    }

    protected final Map<Class<?>, ExceptionHandlerMethod> fetchExceptionHandlerMethodMap() {
        Map<Class<?>, ExceptionHandlerMethod> answer = new HashMap<>();
        for (ExceptionHandlerMethod m : exceptionHandlerMethods) {
            for (Class<?> exceptionClass : m.getExceptionClasses()) {
                answer.put(exceptionClass, m);
            }
        }
        return answer;
    }

    protected boolean isRequestHandlerMethod(EzyMethod method) {
        for (Class annClass : REQUEST_HANDLER_ANNOTATIONS) {
            Annotation annotation = method.getAnnotation(annClass);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }

    public String getControllerName() {
        return clazz.getClazz().getSimpleName();
    }

    private boolean isAnnotationPresent(
        Class<? extends Annotation> annotationClass
    ) {
        return isAnnotationPresentIncludeSuper(
            clazz.getClazz(),
            annotationClass
        );
    }

    @Override
    public String toString() {
        return clazz.getName() +
            "(\n" +
            "\tinstance: " + instance + ",\n" +
            "\trequestHandlerMethods: " + requestHandlerMethods + ",\n" +
            "\texceptionHandlerMethods: " + exceptionHandlerMethods + "\n" +
            ")";
    }
}
