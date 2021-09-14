package com.example.fabric2.service.tar;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Log4j2
public class Tar {

    public InputStream createTar(String entryName , byte[] bytes) {
        return createTar(HashMap.of(entryName, bytes));
    }

    public InputStream createTar(Map<String, byte[]> entries) {

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
}