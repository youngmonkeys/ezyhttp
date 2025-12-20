package com.tvd12.ezyhttp.core.test.io;

import com.tvd12.test.performance.Performance;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileChannelAndRandomAccessCompare {

    public static void main(String[] args) {
        File file = new File("pom.xml");
        long fileLength = file.length();
        long fileChannelTime = Performance.create()
            .test(() -> {
                try (FileChannel fileChannel = FileChannel.open(
                    Paths.get("pom.xml"),
                    StandardOpenOption.READ
                )) {
                    ByteBuffer buffer = ByteBuffer.allocate(1);
                    fileChannel.read(buffer, fileLength - 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .getTime();

        long randomAccessFileTime = Performance.create()
            .test(() -> {
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                    randomAccessFile.seek(fileLength - 1);
                    randomAccessFile.read(new byte[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .getTime();
        System.out.println("fileChannelTime: " + fileChannelTime);
        System.out.println("randomAccessFileTime: " + randomAccessFileTime);
    }
}
