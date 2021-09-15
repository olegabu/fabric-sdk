package com.example.fabric2.test;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public class TestUtils {

    public static byte[] getResourceBytes(String resourcePath) {
        return Try.of(()->new ClassPathResource(resourcePath).getInputStream().readAllBytes()).get();
    }

    @NotNull
    public static FilePart filePartFromResource(String resourcePath) {
        Flux<DataBuffer> dataBuffers = DataBufferUtils.read(new ClassPathResource(resourcePath), DefaultDataBufferFactory.sharedInstance, 2048);
        FilePart fp = new FilePart() {
            @Override
            public String filename() {
                return "test.tar.gz";
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return Mono.empty();
            }

            @Override
            public String name() {
                return "file";
            }

            @Override
            public HttpHeaders headers() {
                return new HttpHeaders();
            }

            @Override
            public Flux<DataBuffer> content() {
                return dataBuffers;
            }
        };
        return fp;
    }
}
