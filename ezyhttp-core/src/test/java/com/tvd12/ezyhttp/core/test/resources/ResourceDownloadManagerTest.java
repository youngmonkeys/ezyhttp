package com.tvd12.ezyhttp.core.test.resources;

import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.util.EzyThreads;
import com.tvd12.ezyhttp.core.exception.MaxResourceDownloadCapacity;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.core.resources.ResourceUploadManager;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

public class ResourceDownloadManagerTest extends BaseTest {

    @Test
    public void drainTest() throws Exception {
        // given
        info("start drainTest");
        ResourceDownloadManager sut = new ResourceDownloadManager();

        int size = ResourceDownloadManager.DEFAULT_BUFFER_SIZE * 3;

        byte[] inputBytes = RandomUtil.randomByteArray(size);
        InputStream inputStream = new ByteArrayInputStream(inputBytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);

        // when
        sut.drain(inputStream, outputStream);

        // then
        byte[] outputBytes = outputStream.toByteArray();
        Asserts.assertEquals(inputBytes, outputBytes);
        sut.stop();
        info("end drainTest");
    }

    @Test
    public void drainOfferAgainGetMaxQueueCapacityTest() throws Exception {
        // given
        ResourceDownloadManager sut = new ResourceDownloadManager(
            1, 1, 1
        );

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any())).thenReturn(1);

        OutputStream outputStream = mock(OutputStream.class);

        // when
        AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        for (int i = 0; i < 100; ++i) {
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
        Asserts.assertEqualsType(exceptionRef.get(), MaxResourceDownloadCapacity.class);
        sut.stop();
        sut.destroy();
        verify(inputStream, atLeastOnce()).read(any());
        verify(outputStream, atLeastOnce()).write(any(), anyInt(), anyInt());
    }

    @Test
    public void drainOfferAgainGetMaxQueueCapacityAndFutureNullTest() throws Exception {
        // given
        ResourceDownloadManager sut = new ResourceDownloadManager(
            1, 1, 1
        );

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any())).thenReturn(1);

        OutputStream outputStream = mock(OutputStream.class);

        EzyFutureMap<?> futureMap = FieldUtil.getFieldValue(sut, "futureMap");

        // when
        for (int i = 0; i < 1000; ++i) {
            try {
                sut.drainAsync(inputStream, outputStream, response -> {
                });
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
        ResourceDownloadManager sut = new ResourceDownloadManager();

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
    public void drainFailedDueToOutputStream() throws Exception {
        // given
        info("start drainFailedDueToOutputStream");
        ResourceDownloadManager sut = new ResourceDownloadManager();

        int size = ResourceDownloadManager.DEFAULT_BUFFER_SIZE * 3;

        byte[] inputBytes = RandomUtil.randomByteArray(size);
        InputStream inputStream = new ByteArrayInputStream(inputBytes);

        OutputStream outputStream = mock(OutputStream.class);
        IOException exception = new IOException("just test");
        doThrow(exception).when(outputStream).write(any(byte[].class), anyInt(), anyInt());

        // when
        Throwable throwable = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));

        // then
        sut.stop();
        Asserts.assertEqualsType(throwable, IOException.class);
        info("finish drainFailedDueToOutputStream");
        sut.destroy();
    }

    @Test
    public void drainFailedDueToOutputStreamEnd() throws Exception {
        // given
        info("start drainFailedDueToOutputStream");
        ResourceDownloadManager sut = new ResourceDownloadManager();

        int size = ResourceDownloadManager.DEFAULT_BUFFER_SIZE * 3;

        byte[] inputBytes = RandomUtil.randomByteArray(size);
        InputStream inputStream = new ByteArrayInputStream(inputBytes);

        OutputStream outputStream = mock(OutputStream.class);
        EOFException exception = new EOFException("just test");
        doThrow(exception).when(outputStream).write(any(byte[].class), anyInt(), anyInt());

        // when
        Throwable throwable = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));

        // then
        sut.stop();
        Asserts.assertEqualsType(throwable, EOFException.class);
        info("finish drainFailedDueToOutputStream");
        sut.destroy();
    }

    @Test
    public void drainFailedDueToOutputStreamDueToError() throws Exception {
        // given
        info("start drainFailedDueToOutputStreamDueToError");
        ResourceDownloadManager sut = new ResourceDownloadManager();

        BlockingQueue<Object> queue = FieldUtil.getFieldValue(sut, "queue");

        queue.offer(new Object());

        InputStream inputStream = mock(InputStream.class);
        OutputStream outputStream = mock(OutputStream.class);

        // when
        sut.drain(inputStream, outputStream);

        // then
        sut.stop();
        info("finish drainFailedDueToOutputStreamDueToError");
    }

    @Test
    public void activeFalse() throws Exception {
        // given
        info("start activeFalse");
        ResourceDownloadManager sut = new ResourceDownloadManager();
        FieldUtil.setFieldValue(sut, "active", false);

        int size = ResourceDownloadManager.DEFAULT_BUFFER_SIZE * 3;

        byte[] inputBytes = RandomUtil.randomByteArray(size);
        InputStream inputStream = new ByteArrayInputStream(inputBytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);

        // when
        sut.drain(inputStream, outputStream);

        // then
        byte[] outputBytes = outputStream.toByteArray();
        Asserts.assertEquals(inputBytes, outputBytes);
        sut.stop();
        info("finish activeFalse");
    }

    @Test
    public void drainFailedDueToMaxResourceUploadCapacity() throws Exception {
        // given
        info("start drainFailedDueToMaxResourceUploadCapacity");
        ResourceDownloadManager sut = new ResourceDownloadManager(1, 1, 1024);

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any(byte[].class))).thenReturn(10);
        OutputStream outputStream = mock(OutputStream.class);

        sut.stop();
        Thread.sleep(200);

        // when
        sut.drainAsync(inputStream, outputStream, it -> {
        });
        Throwable e = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));

        // then
        Asserts.assertThat(e).isEqualsType(MaxResourceDownloadCapacity.class);
        sut.stop();
        info("finsihed drainFailedDueToMaxResourceUploadCapacity");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void drainButFutureNull() throws Exception {
        // given
        info("start drainButFutureNull");
        ResourceDownloadManager sut = new ResourceDownloadManager(100, 1, 1024);

        EzyFutureMap futureMap = FieldUtil.getFieldValue(sut, "futureMap");

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any(byte[].class))).thenAnswer(it -> {
            Thread.sleep(200);
            return 0;
        });
        OutputStream outputStream = mock(OutputStream.class);

        // when
        sut.drainAsync(inputStream, outputStream, it -> {
        });
        futureMap.clear();

        // then
        Thread.sleep(300);
        sut.stop();
        info("finsihed drainButFutureNull");
    }

    @Test
    public void drainFailedDueToOutputStreamThrowable() throws Exception {
        // given
        ResourceDownloadManager sut = new ResourceDownloadManager();

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
