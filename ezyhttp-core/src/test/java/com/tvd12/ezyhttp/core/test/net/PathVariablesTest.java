package com.tvd12.ezyhttp.core.test.net;

import java.util.List;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.net.PathVariables;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.performance.Performance;

public class PathVariablesTest extends BaseTest {

	@Test
	public void test() {
		String uri11 = "/api/v1/customer/{name}/create";
		String check11 = "/api/v1/customer/dung/create";
		List<Entry<String, String>> variables = PathVariables.getVariables(uri11, check11);
		System.out.println(variables);
		long time = Performance.create()
			.test(() -> PathVariables.getVariables(uri11, check11))
			.getTime();
		System.out.println(time);
	}
	
}
