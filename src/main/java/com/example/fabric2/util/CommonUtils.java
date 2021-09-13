package com.example.fabric2.util;

import io.vavr.control.Try;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CommonUtils {

    public Path saveStreamToFile(InputStream inputStream, Path resultFilePath) {
        if (inputStream==null) return null;
        return Try.of(() ->
        {
            Files.deleteIfExists(resultFilePath);
            return Files.copy(inputStream, resultFilePath);
        })
                .map(res -> resultFilePath)
                .getOrElseThrow(e -> new RuntimeException(e));
    }

}
