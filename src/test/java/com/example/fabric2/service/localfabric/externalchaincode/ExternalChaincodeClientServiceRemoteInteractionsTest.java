package com.example.fabric2.service.localfabric.externalchaincode;

import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import com.example.fabric2.test.TestUtils;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
@AutoConfigureWebFlux
@ActiveProfiles("test")
public class ExternalChaincodeClientServiceRemoteInteractionsTest {

    @Autowired
    private ExternalChaincodeClientService clientService;

    private static MockWebServer mockWebServer;

    private static final  String TEST_CHAINCODE_LABEL = "test";
    private static final  Integer TEST_CHAINCODE_PORT = 9991;
    private static final String TEST_TAR_GZ_FILE = "chaincode-as-a-service/ext-chaincode.tar.gz";

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

        mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                if (recordedRequest.getPath().equals(String.format("/control/run-package-on-system/%s/%d", TEST_CHAINCODE_LABEL, TEST_CHAINCODE_PORT))) {
                    assertArrayEquals(
                            TestUtils.getResourceBytes(TEST_TAR_GZ_FILE),
                            recordedRequest.getBody().readByteArray());
                    return new MockResponse().setBody("TestSuccess");
                }
                return new MockResponse();
            }
        });


        SdkAgentConnection sdkConnection = SdkAgentConnection.of(mockWebServer.getHostName(), mockWebServer.getPort());
        FilePart fp = TestUtils.filePartFromResource(TEST_TAR_GZ_FILE);
        StepVerifier.create(clientService.runExternalChaincode(sdkConnection, TEST_CHAINCODE_LABEL, "testPackId", TEST_CHAINCODE_PORT, Mono.just(fp)))
                .expectNext("TestSuccess")
                .verifyComplete();

    }


}
