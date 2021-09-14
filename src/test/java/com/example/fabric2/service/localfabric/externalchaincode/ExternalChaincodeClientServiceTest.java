package com.example.fabric2.service.localfabric.externalchaincode;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.service.externalchaincode.ChaincodeTargetPlatform;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import com.example.fabric2.service.management.PortAssigner;
import com.example.fabric2.util.CommonUtils;
import com.example.fabric2.util.TmpFiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@SpringBootTest
//@AllArgsConstructor
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(SpringExtension.class)
public class ExternalChaincodeClientServiceTest {

    @MockBean
    PortAssigner portAssigner;
    @MockBean
    TmpFiles tmpFiles;
    @MockBean(answer = Answers.CALLS_REAL_METHODS)
    CommonUtils utils;
    @Autowired
    private ExternalChaincodeClientService clientService;

    public static final SdkAgentConnection SDK_CONNECTION = SdkAgentConnection.of("localhost", 8080);

    @Test
    public void packageExternalChaincodeTest() {

        ExternalChaincodeMetadata metadata = ExternalChaincodeMetadata.of("testlabel", "external",
                ChaincodeTargetPlatform.JAVA);

        Mockito.when(portAssigner.assignRemotePort(SDK_CONNECTION)).thenReturn(Mono.just(9990));
        Mockito.when(tmpFiles.generateTmpFileName(Mockito.any(), Mockito.any())).thenReturn(Path.of("./tmp/test.tar.gz"));
        Mockito.when(utils.saveStreamToFile(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> {
                    invocation.callRealMethod();
                    Files.copy(Path.of("./tmp/test.tar.gz"), Path.of("../fabric-starter/chaincode/test.tar.gz"), StandardCopyOption.REPLACE_EXISTING);
                    return Path.of("/opt/chaincode/test.tar.gz");
                });

        Flux<String> result = clientService.installExternalChaincode(metadata, SDK_CONNECTION);

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

    }

}

/*

    Flux<DataBuffer> dataBufferFlux = DataBufferUtils.readInputStream(() ->
            new ByteArrayInputStream(new byte[]{'a', 'b', 'c'}), DefaultDataBufferFactory.sharedInstance, 2048);

    Path testDir = Files.createTempDirectory("packageTest");
        Files.write(testDir.resolve("test.txt"), objectMapper.writeValueAsString(metadata).getBytes());
*/
