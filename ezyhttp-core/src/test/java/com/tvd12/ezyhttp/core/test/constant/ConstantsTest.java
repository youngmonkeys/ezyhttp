package com.tvd12.ezyhttp.core.test.constant;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.Constants;
import com.tvd12.test.assertion.Asserts;

public class ConstantsTest {

	@Test
	public void test() {
		Asserts.assertEquals(
				Constants.DEFAULT_PROPERTIES_FILES, 
				new String[] {
			            "application.properties",
			            "application.yaml"
			    }
		);
	}
}
