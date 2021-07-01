package com.tvd12.ezyhttp.server.core.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Redirect {

	private final String uri;
	
	public static Redirect to(String uri) {
		return new Redirect(uri);
	}
	
	public boolean isURL() {
		return uri.startsWith("http://") || uri.startsWith("https://");
	}
	
}
