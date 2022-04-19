package com.tvd12.ezyhttp.server.core.handler;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.AsyncContext;

import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.codec.StringDeserializer;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.ezyhttp.core.exception.DeserializeBodyException;
import com.tvd12.ezyhttp.core.exception.DeserializeCookieException;
import com.tvd12.ezyhttp.core.exception.DeserializeHeaderException;
import com.tvd12.ezyhttp.core.exception.DeserializeParameterException;
import com.tvd12.ezyhttp.core.exception.DeserializePathVariableException;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import lombok.Getter;
import lombok.Setter;

import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;

public abstract class AbstractRequestHandler implements RequestHandler {

    @Setter
    @Getter
    protected Method handlerMethod;
    protected final DataConverters dataConverters;
    protected final ComponentManager componentManager;

    public AbstractRequestHandler() {
        this.componentManager = ComponentManager.getInstance();
        this.dataConverters = componentManager.getDataConverters();
    }

    @Override
    public final Object handle(
        RequestArguments arguments
    ) throws Exception {
        try {
            return handleRequest(arguments);
        }
        catch (Exception e) {
            try {
                return handleException(arguments, e);
            }
            finally {
                if (arguments.isAsyncStarted()) {
                    AsyncContext asyncContext = arguments.getAsyncContext();
                    processWithLogException(asyncContext::complete);
                }
            }
        }
    }

    protected abstract Object handleRequest(
        RequestArguments arguments
    ) throws Exception;

    protected abstract Object handleException(
        RequestArguments arguments,
        Exception e
    ) throws Exception;

    protected <T> T deserializeHeader(
        int index,
        String value,
        Class<T> type, Class<?> genericType
    ) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializeHeaderException("header#" + index, value, type, e);
        }
    }

    protected <T> T deserializeHeader(
        String name,
        String value,
        Class<T> type, Class<?> genericType
    ) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializeHeaderException(name, value, type, e);
        }
    }

    protected <T> T deserializeParameter(
        int index,
        String value,
        Class<T> type, Class<?> genericType
    ) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializeParameterException("parameter#" + index, value, type, e);
        }
    }

    protected <T> T deserializeParameter(
            String name,
            String value,
            Class<T> type, Class<?> genericType) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializeParameterException(name, value, type, e);
        }
    }

    protected <T> T deserializePathVariable(
        String name,
        String value,
        Class<T> type, Class<?> genericType
    ) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializePathVariableException(name, value, type, e);
        }
    }

    protected <T> T deserializePathVariable(
        int index,
        String value,
        Class<T> type, Class<?> genericType
    ) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializePathVariableException("pathVariable#" + index, value, type, e);
        }
    }

    protected <T> T deserializeCookie(
        int index,
        String value,
        Class<T> type, Class<?> genericType
    ) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializeCookieException("cookie#" + index, value, type, e);
        }
    }

    protected <T> T deserializeCookie(
        String name,
        String value,
        Class<T> type, Class<?> genericType
    ) throws IOException {
        try {
            StringDeserializer deserializer = dataConverters.getStringDeserializer();
            return deserializer.deserialize(value, type, genericType);
        }
        catch (Exception e) {
            throw new DeserializeCookieException(name, value, type, e);
        }
    }

    protected <T> T deserializeBody(
        BodyData bodyData,
        Class<T> type
    ) throws IOException {
        String contentType = bodyData.getContentType();
        if (contentType == null)
            throw new HttpBadRequestException("contentType is null");
        BodyDeserializer deserializer = dataConverters.getBodyDeserializer(contentType);
        try {
            return deserializer.deserialize(bodyData, type);
        }
        catch (Exception e) {
            throw new DeserializeBodyException("can't deserialize body data to: " + type.getName(), e);
        }
    }
}
