package com.example.fabric2.service.localfabric.externalchaincode;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeLocalHostService;
import com.example.fabric2.service.management.PortAssigner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
//@AllArgsConstructor
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
public class ExternalChaincodeClientServiceTest {

    @MockBean
    PortAssigner portAssigner;

    @Autowired
    private ExternalChaincodeLocalHostService hostService;

    @Test
    public void packageExternalChaincodeTest() {

        ExternalChaincodeMetadata metadata = ExternalChaincodeMetadata.of("testlabel", "external", "1.0");
        ExternalChaincodeConnection connectionJson = ExternalChaincodeConnection.of("localhost", 9991, "TODO");

        Mono<Chaincode> result = hostService.installExternalChaincodePeerPart(metadata, connectionJson);

        StepVerifier.create(result)
                .assertNext(chaincode-> Assertions.assertNotEquals(Chaincode.empty, chaincode))
                .verifyComplete();

    }

}

/*

    Flux<DataBuffer> dataBufferFlux = DataBufferUtils.readInputStream(() ->
            new ByteArrayInputStream(new byte[]{'a', 'b', 'c'}), DefaultDataBufferFactory.sharedInstance, 2048);

    Path testDir = Files.createTempDirectory("packageTest");
        Files.write(testDir.resolve("test.txt"), objectMapper.writeValueAsString(metadata).getBytes());
*/
