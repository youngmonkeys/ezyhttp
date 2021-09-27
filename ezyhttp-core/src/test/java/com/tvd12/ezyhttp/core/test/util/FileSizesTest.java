package com.tvd12.ezyhttp.core.test.util;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.util.FileSizes;
import com.tvd12.test.assertion.Asserts;

public class FileSizesTest {

	@Test
	public void toByteSizeTest() {
		// given
		String b = "10b";
		String kb = "20kb";
		String mb = "30MB";
		String gb = "40gb";
		String tb = "50TB";
		
		// when
		long actualB = FileSizes.toByteSize(b);
		long actualKB = FileSizes.toByteSize(kb);
		long actualMB = FileSizes.toByteSize(mb);
		long actualGB = FileSizes.toByteSize(gb);
		long actualTB = FileSizes.toByteSize(tb);
		
		// then
		Asserts.assertEquals(actualB, 10L);
		Asserts.assertEquals(actualKB, 20 * 1024L);
		Asserts.assertEquals(actualMB, 30 * 1024L * 1024L);
		Asserts.assertEquals(actualGB, 40L * 1024L * 1024L * 1024L);
		Asserts.assertEquals(actualTB, 50L * 1024L * 1024L * 1024L * 1024L);
	}
	
	@Test
	public void toByteSizeFailedDueToEmptyValue() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> FileSizes.toByteSize(""));
		
		// then
		Asserts.assertThat(e).isEqualsType(IllegalArgumentException.class);
	}
	
	@Test
	public void toByteSizeFailedDueToInvalidValue() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> FileSizes.toByteSize("ab"));
		
		// then
		Asserts.assertThat(e).isEqualsType(IllegalArgumentException.class);
	}
	
	@Test
	public void toByteSizeFailedDueToInvalidValue2() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> FileSizes.toByteSize("abc"));
		
		// then
		Asserts.assertThat(e).isEqualsType(IllegalArgumentException.class);
	}
}
