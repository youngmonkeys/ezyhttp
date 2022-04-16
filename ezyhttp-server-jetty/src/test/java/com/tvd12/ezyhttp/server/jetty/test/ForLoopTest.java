package com.tvd12.ezyhttp.server.jetty.test;

import java.util.ArrayList;
import java.util.List;

import com.tvd12.test.performance.Performance;

public class ForLoopTest {
	
	public static void main(String[] args) {
		Object obj = new Object();
		long time1 = Performance.create()
			.test(() -> obj.hashCode())
			.getTime();
		System.out.print("time1: " + time1);
		
		List<Object> list = new ArrayList<>();
		list.add(new Object());
		long time2 = Performance.create()
			.test(() -> {for(Object o : list) o.hashCode();})
			.getTime();
		System.out.print("time2: " + time2);
	}
	
}
