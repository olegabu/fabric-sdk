package com.example.fabric2.service.localfabric;

import com.example.fabric2.model.Chaincode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = ALL)
@ActiveProfiles("test")
public class LifecycleCliOperationsTest {

    private final LifecycleCLIOperations cliOperation;

    @Test
    public void getCommittedChaincodes() {
        Flux<Chaincode> chaincodes = cliOperation.getCommittedChaincodes("common");

        StepVerifier.create(chaincodes)
                .expectNext(new Chaincode("dns", "1.0"))
                .verifyComplete();

    }
}
