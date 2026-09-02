package com.ambrosia.profile_service.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.springframework.util.DigestUtils;

import com.ambrosia.profile_service.user.model.dto.request.FileMetadata;
import com.ambrosia.profile_service.user.utils.SupportedFileTypes;

public class FileMetadataFactory {
    public static Path testImagePath = Path.of("src/test/resources/test_image.png");

    public static FileMetadata fileMetadata(){
        try {
            var filePath = testImagePath;
            var fileContentType = Files.probeContentType(filePath);
            var contentLength = Files.size(filePath);
            var md5Hash = Base64.getEncoder().encodeToString(
                DigestUtils.md5Digest(Files.readAllBytes(filePath))
            );
            return new FileMetadata(
                contentLength, 
                SupportedFileTypes.fromMimeType(fileContentType), 
                md5Hash
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
