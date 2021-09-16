package com.tvd12.ezyhttp.core.test.constant;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.test.assertion.Asserts;

public class ContentTypesTest {
	
	@Test
	public void getContentTypeTest() {
		// given
		// when
		String contentType = ContentTypes.getContentType(null);
		
		//
		Asserts.assertNull(contentType);
	}
}
