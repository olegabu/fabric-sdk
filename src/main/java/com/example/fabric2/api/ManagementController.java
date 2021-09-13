package com.example.fabric2.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController("control")
public class ManagementController {

    @Value("${start_ports_pool:9990}")
    private Integer firstPortInPool;

    @GetMapping(path = "/assignport")
    public Flux<Integer> reserveNextFreePort() {
        return Flux.just(firstPortInPool); //TODO: dynamic, store
    }




}
