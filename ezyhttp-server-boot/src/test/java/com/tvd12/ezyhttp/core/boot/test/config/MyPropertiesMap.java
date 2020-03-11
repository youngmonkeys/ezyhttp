package com.tvd12.ezyhttp.core.boot.test.config;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.bean.EzyPropertiesMap;

public class MyPropertiesMap implements EzyPropertiesMap {

	@Override
	public Map<String, String> keyMap() {
		Map<String, String> map = new HashMap<>();
		map.put("webserver.port", "server.port");
		return map;
	}

}
