package com.example.fabric2.api;

import com.example.fabric2.flowtools.cli.FlowCmdExec;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.Fabric2Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
//@WebFluxTest(controllers = LifecycleChaincodeRestController.class)
@ActiveProfiles("test")
public class LifecycleChaincodeRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FlowCmdExec<Chaincode> chaincodeCmdExec;

    @Autowired
    private Fabric2Service fabric2Service;

    @Test
    void testController() {

        when(fabric2Service.getCommittedChaincodes("common")).thenReturn(Flux.just(
                new Chaincode("dns", "1.0", 1),
                new Chaincode("test", "2.0", 2)));

        Flux<Chaincode> responseBody = webTestClient.get()
                .uri("/lifecycle/channel/common/chaincodes")
                .accept(MediaType.TEXT_EVENT_STREAM)

                .exchange()
                .expectStatus().isOk()
                .returnResult(Chaincode.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(new Chaincode("dns", "1.0", 1))
                .expectNext(new Chaincode("test", "2.0", 2))
                .verifyComplete();

    }
}
