package com.ambrosia.content_service.unit;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.core.GzipCompressor;
import com.ambrosia.content_service.util.PostTemplate;

public class PostCompressionTest {

    @Test
    void shouldNotThrowException() throws Exception{
        var compressed = GzipCompressor.compress(PostTemplate.template);
        var decompressed = GzipCompressor.decompress(compressed);
        if(!decompressed.equals(PostTemplate.template))
            throw new Exception("Error!");
    }
}
