package com.example.fabric2.util;

import io.vavr.control.Try;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;


public class FileUtils {

    public static Path generateTmpFileName(String prefix, String suffix) {
        Consumer<Path> deleteTmpFile = path -> Try.of(()->Files.deleteIfExists(path)).map((b)->path);

        return Try.of(()->Files.createTempFile(prefix, suffix))
                .andThen(deleteTmpFile)
                .get();
    }

    public static void setExecutionPermissions(Path filePath) {
        Set<PosixFilePermission> executePermissions =
                EnumSet.of(PosixFilePermission.OWNER_EXECUTE,
                        PosixFilePermission.GROUP_EXECUTE,
                        PosixFilePermission.OTHERS_EXECUTE,
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.GROUP_READ,
                        PosixFilePermission.OTHERS_READ,
                        PosixFilePermission.OWNER_WRITE,
                        PosixFilePermission.GROUP_WRITE
                );

        Try.of(()->java.nio.file.Files.setPosixFilePermissions(filePath, executePermissions)).get();
    }
}
