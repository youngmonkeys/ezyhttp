package com.tvd12.ezyhttp.server.core.asm;

import static com.tvd12.ezyfox.io.EzyStrings.quote;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.tvd12.ezyfox.asm.EzyFunction;
import com.tvd12.ezyfox.asm.EzyFunction.EzyBody;
import com.tvd12.ezyfox.asm.EzyInstruction;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyClassTree;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyfox.reflect.EzyMethods;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.annotation.PathVariable;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestCookie;
import com.tvd12.ezyhttp.server.core.annotation.RequestHeader;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.handler.AbstractRequestHandler;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.RequestURIDecorator;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.RequestParameters;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.util.PathVariableAnnotations;
import com.tvd12.ezyhttp.server.core.util.RequestCookieAnnotations;
import com.tvd12.ezyhttp.server.core.util.RequestHeaderAnnotations;
import com.tvd12.ezyhttp.server.core.util.RequestParamAnnotations;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import lombok.Setter;

public class RequestHandlerImplementer 
        extends AbstractHandlerImplementer<RequestHandlerMethod> {

    @Setter
    private static boolean debug;
    protected final boolean isAsync;
    protected final ControllerProxy controller;
    @Setter
    protected RequestURIDecorator requestURIDecorator;

    protected final static String PARAMETER_PREFIX = "param";
    protected final static AtomicInteger COUNT = new AtomicInteger(0);

    public RequestHandlerImplementer(
            ControllerProxy controller, RequestHandlerMethod handlerMethod) {
        super(handlerMethod);
        this.controller = controller;
        this.isAsync = handlerMethod.isAsync();
    }

    public RequestHandler implement() {
        try {
            return doimplement();
        }
        catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    protected RequestHandler doimplement() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        String implClassName = getImplClassName();
        CtClass implClass = pool.makeClass(implClassName);
        EzyClass superClass = new EzyClass(getSuperClass());
        String controllerFieldContent = makeControllerFieldContent();
        String setControllerMethodContent = makeSetControllerMethodContent();
        String handleRequestMethodContent = makeHandleRequestMethodContent();
        String handleExceptionMethodContent = makeHandleExceptionMethodContent();
        String getHttpMethodMethodContent = makeGetHttpMethodMethodContent();
        String getRequestURIMethodContent = makeGetRequestURIMethodContent();
        String getResponseContentTypeMethodContent = makeGetResponseContentTypeMethodContent();
        String isAsyncMethodContent = makeIsAsynceMethodContent();
        printComponentContent(controllerFieldContent);
        printComponentContent(setControllerMethodContent);
        printComponentContent(handleRequestMethodContent);
        printComponentContent(handleExceptionMethodContent);
        printComponentContent(getHttpMethodMethodContent);
        printComponentContent(getRequestURIMethodContent);
        printComponentContent(getResponseContentTypeMethodContent);
        printComponentContent(isAsyncMethodContent);
        implClass.setSuperclass(pool.get(superClass.getName()));
        implClass.addField(CtField.make(controllerFieldContent, implClass));
        implClass.addMethod(CtNewMethod.make(setControllerMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(handleRequestMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(handleExceptionMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(getHttpMethodMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(getRequestURIMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(getResponseContentTypeMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(isAsyncMethodContent, implClass));
        Class answerClass = implClass.toClass();
        implClass.detach();
        RequestHandler handler = (RequestHandler) answerClass.newInstance();
        handler.setHandlerMethod(handlerMethod.getMethod().getMethod());
        setRepoComponent(handler);
        return handler;
    }

    protected void setRepoComponent(RequestHandler handler) {
        handler.setController(controller.getInstance());
    }

    protected String makeControllerFieldContent() {
        return new EzyInstruction()
                .append("private ")
                    .append(controller.getClazz().getName())
                        .append(" controller")
                .toString();
    }

    protected String makeSetControllerMethodContent() {
        return new EzyFunction(getSetControllerMethod())
                .body()
                    .append(new EzyInstruction("\t", "\n")
                            .append("this.controller")
                            .equal()
                            .brackets(controller.getClazz().getClazz())
                            .append("arg0"))
                    .function()
                .toString();
    }

    protected String makeHandleRequestMethodContent() {
        EzyMethod method = getHandleRequestMethod();
        EzyFunction function = new EzyFunction(method)
                .throwsException();
        EzyBody body = function.body();
        int paramCount = 0;
        int headerCount = 0;
        int parameterCount = 0;
        int pathVariableCount = 0;
        int cookieCount = 0;
        Parameter[] parameters = handlerMethod.getParameters();
        for (Parameter parameter : parameters) {
            Class<?> parameterType = parameter.getType();
            Class<?> genericType = getGenericType(parameter);
            String genericTypeClass = genericType != null
                    ? genericType.getName() + ".class"
                    : "null";
            EzyInstruction instruction = new EzyInstruction("\t", "\n")
                    .clazz(parameterType)
                    .append(" ").append(PARAMETER_PREFIX).append(paramCount)
                    .equal();
            boolean hasAnnotation = false;
            RequestParam requestParamAnno = parameter.getAnnotation(RequestParam.class);
            if (requestParamAnno != null) {
                String paramKey = RequestParamAnnotations
                        .getParamKeyString(requestParamAnno, parameterCount);
                String defaultValue = requestParamAnno.defaultValue();
                String getValueExpression = defaultValue.equals(EzyStrings.NULL)
                   ? "arg0.getParameter(" + paramKey + ")"
                   : "arg0.getParameter(" + paramKey + ", " + quote(defaultValue) + ")";
                String valueExpression = "this.deserializeParameter(" +
                        paramKey +
                        ", " + getValueExpression +
                        ", " + parameterType.getTypeName() + ".class" +
                        ", " + genericTypeClass + ")" ;
                instruction
                    .cast(parameterType, valueExpression);
                ++ parameterCount;
                hasAnnotation = true;
            }
            RequestHeader requestHeaderAnno = parameter.getAnnotation(RequestHeader.class);
            if (requestHeaderAnno != null) {
                String headerKey = RequestHeaderAnnotations
                        .getHeaderKeyString(requestHeaderAnno, headerCount);
                String defaultValue = requestHeaderAnno.defaultValue();
                String getValueExpression = defaultValue.equals(EzyStrings.NULL)
                   ? "arg0.getHeader(" + headerKey + ")"
                   : "arg0.getHeader(" + headerKey + ", " + quote(defaultValue) + ")";
                String valueExpression = "this.deserializeHeader(" +
                        headerKey +
                        ", " + getValueExpression +
                        ", " + parameterType.getTypeName() + ".class" +
                        ", " + genericTypeClass + ")" ;
                instruction
                    .cast(parameterType, valueExpression);
                ++ headerCount;
                hasAnnotation = true;
            }
            PathVariable pathVariableAnno = parameter.getAnnotation(PathVariable.class);
            if (pathVariableAnno != null) {
                String varNameKey = PathVariableAnnotations
                        .getVariableNameKeyString(pathVariableAnno, pathVariableCount);
                String valueExpression = "this.deserializePathVariable(" +
                        varNameKey +
                        ", arg0.getPathVariable(" + varNameKey + ")" +
                        ", " + parameterType.getTypeName() + ".class" +
                        ", " + genericTypeClass + ")" ;
                instruction
                    .cast(parameterType, valueExpression);
                ++ pathVariableCount;
                hasAnnotation = true;
            }
            RequestCookie requestCookieAnno = parameter.getAnnotation(RequestCookie.class);
            if (requestCookieAnno != null) {
                String cookieKey = RequestCookieAnnotations
                        .getCookieKeyString(requestCookieAnno, cookieCount);
                String defaultValue = requestCookieAnno.defaultValue();
                String getValueExpression = defaultValue.equals(EzyStrings.NULL)
                   ? "arg0.getCookieValue(" + cookieKey + ")"
                   : "arg0.getCookieValue(" + cookieKey + ", " + quote(defaultValue) + ")";
                String valueExpression = "this.deserializeCookie(" +
                        cookieKey +
                        ", " + getValueExpression +
                        ", " + parameterType.getTypeName() + ".class" +
                        ", " + genericTypeClass + ")" ;
                instruction
                    .cast(parameterType, valueExpression);
                ++ cookieCount;
                hasAnnotation = true;
            }
            RequestBody requestBodyAnno = parameter.getAnnotation(RequestBody.class);
            if (requestBodyAnno != null) {
                instruction
                    .brackets(parameterType)
                    .append("this.deserializeBody(")
                        .append("arg0, ").clazz(parameterType, true)
                    .append(")");
                hasAnnotation = true;
            }
            if (!hasAnnotation) {
                String valueExpression = "arg0";
                if (parameterType != RequestArguments.class) {
                    String argumentKey = RequestParameters.getArgumentKeyString(parameter);
                    valueExpression = "arg0.getArgument(" + argumentKey + ")";
                }
                instruction.cast(parameterType, valueExpression);
            }
            body.append(instruction);
            ++ paramCount;

        }
        EzyInstruction instruction = new EzyInstruction("\t", "\n");
        Class<?> returnType = handlerMethod.getReturnType();
        if (returnType != void.class)
            instruction.answer();
        StringBuilder answerExpression = new StringBuilder();
        answerExpression.append("this.controller.").append(handlerMethod.getName())
                .append("(");
        for (int i = 0 ; i < paramCount ; ++i) {
            answerExpression.append(PARAMETER_PREFIX).append(i);
            if (i < paramCount - 1)
                answerExpression.append(", ");
        }
        answerExpression.append(")");
        if (returnType != void.class)
            instruction.valueOf(returnType, answerExpression.toString());
        else
            instruction.append(answerExpression);
        body.append(instruction);
        if (returnType == void.class)
            body.append(new EzyInstruction("\t", "\n").append("return null"));
        return function.toString();
    }

    protected String makeHandleExceptionMethodContent() {
        EzyMethod method = getHandleExceptionMethod();
        EzyFunction function = new EzyFunction(method)
                .throwsException();
        EzyBody body = function.body();
        Map<Class<?>, ExceptionHandlerMethod> exceptionHandlerMethodMap
                = controller.getExceptionHandlerMethodMap();
        Set<Class<?>> exceptionClasses = exceptionHandlerMethodMap.keySet();
        EzyClassTree exceptionTree = new EzyClassTree(exceptionClasses);
        for (Class<?> exceptionClass : exceptionTree.toList()) {
            ExceptionHandlerMethod m = exceptionHandlerMethodMap.get(exceptionClass);
            EzyInstruction instructionIf = new EzyInstruction("\t", "\n", false)
                    .append("if (arg1 instanceof ")
                        .append(exceptionClass.getName())
                    .append(") {");
            body.append(instructionIf);
            EzyInstruction instructionHandle = new EzyInstruction("\t\t", "\n");
            Class<?> returnType = m.getReturnType();
            if (returnType != void.class)
                instructionHandle.answer();
            instructionHandle
                    .append("this.controller.").append(m.getName())
                    .bracketopen();
            appendHandleExceptionMethodArguments(m, instructionHandle, exceptionClass);
            instructionHandle
                    .bracketclose();
            body.append(instructionHandle);
            if (returnType == void.class)
                body.append(new EzyInstruction("\t\t", "\n").append("return null"));
            body.append(new EzyInstruction("\t", "\n", false).append("}"));
        }
        body.append(new EzyInstruction("\t", "\n").append("throw arg1"));
        return function.toString();
    }

    protected String makeGetHttpMethodMethodContent() {
        HttpMethod httpMethod = handlerMethod.getHttpMethod();
        return new EzyFunction(getGetHttpMethodMethod())
                .body()
                    .append(new EzyInstruction("\t", "\n")
                            .answer()
                            .append(httpMethod.getDeclaringClass().getName())
                            .dot()
                            .append(httpMethod))
                    .function()
                .toString();
    }

    protected String makeGetRequestURIMethodContent() {
        String requestURI = handlerMethod.getRequestURI();
        if (requestURIDecorator != null) {
            requestURI = requestURIDecorator.decorate(controller.getClazz(), requestURI);
        }
        return new EzyFunction(getGetRequestURIMethod())
                .body()
                    .append(new EzyInstruction("\t", "\n")
                            .answer()
                            .string(requestURI))
                    .function()
                .toString();
    }

    protected String makeGetResponseContentTypeMethodContent() {
        return new EzyFunction(getGetResponseContentTypeMethod())
                .body()
                    .append(new EzyInstruction("\t", "\n")
                            .answer()
                            .string(handlerMethod.getResponseType()))
                    .function()
                .toString();
    }

    protected String makeIsAsynceMethodContent() {
        return new EzyFunction(getIsAsyncMethod())
                .body()
                    .append(new EzyInstruction("\t", "\n")
                            .answer()
                            .append(isAsync))
                    .function()
                .toString();
    }

    protected Class<?> getGenericType(Parameter parameter) {
        Type parameterizedType = parameter.getParameterizedType();
        if (parameterizedType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) parameterizedType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            return (Class<?>)parameterArgTypes[0];
        }
        return null;
    }

    protected EzyMethod getSetControllerMethod() {
        Method method = EzyMethods.getMethod(
                AbstractRequestHandler.class, "setController", Object.class);
        return new EzyMethod(method);
    }

    protected EzyMethod getHandleRequestMethod() {
        Method method = EzyMethods.getMethod(
                AbstractRequestHandler.class, "handleRequest", RequestArguments.class);
        return new EzyMethod(method);
    }

    protected EzyMethod getHandleExceptionMethod() {
        Method method = EzyMethods.getMethod(
                AbstractRequestHandler.class,
                "handleException",
                RequestArguments.class, Exception.class);
        return new EzyMethod(method);
    }

    protected EzyMethod getGetHttpMethodMethod() {
        Method method = EzyMethods.getMethod(
                AbstractRequestHandler.class, "getMethod");
        return new EzyMethod(method);
    }

    protected EzyMethod getGetRequestURIMethod() {
        Method method = EzyMethods.getMethod(
                AbstractRequestHandler.class, "getRequestURI");
        return new EzyMethod(method);
    }

    protected EzyMethod getGetResponseContentTypeMethod() {
        Method method = EzyMethods.getMethod(
                AbstractRequestHandler.class, "getResponseContentType");
        return new EzyMethod(method);
    }

    protected EzyMethod getIsAsyncMethod() {
        Method method = EzyMethods.getMethod(
                AbstractRequestHandler.class, "isAsync");
        return new EzyMethod(method);
    }

    protected Class<?> getSuperClass() {
        return AbstractRequestHandler.class;
    }

    protected String getImplClassName() {
        return controller.getControllerName()
                + "$" + handlerMethod.getName() + "$Handler$AutoImpl$" + COUNT.incrementAndGet();
    }

    protected void printComponentContent(String componentContent) {
        if (debug)
            logger.debug("component content: \n{}", componentContent);
    }

}
