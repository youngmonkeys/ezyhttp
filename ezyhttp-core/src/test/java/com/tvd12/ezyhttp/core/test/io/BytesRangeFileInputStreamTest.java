package com.tvd12.ezyhttp.core.test.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.stream.EzyInputStreams;
import com.tvd12.ezyhttp.core.io.BytesRangeFileInputStream;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;

public class BytesRangeFileInputStreamTest {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    @Test
    public void readBytesTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final long fileLength = pomFile.length();
        final String range = "bytes=0-" + (fileLength * 2);

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );

        // when
        final byte[] actual = EzyInputStreams.toByteArray(sut);

        // then
        sut.close();
        Asserts.assertEquals(
            actual,
            Files.readAllBytes(pomFile.toPath())
        );
        Asserts.assertEquals(sut.getFrom(), 0L);
        Asserts.assertEquals(sut.getTo(), fileLength);
        Asserts.assertEquals(sut.getFileLength(), fileLength);
        Asserts.assertEquals(sut.getReadBytes(), fileLength);
        Asserts.assertEquals(sut.getTargetReadBytes(), fileLength);
        Asserts.assertEquals(
            sut.getBytesContentRangeString(),
            "bytes 0-" + (sut.getReadBytes() - 1) + '/' + sut.getReadBytes()
        );
    }

    @Test
    public void readBytesWithFromEqualsToTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final long fileLength = pomFile.length();
        final String range = "bytes=2-1";

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );

        // when
        final byte[] actual = EzyInputStreams.toByteArray(sut);

        // then
        sut.close();
        Asserts.assertEquals(
            actual,
            EMPTY_BYTE_ARRAY
        );
        Asserts.assertEquals(sut.getFrom(), 2L);
        Asserts.assertEquals(sut.getTo(), 2L);
        Asserts.assertEquals(sut.getFileLength(), fileLength);
        Asserts.assertEquals(sut.getReadBytes(), 0L);
        Asserts.assertEquals(sut.getTargetReadBytes(), 0L);
        Asserts.assertEquals(
            sut.getBytesContentRangeString(),
            "bytes 2-2/" + fileLength
        );
    }

    @Test
    public void readBytesWithZeroToTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final long readLength = Math.min(
            pomFile.length(),
            BytesRangeFileInputStream.MAX_CHUNK_LENGTH
        );
        final String range = "bytes=0-";

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );

        // when
        final byte[] actual = EzyInputStreams.toByteArray(sut);

        // then
        sut.close();
        Asserts.assertEquals(
            actual,
            Files.readAllBytes(pomFile.toPath())
        );
        Asserts.assertEquals(sut.getFrom(), 0L);
        Asserts.assertEquals(sut.getTo(), readLength);
        Asserts.assertEquals(sut.getFileLength(), readLength);
        Asserts.assertEquals(sut.getReadBytes(), readLength);
        Asserts.assertEquals(sut.getTargetReadBytes(), readLength);
    }

    @Test
    public void readSomeBytesTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final String range = "bytes=1-2";

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );

        // when
        final byte[] actual = EzyInputStreams.toByteArray(sut);

        // then
        sut.close();
        final byte[] bytes = Files.readAllBytes(pomFile.toPath());
        Asserts.assertEquals(
            actual,
            new byte[] { bytes[1], bytes[2] }
        );
        Asserts.assertEquals(sut.getFrom(), 1L);
        Asserts.assertEquals(sut.getTo(), 3L);
        Asserts.assertEquals(sut.getFileLength(), pomFile.length());
        Asserts.assertEquals(sut.getReadBytes(), 2L);
        Asserts.assertEquals(sut.getTargetReadBytes(), 2L);
    }

    @Test
    public void readBytesButZeroTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final long fileLength = pomFile.length();
        final String range = "bytes=0-" + (fileLength * 2);

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );
        final RandomAccessFile randomAccessFile = FieldUtil.getFieldValue(
            sut,
            "randomAccessFile"
        );
        randomAccessFile.seek(fileLength);

        // when
        final byte[] actual = EzyInputStreams.toByteArray(sut);

        // then
        sut.close();
        Asserts.assertEquals(
            actual,
            EMPTY_BYTE_ARRAY
        );
        Asserts.assertEquals(sut.getFrom(), 0L);
        Asserts.assertEquals(sut.getTo(), fileLength);
        Asserts.assertEquals(sut.getFileLength(), fileLength);
        Asserts.assertEquals(sut.getReadBytes(), 0L);
        Asserts.assertEquals(sut.getTargetReadBytes(), fileLength);
    }

    @Test
    public void readSingleByteTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final long fileLength = pomFile.length();
        final String range = "bytes=0-" + (fileLength * 2);

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );

        // when
        final byte[] actual = new byte[(int) fileLength];
        for (int i = 0; i < actual.length; ++i) {
            actual[i] = (byte) sut.read();
        }

        // then
        sut.close();
        Asserts.assertEquals(
            actual,
            Files.readAllBytes(pomFile.toPath())
        );
        Asserts.assertEquals(sut.getFrom(), 0L);
        Asserts.assertEquals(sut.getTo(), fileLength);
        Asserts.assertEquals(sut.getFileLength(), fileLength);
        Asserts.assertEquals(sut.getReadBytes(), fileLength);
        Asserts.assertEquals(sut.getTargetReadBytes(), fileLength);
    }

    @Test
    public void readSingleByteWithRangeTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final long fileLength = pomFile.length();
        final String range = "bytes=0-1";

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );

        // when
        final byte[] actual = new byte[2];
        for (int i = 0; i < fileLength; ++i) {
            final int rb = sut.read();
            if (rb < 0) {
                break;
            }
            actual[i] = (byte) rb;
        }

        // then
        sut.close();
        final byte[] bytes = Files.readAllBytes(pomFile.toPath());
        Asserts.assertEquals(
            actual,
            new byte[] { bytes[0], bytes[1] }
        );
        Asserts.assertEquals(sut.getFrom(), 0L);
        Asserts.assertEquals(sut.getTo(), 2L);
        Asserts.assertEquals(sut.getFileLength(), fileLength);
        Asserts.assertEquals(sut.getReadBytes(), 2L);
        Asserts.assertEquals(sut.getTargetReadBytes(), 2L);
    }

    @Test
    public void readSingleByteButZeroTest() throws Exception {
        // given
        final String pomFilePath = "pom.xml";
        final File pomFile = new File(pomFilePath);
        final long fileLength = pomFile.length();
        final String range = "bytes=0-1";

        final BytesRangeFileInputStream sut = new BytesRangeFileInputStream(
            pomFilePath,
            range
        );
        final RandomAccessFile randomAccessFile = FieldUtil.getFieldValue(
            sut,
            "randomAccessFile"
        );
        randomAccessFile.seek(fileLength);

        // when
        final int actual = sut.read();

        // then
        sut.close();
        Asserts.assertEquals(actual, -1);
        Asserts.assertEquals(sut.getFrom(), 0L);
        Asserts.assertEquals(sut.getTo(), 2L);
        Asserts.assertEquals(sut.getFileLength(), fileLength);
        Asserts.assertEquals(sut.getReadBytes(), 0L);
        Asserts.assertEquals(sut.getTargetReadBytes(), 2L);
    }

    @Test
    public void fileNotExistTest() {
        // given
        final String pomFilePath = "file not found";
        final String range = "bytes=1-2";

        // when
        final Throwable e = Asserts.assertThrows(
            () -> new BytesRangeFileInputStream(
                pomFilePath,
                range
            )
        );

        // then
        Asserts.assertEqualsType(e, FileNotFoundException.class);
    }

    @Test
    public void seekErrorTest() {
        // given
        final String pomFilePath = "pom.xml";
        final String range = "bytes=-1-2";

        // when
        final Throwable e = Asserts.assertThrows(
            () -> new BytesRangeFileInputStream(
                pomFilePath,
                range
            )
        );

        // then
        Asserts.assertEqualsType(e, IOException.class);
    }
}
