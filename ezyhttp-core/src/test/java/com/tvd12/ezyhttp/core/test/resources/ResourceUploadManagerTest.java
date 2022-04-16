package com.tvd12.ezyhttp.core.test.resources;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.util.EzyThreads;
import org.testng.annotations.Test;

import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyhttp.core.exception.MaxResourceUploadCapacity;
import com.tvd12.ezyhttp.core.exception.MaxUploadSizeException;
import com.tvd12.ezyhttp.core.resources.ResourceUploadManager;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;

public class ResourceUploadManagerTest extends BaseTest {
	
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
		sut.destroy();
	}

	@Test
	public void drainOfferAgainGetMaxQueueCapacityTest() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager(
			1, 1, 1
		);

		InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any())).thenReturn(1);

		OutputStream outputStream = mock(OutputStream.class);

		// when
		AtomicReference<Exception> exceptionRef = new AtomicReference<>();
		for (int i = 0 ; i < 100 ; ++i) {
			try {
				sut.drainAsync(inputStream, outputStream, new EzyResultCallback<Boolean>() {
					@Override
					public void onResponse(Boolean response) {
					}

					@Override
					public void onException(Exception e) {
						exceptionRef.set(e);
					}
				});
			} catch (Exception ignored) {
			}
		}

		// then
		while (exceptionRef.get() == null) {
			EzyThreads.sleep(3);
		}
		Asserts.assertEqualsType(exceptionRef.get(), MaxResourceUploadCapacity.class);
		sut.stop();
		sut.destroy();
		verify(inputStream, atLeastOnce()).read(any());
		verify(outputStream, atLeastOnce()).write(any(), anyInt(), anyInt());
	}

	@Test
	public void drainOfferAgainGetMaxQueueCapacityAndFutureNullTest() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager(
			1, 1, 1
		);

		InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any())).thenReturn(1);

		OutputStream outputStream = mock(OutputStream.class);

		EzyFutureMap<?> futureMap = FieldUtil.getFieldValue(sut, "futureMap");

		// when
		for (int i = 0 ; i < 1000 ; ++i) {
			try {
				sut.drainAsync(inputStream, outputStream, response -> {});
			} catch (Exception ignored) {
			}
			futureMap.clear();
		}

		// then
		sut.stop();
		sut.destroy();
		verify(inputStream, atLeastOnce()).read(any());
		verify(outputStream, atLeastOnce()).write(any(), anyInt(), anyInt());
	}

	@Test
	public void drainOfferAgainButErrorTest() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager();

		InputStream inputStream = mock(InputStream.class);
		OutputStream outputStream = mock(OutputStream.class);

		// when
		AtomicBoolean finished = new AtomicBoolean();
		sut.drainAsync(inputStream, outputStream, response -> {
			finished.set(true);
			throw new RuntimeException("just test");
		});

		// then
		while (!finished.get()) {
			EzyThreads.sleep(3);
		}
		sut.stop();
		sut.destroy();
		verify(inputStream, times(1)).read(any());
	}

    @Test
    public void drainWithMaxUploadSizeTest() throws Exception {
        // given
        ResourceUploadManager sut = new ResourceUploadManager();
        
        int size = ResourceUploadManager.DEFAULT_BUFFER_SIZE * 3;
        
        byte[] inputBytes = RandomUtil.randomByteArray(size);
        InputStream inputStream = new ByteArrayInputStream(inputBytes);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
        
        // when
        sut.drain(inputStream, outputStream, Integer.MAX_VALUE);
        
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
    public void drainFailedDueToOverUploadSizeTest() {
        // given
        ResourceUploadManager sut = new ResourceUploadManager();
        
        int size = ResourceUploadManager.DEFAULT_BUFFER_SIZE * 3;
        
        byte[] inputBytes = RandomUtil.randomByteArray(size);
        InputStream inputStream = new ByteArrayInputStream(inputBytes);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
        
        // when
        Throwable e = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream, 1));
        
        // then
        Asserts.assertEqualsType(e, MaxUploadSizeException.class);
        sut.stop();
    }
	
	@Test
	public void activeFalse() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager();
		FieldUtil.setFieldValue(sut, "active", false);
		
		int size = ResourceUploadManager.DEFAULT_BUFFER_SIZE * 3;
		
		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
		
		// when
		sut.drainAsync(inputStream, outputStream, it -> {});
		Thread.sleep(100);
		
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
        when(inputStream.read(any(byte[].class))).thenReturn(10);
        OutputStream outputStream = mock(OutputStream.class);
        
        sut.stop();
        Thread.sleep(200);
        
        // when
        sut.drainAsync(inputStream, outputStream, it -> {});
        Throwable e = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));
        
        // then
        Asserts.assertThat(e).isEqualsType(MaxResourceUploadCapacity.class);
        sut.stop();
	}
	
	@SuppressWarnings("rawtypes")
    @Test
    public void drainButFutureNull() throws Exception {
        // given
        ResourceUploadManager sut = new ResourceUploadManager(100, 1, 1024);
        
        EzyFutureMap futureMap = FieldUtil.getFieldValue(sut, "futureMap");
        
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any(byte[].class))).thenAnswer(it -> {
            Thread.sleep(200);
            return 0;
        });
        OutputStream outputStream = mock(OutputStream.class);
        
        // when
        sut.drainAsync(inputStream, outputStream, it -> {});
        futureMap.clear();
        
        // then
        Thread.sleep(300);
        sut.stop();
    }

	@Test
	public void drainFailedDueToOutputStreamThrowable() throws Exception {
		// given
		ResourceUploadManager sut = new ResourceUploadManager();

		int size = ResourceUploadManager.DEFAULT_BUFFER_SIZE * 3;

		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);

		OutputStream outputStream = mock(OutputStream.class);
		Throwable exception = new StackOverflowError("just test");
		doThrow(exception).when(outputStream).write(any(byte[].class), anyInt(), anyInt());

		// when
		Throwable e = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));

		// then
		Asserts.assertThat(e.getCause()).isEqualsType(StackOverflowError.class);
		sut.stop();
		verify(outputStream, times(1)).write(any(byte[].class), anyInt(), anyInt());
	}
}
