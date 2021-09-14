package com.example.fabric2.service.management;

import com.example.fabric2.dto.SdkAgentConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
public class PortAssigner {

    @Value("${start_ports_pool:9990}")
    private Integer firstPortInPool;

    private final WebClient webClient;

    public PortAssigner() {
        this.webClient = WebClient.builder().build();
    }

    public Flux<Integer> assignLocalPort() {
        return Flux.just(firstPortInPool); //TODO: get,reserve port from pool
    }

    public Mono<Integer> assignRemotePort(SdkAgentConnection sdkAgentConnection) {
        return webClient.get().uri(sdkAgentConnection.getAddress() + "/control/assignport")
                .retrieve()
                .bodyToMono(Integer.class);
    }
}
