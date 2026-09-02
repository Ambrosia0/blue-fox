package com.ambrosia.content_service.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.util.Assert;

public class GzipCompressor {
    public static byte[] compress(String content) throws IOException{
        Assert.notNull(content, "Content must not be null for gzip compression!");
        var byteOutputStream = new ByteArrayOutputStream();
        try (var gzipOs = new GZIPOutputStream(byteOutputStream)) {
            gzipOs.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return byteOutputStream.toByteArray();
    }

    public static String decompress(byte[] content) throws IOException{
        if(content == null || content.length == 0)
            return "";
        try (var gzioIs = new GZIPInputStream(new ByteArrayInputStream(content))) {
            return new String(gzioIs.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
