package com.example.fabric2.api;

import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.Fabric2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
//@RequestMapping("/lifecycle")
@RequiredArgsConstructor
public class LifecycleChaincodeRestController {

    @Autowired
    private  Fabric2Service fabric2Service;

    @CrossOrigin
    @GetMapping(path = "/channels/{channelId}/chaincodes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chaincode> getCommittedChaincodes(@PathVariable String channelId) {
        return fabric2Service.getCommittedChaincodes(channelId);
    }

    @CrossOrigin
    @GetMapping(path = "/ping", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> pingPong() {
        return Flux.just("pong");
    }

}
