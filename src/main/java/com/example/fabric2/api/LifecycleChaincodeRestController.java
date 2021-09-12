package com.example.fabric2.api;

import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.Fabric2Service;
import com.example.fabric2.service.externalchaincode.ChaincodeTargetPlatform;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
public class LifecycleChaincodeRestController {


    private final Fabric2Service fabric2Service;

    @CrossOrigin
    @GetMapping(path = "/channels/{channelId}/chaincodes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chaincode> getCommittedChaincodes(@PathVariable String channelId) {
        return fabric2Service.getCommittedChaincodes(channelId);
    }

    @CrossOrigin
    @PostMapping(path = "/externalchaincodes/{label}/{type}/{version}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> installExternalChaincode(
            @PathVariable String label,
            @PathVariable String type,
            @PathVariable String version,
            @PathVariable ChaincodeTargetPlatform targetPlatform,
            @RequestParam SdkAgentConnection sdkAgentConnection,
            @RequestPart("files") Mono<FilePart> filePartFlux) {

        Publisher<String> stringFlux = fabric2Service.installExternalChaincode(
                ExternalChaincodeMetadata.of(label, type, targetPlatform), sdkAgentConnection, filePartFlux);
        return stringFlux;
    }

    @CrossOrigin
    @GetMapping(path = "/ping", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> pingPong() {
        return Flux.just("pong");
    }

}
