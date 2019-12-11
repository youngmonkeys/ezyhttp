package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.server.core.annotation.DoDelete;
import com.tvd12.ezyhttp.server.core.constant.Constants;
import com.tvd12.ezyhttp.server.core.constant.ContentTypes;

public final class DoDeleteAnnotations {

	private DoDeleteAnnotations() {}
	
	public static String getURI(DoDelete annotation) {
		String uri = annotation.value();
		if(EzyStrings.isNoContent(uri))
			uri = annotation.uri();
		if(EzyStrings.isNoContent(uri))
			uri = Constants.EMPTY_STRING;
		return uri;
	}
	
	public static String getResponseType(DoDelete annotation) {
		String responseType = annotation.responseType();
		if(EzyStrings.isNoContent(responseType))
			responseType = ContentTypes.APPLICATION_JSON;
		return responseType;
	}
}
