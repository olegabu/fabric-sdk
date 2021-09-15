package com.example.fabric2.service.management;


import com.example.fabric2.service.tar.Tar;
import com.example.fabric2.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class PackageRunnerTest {

    @Autowired
    private PackageRunner packageRunner;
    @Autowired
    private Tar tar;

    @Test
    public void extractAndRunTargGZ() {

        FilePart fp = TestUtils.filePartFromResource("chaincode-as-a-service/ext-chaincode.tar.gz");

        Mono<String> test = packageRunner.runTarGzPackage("test", 9991, Mono.just(fp));

        StepVerifier.create(test)
                .expectNextCount(1)
                .verifyComplete();
    }
}
