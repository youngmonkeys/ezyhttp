package com.tvd12.ezyhttp.server.core.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.view.View;

public interface UnhandledErrorHandler {

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
	    if (result != null) {
	        if (result instanceof ResponseEntity) {
	            response.setContentType(((ResponseEntity)result).getContentType());
	        }
	        else if (result instanceof View) {
	            response.setContentType(ContentTypes.TEXT_HTML_UTF8);
	        }
	        if (EzyStrings.isNoContent(response.getContentType())) {
	            response.setContentType(ContentTypes.APPLICATION_JSON);
	        }
	    }
	    return result;
	}
}
