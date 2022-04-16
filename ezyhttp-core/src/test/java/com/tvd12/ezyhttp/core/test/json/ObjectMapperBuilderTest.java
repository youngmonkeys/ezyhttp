package com.tvd12.ezyhttp.core.test.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.io.EzyDates;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.test.assertion.Asserts;

public class ObjectMapperBuilderTest {

	private final ObjectMapper sut = new ObjectMapperBuilder()
			.decorator(it -> {})
			.build();
	
	@Test
	public void instantTest() {
		// given
		long current = System.currentTimeMillis();
		
		// when
		Instant actual = sut.convertValue(current, Instant.class);
		
		// then
		Asserts.assertEquals(Instant.ofEpochMilli(current), actual);
	}
	
	@Test
	public void instantStringTest() {
		// given
		String source = "2021-01-30T10:20:30:500";
		
		// when
		Instant actual = sut.convertValue(source, Instant.class);
		
		// then
		Asserts.assertEquals(EzyDates.parse(source).toInstant(), actual);
	}
	
	@Test
	public void instantFailedDueToBoolean() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> sut.convertValue(true, Instant.class));
		
		// then
		Asserts.assertThat(e.getCause()).isEqualsType(IOException.class);
	}
	
	@Test
	public void dateTest() {
		// given
		long current = System.currentTimeMillis();
		
		// when
		Date actual = sut.convertValue(current, Date.class);
		
		// then
		Asserts.assertEquals(new Date(current), actual);
	}
	
	@Test
	public void dateStringTest() {
		// given
		String source = "2021-01-30T10:20:30:500";
		
		// when
		Date actual = sut.convertValue(source, Date.class);
		
		// then
		Asserts.assertEquals(EzyDates.parse(source), actual);
	}
	
	@Test
	public void dateFailedDueToBoolean() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> sut.convertValue(true, Date.class));
		
		// then
		Asserts.assertThat(e.getCause()).isEqualsType(IOException.class);
	}
	
	@Test
	public void localDateTest() {
		// given
		String source = "2021-01-30";
		
		// when
		LocalDate actual = sut.convertValue(source, LocalDate.class);
		
		// then
		Asserts.assertEquals(EzyDates.parseDate(source), actual);
	}
	
	@Test
	public void localDateLongTest() {
		// given
		Long source = System.currentTimeMillis();
		
		// when
		LocalDate actual = sut.convertValue(source, LocalDate.class);
		
		// then
		Asserts.assertEquals(LocalDate.now(), actual);
	}
	
	@Test
	public void localDateFailedDueToBoolean() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> sut.convertValue(true, LocalDate.class));
		
		// then
		Asserts.assertThat(e.getCause()).isEqualsType(IOException.class);
	}
	
	@Test
	public void localTimeTest() {
		// given
		String source = "10:20:30:500";
		
		// when
		LocalTime actual = sut.convertValue(source, LocalTime.class);
		
		// then
		Asserts.assertEquals(EzyDates.parseTime(source), actual);
	}
	
	@Test
	public void localTimeLongTest() {
		// given
		long source = System.currentTimeMillis();
		
		// when
		LocalTime actual = sut.convertValue(source, LocalTime.class);
		
		// then
		Asserts.assertEquals(EzyDates.millisToDateTime(source).toLocalTime(), actual);
	}
	
	@Test
	public void localTimeFailedDueToBoolean() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> sut.convertValue(true, LocalTime.class));
		
		// then
		Asserts.assertThat(e.getCause()).isEqualsType(IOException.class);
	}
	
	@Test
	public void localDateTimeTest() {
		// given
		String source = "2021-01-30T10:20:30:500";
		
		// when
		LocalDateTime actual = sut.convertValue(source, LocalDateTime.class);
		
		// then
		Asserts.assertEquals(EzyDates.parseDateTime(source), actual);
	}
	
	@Test
	public void localDateLongTimeTest() {
		// given
		long source = System.currentTimeMillis();
		
		// when
		LocalDateTime actual = sut.convertValue(source, LocalDateTime.class);
		
		// then
		Asserts.assertEquals(EzyDates.millisToDateTime(source), actual);
	}
	
	@Test
	public void localDateTimeFailedDueToBoolean() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> sut.convertValue(true, LocalDateTime.class));
		
		// then
		Asserts.assertThat(e.getCause()).isEqualsType(IOException.class);
	}
	
	@Test
	public void bigIntegerTest() {
		// given
		String source = "12345";
		
		// when
		BigInteger actual = sut.convertValue(source, BigInteger.class);
		
		// then
		Asserts.assertEquals(BigInteger.valueOf(12345), actual);
	}
	
	@Test
	public void bigDecimalTest() {
		// given
		String source = "12345.6";
		
		ObjectMapper sut = new ObjectMapperBuilder()
				.build();
		
		// when
		BigDecimal actual = sut.convertValue(source, BigDecimal.class);
		
		// then
		Asserts.assertEquals(BigDecimal.valueOf(12345.6), actual);
	}
}
