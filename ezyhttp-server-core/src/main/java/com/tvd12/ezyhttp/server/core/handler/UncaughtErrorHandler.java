package com.tvd12.ezyhttp.server.core.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.view.View;

public interface UncaughtErrorHandler {

    default Object processError(int errorStatusCode) {
        return null;
    }
    
	default Object processError(
	    HttpMethod method,
        HttpServletRequest request,
        HttpServletResponse response,
        int errorStatusCode
    ) {
	    return processError(errorStatusCode);
	}
	
	default Object handleError(
        HttpMethod method,
        HttpServletRequest request,
        HttpServletResponse response,
        int errorStatusCode
    ) {
	    Object result = processError(method, request, response, errorStatusCode);
	    if (request instanceof ResponseEntity) {
	        response.setContentType(((ResponseEntity)result).getContentType());
	    }
	    else if (request instanceof View) {
	        response.setContentType(ContentTypes.TEXT_HTML_UTF8);
	    }
	    return result;
	}
}
