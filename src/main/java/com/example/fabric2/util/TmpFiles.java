package com.example.fabric2.util;

import io.vavr.control.Try;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Component
public class TmpFiles {

    public Path generateTmpFileName(String prefix, String suffix) {
        Consumer<Path> deleteTmpFile = path -> Try.of(()->Files.deleteIfExists(path)).map((b)->path);

        return Try.of(()->Files.createTempFile(prefix, suffix))
                .andThen(deleteTmpFile)
                .get();
    }

}
