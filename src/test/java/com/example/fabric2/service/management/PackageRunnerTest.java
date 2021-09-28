package com.example.fabric2.service.management;


import com.example.fabric2.test.TestUtils;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
public class PackageRunnerTest {

    @Autowired
    private PackageHandler packageRunner;

    @Test
    public void extractAndRunTargGZ() {

        FilePart fp = TestUtils.filePartFromResource("chaincode-as-a-service/ext-chaincode.tar.gz");

        Mono<String> test = packageRunner.runTarGzPackage("test", 9991, HashMap.of("PACKAGE_ID", "testPackId"), Mono.just(fp));

        StepVerifier.create(test)
                .expectNextCount(1)
                .verifyComplete();
    }
}
