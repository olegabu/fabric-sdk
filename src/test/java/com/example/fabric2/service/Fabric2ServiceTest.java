package com.example.fabric2.service;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.util.ChaincodeUtils;
import com.example.fabric2.util.FileUtils;
import com.example.fabric2.util.Tar;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@SpringBootTest
@ActiveProfiles("by-docker")
public class Fabric2ServiceTest {

    @Value("${org}")
    private String ORG;

    @MockBean
    private FileUtils fileUtils;

    @Autowired
    private Fabric2Service fabric2Service;
    @Autowired
    private ChaincodeUtils chaincodeUtils;

    public static final ExternalChaincodeMetadata METADATA = ExternalChaincodeMetadata.of("test", "external", "1.0");
    public static final ExternalChaincodeConnection TEST_EXTERNAL_CONNECTION = ExternalChaincodeConnection.of("localhost", 9991, "TODO");


    @Test
    public void testInstallReturnsPackageId() {
        prepareMockitoForDockerContainer();

        Mono<InputStream> inputStreamMono = chaincodeUtils.prepareLifecyclePackageStreamForExternalChaincode(METADATA, TEST_EXTERNAL_CONNECTION);
        Mono<Chaincode> installResult = inputStreamMono.flatMap(is -> fabric2Service.installChaincodeFromPackage(is));

        StepVerifier.create(installResult)
                .expectNextMatches(chaincode -> Pattern.matches("[a-fA-F\\d]{64}", chaincode.getPackageId()))
                .verifyComplete();
    }

    @Test
    public void testInstallApproveCommitChaincode() {

        prepareMockitoForDockerContainer();

        Mono<String> installApprove = chaincodeUtils.prepareLifecyclePackageStreamForExternalChaincode(METADATA, TEST_EXTERNAL_CONNECTION).flatMap(is ->
                fabric2Service.installChaincodeFromPackage(is)).flatMap(chaincode ->
                fabric2Service.approveChaincode("common", "test", "1.0", chaincode.getPackageId())
                        .map(res -> chaincode.getPackageId()));

        Mono<Chaincode> approvedChaincodesFilteredByNewPackageId = /*installApprove*/Mono.just("1ae99bbd95049d1456551e2ffe6e9fc54ec9123f0612c63558940d623136f4c2").flatMap(newPackageId -> Mono.from(
                fabric2Service.getApprovedChaincodes("common", "test")
                        .filter(approved -> approved.getPackageId().equals(newPackageId)))
        );


        approvedChaincodesFilteredByNewPackageId.map(
                approvedChaincode -> fabric2Service.checkCommitReadiness(ORG, "common", "test", "1.0", 1)        )
                .map(isReady->fabric2Service.commitChaincode("common", "test", "1.0", 1));// TODO


        StepVerifier.create(approvedChaincodesFilteredByNewPackageId)
                .expectNextCount(1)
                .verifyComplete();
    }


    private void prepareMockitoForDockerContainer() {
        //TODO: add approach of provisioning created file to container during tests (or avoid files, use InputStream)
        Mockito.when(fileUtils.savePackageToFile(Mockito.any())).thenCallRealMethod();
        Mockito.when(fileUtils.generateTmpFileName(Mockito.anyString(), Mockito.anyString())).thenReturn(Path.of("./tmp/test.tar.gz"));
        Mockito.when(fileUtils.saveStreamToFile(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> {
                    invocation.callRealMethod();
                    Files.copy(Path.of("./tmp/test.tar.gz"), Path.of("../fabric-starter/chaincode/test.tar.gz"), StandardCopyOption.REPLACE_EXISTING);
                    return Path.of("/opt/chaincode/test.tar.gz");
                });
    }

}
