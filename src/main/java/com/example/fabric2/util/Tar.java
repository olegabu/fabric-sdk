package com.example.fabric2.util;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
@Log4j2
public class Tar {

    public Path createFromDirectory() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public InputStream createTarGz(String entryName, byte[] bytes) {
        return createTarGz(HashMap.of(entryName, bytes));
    }

    public InputStream createTarGz(Map<String, byte[]> entries) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        try {
            TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(bos));
            archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            entries.forEach((fileName, bytes) -> {
                try {
                    TarArchiveEntry archiveEntry = new TarArchiveEntry(fileName);
                    archiveEntry.setMode(0100644);
                    archiveEntry.setSize(bytes.length);
                    archiveOutputStream.putArchiveEntry(archiveEntry);
                    archiveOutputStream.write(bytes);
                    archiveOutputStream.closeArchiveEntry();
                } catch (IOException e) {
                    log.error("Error creating tar entry " + fileName, e);
                }
            });
            archiveOutputStream.close();

            return new ByteArrayInputStream(bos.toByteArray()); //TODO: use piped streams

        } catch (IOException e) {
            throw new RuntimeException("Error creating tar archive", e);
        }
    }

    public Path extractTarGz(Path targetDir, InputStream tarGzInputStream) {

        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(tarGzInputStream)))) {

            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
            while (currentEntry != null) {
                Path destPath = Path.of(targetDir.toString(), currentEntry.getName());
                log.debug("Targz extracting: {}", destPath.toAbsolutePath().toString());
                if (currentEntry.isDirectory()) {
                    Files.createDirectories(destPath);
                } else {
                    Files.createDirectories(destPath.getParent());
                    Files.copy(tarInput, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
//                if (!currentEntry.getName().equals("metadata.json")) { // right now anything but this
//                    byte[] buf = new byte[(int) currentEntry.getSize()];
//                    tarInput.read(buf, 0, (int) currentEntry.getSize());
//
//                    return buf;
//
//                }
                currentEntry = tarInput.getNextTarEntry();
            }
        } catch (Exception e) {
            log.error("Cannot extract tar file", e);
        }
        return targetDir;
    }
}