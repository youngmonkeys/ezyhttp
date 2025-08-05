package com.tvd12.ezyhttp.server.core.reflect;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.annotation.*;
import com.tvd12.ezyhttp.server.core.util.DoDeleteAnnotations;
import com.tvd12.ezyhttp.server.core.util.DoGetAnnotations;
import com.tvd12.ezyhttp.server.core.util.DoPostAnnotations;
import com.tvd12.ezyhttp.server.core.util.DoPutAnnotations;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyhttp.core.net.URIBuilder.normalizePath;

@Getter
public class RequestHandlerMethod extends HandlerMethod {

    protected final String rootURI;
    protected final String requestURI;
    protected final String responseType;
    protected final HttpMethod httpMethod;

    private RequestHandlerMethod(
        String rootURI,
        String requestURI,
        HttpMethod httpMethod,
        String responseType,
        EzyMethod method
    ) {
        super(method);
        this.rootURI = rootURI;
        this.httpMethod = httpMethod;
        this.responseType = responseType;
        this.requestURI = normalizePath(rootURI + requestURI);
    }

    public RequestHandlerMethod(String rootURI, EzyMethod method) {
        super(method);
        this.rootURI = rootURI;
        this.requestURI = fetchRequestURI(rootURI);
        this.httpMethod = fetchHttpMethod();
        this.responseType = fetchResponseType();
    }

    protected String fetchRequestURI(String rootURI) {
        String uri = rootURI + fetchRequestURIFragment();
        return normalizePath(uri);
    }

    protected String fetchRequestURIFragment() {
        DoGet doGet = method.getAnnotation(DoGet.class);
        if (doGet != null) {
            return DoGetAnnotations.getURI(doGet);
        }
        DoPost doPost = method.getAnnotation(DoPost.class);
        if (doPost != null) {
            return DoPostAnnotations.getURI(doPost);
        }
        DoPut doPut = method.getAnnotation(DoPut.class);
        if (doPut != null) {
            return DoPutAnnotations.getURI(doPut);
        }
        DoDelete doDelete = method.getAnnotation(DoDelete.class);
        return DoDeleteAnnotations.getURI(doDelete);
    }

    protected HttpMethod fetchHttpMethod() {
        DoGet doGet = method.getAnnotation(DoGet.class);
        if (doGet != null) {
            return HttpMethod.GET;
        }
        DoPost doPost = method.getAnnotation(DoPost.class);
        if (doPost != null) {
            return HttpMethod.POST;
        }
        DoPut doPut = method.getAnnotation(DoPut.class);
        if (doPut != null) {
            return HttpMethod.PUT;
        }
        return HttpMethod.DELETE;
    }

    protected String fetchResponseType() {
        DoGet doGet = method.getAnnotation(DoGet.class);
        if (doGet != null) {
            return DoGetAnnotations.getResponseType(doGet);
        }
        DoPost doPost = method.getAnnotation(DoPost.class);
        if (doPost != null) {
            return DoPostAnnotations.getResponseType(doPost);
        }
        DoPut doPut = method.getAnnotation(DoPut.class);
        if (doPut != null) {
            return DoPutAnnotations.getResponseType(doPut);
        }
        DoDelete doDelete = method.getAnnotation(DoDelete.class);
        return DoDeleteAnnotations.getResponseType(doDelete);
    }

    public List<RequestHandlerMethod> duplicatedToOtherRequestHandlerMethods() {
        return newArrayList(
            fetchOtherRequestURIs(),
            it -> new RequestHandlerMethod(
                rootURI,
                it,
                httpMethod,
                responseType,
                method
            )
        );
    }

    protected Set<String> fetchOtherRequestURIs() {
        DoGet doGet = method.getAnnotation(DoGet.class);
        if (doGet == null) {
            return Collections.emptySet();
        }
        return Stream
            .of(doGet.otherUris())
            .filter(EzyStrings::isNotBlank)
            .collect(Collectors.toSet());
    }

    public boolean isApi() {
        return method.isAnnotated(Api.class);
    }

    public boolean isAuthenticated() {
        return method.isAnnotated(Authenticated.class);
    }

    public boolean isAuthenticatable() {
        return method.isAnnotated(Authenticatable.class);
    }

    public boolean isAsync() {
        return method.isAnnotated(Async.class);
    }

    public boolean isManagement() {
        return method.isAnnotated(EzyManagement.class);
    }

    public boolean isPayment() {
        return method.isAnnotated(EzyPayment.class);
    }

    public String getFeature() {
        EzyFeature annotation = method.getAnnotation(EzyFeature.class);
        return annotation != null ? annotation.value() : null;
    }

    @Override
    public String toString() {
        return method.getName() +
            "(" +
            "uri: " + requestURI +
            ")";
    }
}
