package com.tvd12.ezyhttp.server.core.asm;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import com.tvd12.ezyfox.asm.EzyFunction;
import com.tvd12.ezyfox.asm.EzyFunction.EzyBody;
import com.tvd12.ezyfox.asm.EzyInstruction;
import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyClassTree;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyfox.reflect.EzyMethods;
import com.tvd12.ezyfox.reflect.EzyReflections;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerProxy;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import lombok.Setter;

public class ExceptionHandlerImplementer
    extends AbstractHandlerImplementer<ExceptionHandlerMethod> {

    @Setter
    private static boolean debug;
    protected final ExceptionHandlerProxy exceptionHandler;

    protected static final AtomicInteger COUNT = new AtomicInteger(0);

    public ExceptionHandlerImplementer(
        ExceptionHandlerProxy exceptionHandler,
        ExceptionHandlerMethod handlerMethod
    ) {
        super(handlerMethod);
        this.exceptionHandler = exceptionHandler;
    }

    public UncaughtExceptionHandler implement() {
        try {
            return doImplement();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    protected UncaughtExceptionHandler doImplement() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        String implClassName = getImplClassName();
        CtClass implClass = pool.makeClass(implClassName);
        EzyClass superClass = new EzyClass(getSuperClass());
        String exceptionHandlerFieldContent = makeExceptionHandlerFieldContent();
        String setExceptionHandlerMethodContent = makeSetExceptionHandlerMethodContent();
        String handleExceptionMethodContent = makeHandleExceptionMethodContent();
        String getResponseContentTypeMethodContent = makeGetResponseContentTypeMethodContent();
        printComponentContent(implClassName);
        printComponentContent(exceptionHandlerFieldContent);
        printComponentContent(setExceptionHandlerMethodContent);
        printComponentContent(handleExceptionMethodContent);
        printComponentContent(getResponseContentTypeMethodContent);
        implClass.setSuperclass(pool.get(superClass.getName()));
        implClass.addField(CtField.make(exceptionHandlerFieldContent, implClass));
        implClass.addMethod(CtNewMethod.make(setExceptionHandlerMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(handleExceptionMethodContent, implClass));
        implClass.addMethod(CtNewMethod.make(getResponseContentTypeMethodContent, implClass));
        Class answerClass = implClass.toClass();
        implClass.detach();
        AsmUncaughtExceptionHandler handler = (AsmUncaughtExceptionHandler)
            answerClass.newInstance();
        setRepoComponent(handler);
        return handler;
    }

    protected void setRepoComponent(AsmUncaughtExceptionHandler handler) {
        handler.setExceptionHandler(exceptionHandler.getInstance());
    }

    protected String makeExceptionHandlerFieldContent() {
        return new EzyInstruction()
            .append("private ")
            .append(exceptionHandler.getClazz().getName())
            .append(" exceptionHandler")
            .toString();
    }

    protected String makeSetExceptionHandlerMethodContent() {
        return new EzyFunction(getSetExceptionHandlerMethod())
            .body()
            .append(new EzyInstruction("\t", "\n")
                .append("this.exceptionHandler")
                .equal()
                .brackets(exceptionHandler.getClazz().getClazz())
                .append("arg0"))
            .function()
            .toString();
    }

    protected String makeHandleExceptionMethodContent() {
        EzyMethod method = getHandleExceptionMethod();
        EzyFunction function = new EzyFunction(method);
        EzyBody body = function.body();
        Class<?>[] exceptionClasses = handlerMethod.getExceptionClasses();
        EzyClassTree exceptionTree = new EzyClassTree(exceptionClasses);
        for (Class<?> exceptionClass : exceptionTree.toList()) {
            EzyInstruction instructionIf = new EzyInstruction("\t", "\n", false)
                .append("if (arg1 instanceof ")
                .append(exceptionClass.getName())
                .append(") {");
            body.append(instructionIf);
            EzyInstruction instructionHandle = new EzyInstruction("\t\t", "\n");
            Class<?> returnType = handlerMethod.getReturnType();
            if (returnType != void.class) {
                instructionHandle.answer();
            }
            instructionHandle
                .append("this.exceptionHandler.").append(handlerMethod.getName())
                .bracketopen();
            appendHandleExceptionMethodArguments(handlerMethod, instructionHandle, exceptionClass);
            instructionHandle.bracketclose();
            body.append(instructionHandle);
            if (returnType == void.class) {
                body.append(new EzyInstruction("\t\t", "\n").append("return null"));
            }
            body.append(new EzyInstruction("\t", "\n", false).append("}"));
        }
        body.append(new EzyInstruction("\t", "\n").append("throw arg1"));
        return toThrowExceptionFunction(method, function);
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

    protected String toThrowExceptionFunction(EzyMethod method, EzyFunction function) {
        return method.getDeclaration(EzyReflections.MODIFIER_PUBLIC) +
            " throws Exception {\n" +
            function.body() +
            "}";
    }

    protected EzyMethod getSetExceptionHandlerMethod() {
        Method method = EzyMethods.getMethod(
            AsmAbstractUncaughtExceptionHandler.class, "setExceptionHandler", Object.class);
        return new EzyMethod(method);
    }

    protected EzyMethod getHandleExceptionMethod() {
        Method method = EzyMethods.getMethod(
            AsmAbstractUncaughtExceptionHandler.class,
            "handleException",
            RequestArguments.class, Exception.class);
        return new EzyMethod(method);
    }

    protected EzyMethod getGetResponseContentTypeMethod() {
        Method method = EzyMethods.getMethod(
            AsmAbstractUncaughtExceptionHandler.class, "getResponseContentType");
        return new EzyMethod(method);
    }

    protected Class<?> getSuperClass() {
        return AsmAbstractUncaughtExceptionHandler.class;
    }

    protected String getImplClassName() {
        return exceptionHandler.getClassSimpleName()
            + "$" + handlerMethod.getName() + "$ExceptionHandler$AutoImpl$"
            + COUNT.incrementAndGet();
    }

    protected void printComponentContent(String componentContent) {
        if (debug) {
            logger.debug("component content: \n{}", componentContent);
        }
    }
}
