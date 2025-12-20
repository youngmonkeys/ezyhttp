package com.tvd12.ezyhttp.core.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

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
    private final FileChannel fileChannel;

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
        fileChannel = FileChannel.open(
            file.toPath(),
            StandardOpenOption.READ
        );
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int read(byte[] b) throws IOException {
        if (readBytes >= targetReadBytes) {
            return -1;
        }
        long remaining = targetReadBytes - readBytes;
        int actualLength = (int) Math.min(b.length, remaining);
        ByteBuffer dst = ByteBuffer.wrap(b, 0, actualLength);
        long position = from + readBytes;
        int rb = fileChannel.read(dst, position);
        if (rb > 0) {
            readBytes += rb;
        }
        return rb;
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException("unsupport");
    }

    @Override
    public void close() throws IOException {
        fileChannel.close();
    }

    public String getBytesContentRangeString() {
        return "bytes " + from +
            '-' + (from < to ? to - 1 : to) +
            '/' + fileLength;
    }
}
