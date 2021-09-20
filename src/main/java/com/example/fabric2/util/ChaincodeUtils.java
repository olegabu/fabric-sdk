package com.example.fabric2.util;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.HashMap;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ChaincodeUtils {

    private final ObjectMapper objectMapper;
    private final Tar tar;


    public Mono<InputStream> prepareLifecyclePackageStreamForExternalChaincode(ExternalChaincodeMetadata metadata, ExternalChaincodeConnection connection) {
        return Try.of(() -> Mono.just(
                this.tar.createTarGz("connection.json", objectMapper.writeValueAsString(connection).getBytes()))
                .map(codeTarGzInputStream -> tar.createTarGz(HashMap.of(
                        "metadata.json", Try.of(() -> objectMapper.writeValueAsString(metadata).getBytes()).get(),
                        "code.tar.gz", Try.of(() -> codeTarGzInputStream.readAllBytes()).get()))
                ))
                .getOrElseThrow(e -> new RuntimeException("Error packing serialized jsons:" + metadata.toString(), e));
    }

}
