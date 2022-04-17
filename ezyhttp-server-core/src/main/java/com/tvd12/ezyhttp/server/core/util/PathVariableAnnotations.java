package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.server.core.annotation.PathVariable;

public final class PathVariableAnnotations {

	private PathVariableAnnotations() {}
	
	public static String getVariableNameKeyString(PathVariable annotation, int index) {
		String keyString = annotation.value();
		if (EzyStrings.isNoContent(keyString))
			keyString = String.valueOf(index);
		else
			keyString = EzyStrings.quote(keyString);
		return keyString;
	}
	
}
