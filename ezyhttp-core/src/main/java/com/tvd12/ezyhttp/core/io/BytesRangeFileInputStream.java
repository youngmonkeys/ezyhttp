package com.tvd12.ezyhttp.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.tvd12.ezyhttp.core.data.BytesRange;

import lombok.Getter;

public class BytesRangeFileInputStream extends InputStream {

    @Getter
    private final long from;
    @Getter
    private final long to;
    @Getter
    private final long fileLength;
    @Getter
    private long readBytes;
    @Getter
    private final long targetReadBytes;
    private final RandomAccessFile randomAccessFile;

    public static final int MAX_CHUNK_LENGTH = 2 * 1024 * 1024;

    public BytesRangeFileInputStream(
        String filePath,
        String range
    ) throws Exception {
        this(filePath, new BytesRange(range));
    }

    public BytesRangeFileInputStream(
        String filePath,
        BytesRange range
    ) throws Exception {
        this(
            filePath,
            range.getFrom(),
            range.getTo()
        );
    }

    public BytesRangeFileInputStream(
        String filePath,
        long rangeFrom,
        long rangeTo
    ) throws Exception {
        from = rangeFrom;
        final AnywayFileLoader fileLoader = AnywayFileLoader.getDefault();
        final File file = fileLoader.load(filePath);
        if (file == null) {
            throw new FileNotFoundException(filePath + " not found");
        }
        fileLength = file.length();
        long actualTo = rangeTo == 0
            ? from + MAX_CHUNK_LENGTH
            : rangeTo + 1;
        if (actualTo > fileLength) {
            actualTo = fileLength;
        }
        to = actualTo;
        targetReadBytes = actualTo - from;
        randomAccessFile = new RandomAccessFile(
            file,
            "r"
        );
        try {
            randomAccessFile.seek(from);
        } catch (Exception e) {
            randomAccessFile.close();
            throw e;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (readBytes >= targetReadBytes) {
            return -1;
        }
        final int length = (int) (to - (from + readBytes));
        final int actualLength = Math.min(b.length, length);
        final int rb = randomAccessFile.read(b, 0, actualLength);
        if (rb > 0) {
            readBytes += rb;
        }
        return rb;
    }

    @Override
    public int read() throws IOException {
        if (readBytes >= targetReadBytes) {
            return -1;
        }
        final int b = randomAccessFile.read();
        if (b >= 0) {
            ++readBytes;
        }
        return b;
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }

    public String getBytesContentRangeString() {
        return "bytes " + from +
            '-' + (from < to ? to - 1 : to) +
            '/' + fileLength;
    }
}
