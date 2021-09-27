package com.tvd12.ezyhttp.server.core.test.view;

import java.util.Map;
import java.util.Properties;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.view.MessageReader;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodUtil;

public class MessageReaderTest {
	
	@Test
	public void test() {
		// given
		MessageReader reader = new MessageReader();
		
		String folderPath = "messages";
		
		// when
		Map<String, Properties> map = reader.read(folderPath);
		
		// then
		Asserts.assertEquals(4, map.size());
		Asserts.assertEquals(2, map.get("").size());
		Asserts.assertEquals(1, map.get("en_US").size());
		Asserts.assertEquals(1, map.get("vi").size());
	}
	
	@Test
	public void getFileNameNoPath() {
		// given
		String filePath = "messages_en_US.properties";
		
		MessageReader reader = new MessageReader();
		
		// when
		String actual = MethodUtil.invokeMethod("getFileName", reader, filePath);
		
		// then
		Asserts.assertEquals(filePath, actual);
	}
	
	@Test
	public void getFileNameHahsPath() {
		// given
		String filePath = "messages/";
		
		MessageReader reader = new MessageReader();
		
		// when
		String actual = MethodUtil.invokeMethod("getFileName", reader, filePath);
		
		// then
		Asserts.assertEquals(filePath, actual);
	}
}
