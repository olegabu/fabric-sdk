package com.example.fabric2.service.localfabric.externalchaincode;

import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebFlux
public class ExternalChaincodeClientServiceRemoteInteractionsTest {

    @Autowired
    private ExternalChaincodeClientService clientService;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void sendingChaincodeTarToRemoteServerForRunning() throws InterruptedException {
        SdkAgentConnection sdkConnection = SdkAgentConnection.of(mockWebServer.getHostName(), mockWebServer.getPort());

        Flux<DataBuffer> dataBuffers = DataBufferUtils.read(new ClassPathResource("chaincode-as-a-service/ext-chaincode.tar.gz"), DefaultDataBufferFactory.sharedInstance, 2048);
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


        mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                if (recordedRequest.getPath().equals("/control/runchaincode")) {
                    assertArrayEquals(getResourceBytes("chaincode-as-a-service/ext-chaincode.tar.gz"), recordedRequest.getBody().readByteArray());
                    return new MockResponse().setBody("TestSuccess");
                }
                return new MockResponse();
            }
        });


        StepVerifier.create(clientService.requestRunExternalChaincode(sdkConnection, Mono.just(fp)))
                .expectNext("TestSuccess")
                .verifyComplete();

    }

    private byte[] getResourceBytes(String resourcePath) {
        return Try.of(()->new ClassPathResource(resourcePath).getInputStream().readAllBytes()).get();
    }

}
