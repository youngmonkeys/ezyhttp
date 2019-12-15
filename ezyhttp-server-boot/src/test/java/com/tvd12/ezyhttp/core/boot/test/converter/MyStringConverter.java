package com.tvd12.ezyhttp.core.boot.test.converter;

import java.util.List;

import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.ezyhttp.core.annotation.StringConvert;
import com.tvd12.ezyhttp.core.codec.DefaultStringDeserializer;

@StringConvert
public class MyStringConverter extends DefaultStringDeserializer {

	public MyStringConverter() {
		super();
		this.mappers.put(List.class, v -> {
			String[] strs = v.split(",");
			return Lists.newArrayList(strs);
		});
	}
	
}
