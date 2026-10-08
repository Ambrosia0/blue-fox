package com.ambrosia.content_service.post.utils;

public record ConvertedDoc(
    String document,
    String preview
) {
    public static ConvertedDoc from(String doc, String preview){
        return new ConvertedDoc(
            doc, 
            preview
        );
    }
}
