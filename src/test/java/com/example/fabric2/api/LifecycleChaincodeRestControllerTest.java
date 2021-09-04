package com.example.fabric2.api;

import com.example.fabric2.model.Chaincode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest
@AutoConfigureWebTestClient
//@WebFluxTest(controllers = LifecycleChaincodeRestController.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = ALL)
public class LifecycleChaincodeRestControllerTest {
    private final WebTestClient webTestClient;

    @Test
    void testController() {
        Flux<Chaincode> responseBody = webTestClient.get()
                .uri("/lifecycle/channels/common/chaincodes")
                .accept(MediaType.TEXT_EVENT_STREAM)

                .exchange()
                .expectStatus().isOk()
                .returnResult(Chaincode.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(new Chaincode("dns", "1.0"))
                .verifyComplete();

    }
}
