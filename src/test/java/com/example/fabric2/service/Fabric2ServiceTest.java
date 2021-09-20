package com.example.fabric2.service;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.util.ChaincodeUtils;
import com.example.fabric2.util.FileUtils;
import com.example.fabric2.util.Tar;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@SpringBootTest
@ActiveProfiles("by-docker")
public class Fabric2ServiceTest {

    @MockBean
    private FileUtils fileUtils;

    @Autowired
    private Fabric2Service fabric2Service;
    @Autowired
    private Tar tar;
    @Autowired
    private ChaincodeUtils chaincodeUtils;

    public static final ExternalChaincodeMetadata METADATA = ExternalChaincodeMetadata.of("test", "external", "1.0");

    @Test
    public void testInstallChaincode() {

        Mono<InputStream> inputStreamMono = chaincodeUtils.prepareLifecyclePackageStreamForExternalChaincode(METADATA, ExternalChaincodeConnection.of("localhost", 9991, "TODO"));

        //TODO: add approach of provisioning created file to container during tests (or avoid files, use InputStream)
        Mockito.when(fileUtils.savePackageToFile(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtils.generateTmpFileName(Mockito.anyString(), Mockito.anyString())).thenReturn(Path.of("./tmp/test.tar.gz"));
        Mockito.when(fileUtils.saveStreamToFile(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> {
                    invocation.callRealMethod();
                    Files.copy(Path.of("./tmp/test.tar.gz"), Path.of("../fabric-starter/chaincode/test.tar.gz"), StandardCopyOption.REPLACE_EXISTING);
                    return Path.of("/opt/chaincode/test.tar.gz");
                });


        Mono<String> installed = inputStreamMono.flatMap(is -> fabric2Service.installChaincodeFromPackage(is));

        Mono<String> approveResult = installed.flatMap(packageId ->
                fabric2Service.approveChaincode("common", "test", "test", "1.0", packageId));


        StepVerifier.create(approveResult)
                .expectNextCount(1)
                .verifyComplete();

    }


}
