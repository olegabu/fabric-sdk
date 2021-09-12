package com.example.fabric2.service.fabriclowlevel.externalchaincode;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.service.externalchaincode.ChaincodeTargetPlatform;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.test.context.TestConstructor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@AllArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ExternalChaincodeClientTest {

    private final ExternalChaincodeClientService clientService;
    private final ObjectMapper objectMapper;


    @Test
    public void packageExternalChaincodeTest() throws IOException {

        ExternalChaincodeMetadata metadata= ExternalChaincodeMetadata.of("testlabel", "external",
                ChaincodeTargetPlatform.JAVA);
        ExternalChaincodeConnection connection= ExternalChaincodeConnection.of("localhost:9999", "");

        Flux<DataBuffer> dataBufferFlux = DataBufferUtils.readInputStream(() ->
                new ByteArrayInputStream(new byte[]{'a', 'b', 'c'}), DefaultDataBufferFactory.sharedInstance, 2048);

        Path testDir = Files.createTempDirectory("packageTest");
        Files.write(testDir.resolve("test.txt"), objectMapper.writeValueAsString(metadata).getBytes());

        Flux<Path> packagePath = clientService.packageExternalChaincode(metadata, connection, dataBufferFlux);

        StepVerifier.create(packagePath)
                .expectNextCount(1)
                .verifyComplete();

    }

}
