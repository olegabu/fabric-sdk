package com.example.fabric2.api;

import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.Fabric2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController()
@RequestMapping(value = "/lifecycle")
@RequiredArgsConstructor
@CrossOrigin
public class LifecycleChaincodeRestController {


    private final Fabric2Service fabric2Service;

    /**
     * Get list of installed on the associated peer
     * @return stream of installed chaincodes
     */
    @GetMapping(path = "/chaincodes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chaincode> getInstalledChaincodes() {
        return fabric2Service.getInstalledChaincodes();
    }

    /**
     * Get list of committed chaincodes on the channel
     * @param channelId
     * @return stream of committed chaincodes
     */
    @GetMapping(path = "/channel/{channelId}/chaincodes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chaincode> getCommittedChaincodes(@PathVariable String channelId) {
        return fabric2Service.getCommittedChaincodes(channelId);
    }


    @PostMapping(path = "/chaincode/approve/{channelId}/{chaincodeName}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chaincode> approveChaincode(@PathVariable String channelId, @PathVariable String chaincodeName) {
        return null;//TODO
    }


    @GetMapping(path = "/ping", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> pingPong() {
        return Flux.just("pong");
    }

}
