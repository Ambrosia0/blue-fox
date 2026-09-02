package com.ambrosia.content_service.post.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


// Component type bean used in custom Jackson mapper
public class ExtensionLoader {
    private final Set<String> extensionMap = ConcurrentHashMap.newKeySet();

    public ExtensionLoader() throws IOException{
        try (Stream<String> stream = Files.lines(Paths.get("extensions"))) {
            stream.forEach(name -> extensionMap.add(name));
        } catch (Exception e) {
            IO.println("Can't find/parse file with extension's names");
        }
    }
}
