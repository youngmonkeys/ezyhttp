package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.constant.Constants;
import com.tvd12.ezyhttp.server.core.annotation.Controller;

public final class ControllerApplications {

	private ControllerApplications() {}

	public static String getURI(Controller annotation) {
		String uri = annotation.value();
		if(EzyStrings.isNoContent(uri))
			uri = annotation.uri();
		if(EzyStrings.isNoContent(uri))
			uri = Constants.DEFAULT_URI;
		return uri;
	}
	
}
