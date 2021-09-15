package com.tvd12.ezyhttp.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.io.EzyStrings;

public class MultiValueMap {

	protected final Map<String, List<String>> map;
	
	public MultiValueMap(Map<String, List<String>> map) {
		this.map = map;
	}
	
	public static MultiValueMap of(Map<String, List<String>> map) {
		return new MultiValueMap(map);
	}
	
	public Set<String> keySets() {
		return map.keySet();
	}
	
	public String getValue(String key) {
		List<String> values = map.get(key);
		if(values == null || values.isEmpty())
			return null;
		String value = values.get(0);
		return value;
	}
	
	public String getValue(String key, String defaultValue) {
		String value = getValue(key);
		return value != null ? value : defaultValue;
	}
	
	public List<String> getValues(String key) {
		List<String> values = map.get(key);
		if(values == null)
			return Collections.emptyList();
		return values;
	}
	
	public Map<String, String> getValueMap(String name) {
		List<String> values = getValues(name);
		Map<String, String> map = new HashMap<>();
		for(String item : values) {
			if(EzyStrings.isNoContent(item))
				continue;
			String[] kvs = item.split(";");
			for(String kv : kvs) {
				String[] strs = kv.split("=");
				String key = strs[0].trim();
				String value = strs.length <= 1 
						? kv.contains("=") ? "" : null 
						: strs[1];
				map.put(key, value);
			}
		}
		return map;
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
	
	public Map<String, String> toMap() {
		Map<String, String> answer = new HashMap<>();
		for(Entry<String, List<String>> entry : map.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			StringBuilder valueString = new StringBuilder();
			for(int i = 0 ; i < values.size() ; ++i) {
				if(i > 0) 
					valueString.append(";");
				valueString.append(values.get(i));
			}
			answer.put(key, valueString.toString());
		}
		return answer;
	}
	
	public static List<String> mapToKeyValueList(Map<String, Object> map) {
		List<String> answer = new LinkedList<>();
		for(Entry<String, Object> e : map.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();
			if(value == null) {
				answer.add(key);
			}
			else {
				answer.add(key + "=" + value);
			}
		}
		return answer;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<MultiValueMap> {
		
		public final Map<String, List<String>> map;
		
		public Builder() {
			this.map = new HashMap<>();
		}
		
		public Builder setValue(String key, String value) {
			List<String> v = map.get(key);
			if(v == null) {
				v = new ArrayList<>();
				map.put(key, v);
			}
			v.add(value);
			return this;
		}
		
		public Builder setValues(String key, Iterable<String> values) {
			for(String value : values)
				setValue(key, value);
			return this;
		}
		
		public Builder setValues(String key, Map<String, Object> values) {
			return setValues(key, mapToKeyValueList(values));
		}
		
		@Override
		public MultiValueMap build() {
			return new MultiValueMap(map);
		}
		
	}
	
}
