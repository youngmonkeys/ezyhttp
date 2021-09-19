package com.tvd12.ezyhttp.server.core.test.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.exception.MaxResourceUploadCapacity;
import com.tvd12.ezyhttp.server.core.resources.ResourceUploadManager;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;

public class ResourceUploadManagerTest {
	
	@Test
	public void drainTest() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager();
		
		int size = ResourceUploadManager.DEFAULT_BUFFER_SIZE * 3;
		
		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
		
		// when
		sut.drain(inputStream, outputStream);
		
		// then
		byte[] outputBytes = outputStream.toByteArray();
		Asserts.assertEquals(inputBytes, outputBytes);
		sut.stop();
	}
	
	@Test
	public void drainFailedDueToOutputStream() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager();
		
		int size = ResourceUploadManager.DEFAULT_BUFFER_SIZE * 3;
		
		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		
		OutputStream outputStream = mock(OutputStream.class);
		IOException exception = new IOException("just test");
		doThrow(exception).when(outputStream).write(any(byte[].class), anyInt(), anyInt());
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));
		
		// then
		Asserts.assertThat(e).isEqualsType(IOException.class);
		sut.stop();
	}
	
	@Test
	public void drainFailedDueToOutputStreamDueToError() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager();
		
		BlockingQueue<Object> queue = FieldUtil.getFieldValue(sut, "queue");
		
		queue.offer(new Object());
		
		InputStream inputStream = mock(InputStream.class);
		OutputStream outputStream = mock(OutputStream.class);
		
		// when
		sut.drain(inputStream, outputStream);
		
		// then
		sut.stop();
	}
	
	@Test
	public void activeFalse() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager();
		
		int size = ResourceUploadManager.DEFAULT_BUFFER_SIZE * 3;
		
		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
		
		// when
		sut.drain(inputStream, outputStream);
		
		// then
		byte[] outputBytes = outputStream.toByteArray();
		Asserts.assertEquals(inputBytes, outputBytes);
		sut.stop();
	}
	
	@Test
	public void drainFailedDueToMaxResourceUploadCapacity() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager(1, 1, 1024);
		
		InputStream inputStream = mock(InputStream.class);
		
		OutputStream outputStream = mock(OutputStream.class);
		IOException exception = new IOException("just test");
		doThrow(exception).when(outputStream).write(any(byte[].class), anyInt(), anyInt());
		sut.stop();
		
		BlockingQueue<Object> queue = FieldUtil.getFieldValue(sut, "queue");
		
		queue.offer(new Object());
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));
		
		// then
		Asserts.assertThat(e).isEqualsType(MaxResourceUploadCapacity.class);
	}
}
