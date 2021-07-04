package com.tvd12.ezyhttp.core.util;

public final class FileSizes {

	private FileSizes() {}
	
	public static long toByteSize(String value) {
		String lowercase = value.toLowerCase();
		if(value.length() > 2) {
			if(lowercase.endsWith("kb"))
				return Long.valueOf(value.substring(0, value.length() - 2)) * 1024;
			if(lowercase.endsWith("mb"))
				return Long.valueOf(value.substring(0, value.length() - 2)) * 1024 * 1024;
			if(lowercase.endsWith("gb"))
				return Long.valueOf(value.substring(0, value.length() - 2)) * 1024 * 1024 * 1024;
			if(lowercase.endsWith("tb"))
				return Long.valueOf(value.substring(0, value.length() - 2)) * 1024 * 1024 * 1024;
		}
		else if(lowercase.endsWith("b")) {
			return Long.valueOf(value.substring(0, value.length() - 1));
		}
		throw new IllegalArgumentException("size must follow template: [value][B|KB|MB|GB|TB]");
	}
	
}
