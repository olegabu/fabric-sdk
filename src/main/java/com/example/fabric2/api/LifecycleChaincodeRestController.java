package com.example.fabric2.api;

import com.example.fabric2.dto.InstallChaincodeResult;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.Fabric2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController()
@RequestMapping(value = "/lifecycle")
@RequiredArgsConstructor
@CrossOrigin
public class LifecycleChaincodeRestController {


    private final Fabric2Service fabric2Service;

    /**
     * Get list of installed on the associated peer
     *
     * @return stream of installed chaincodes
     */
    @GetMapping(path = "/chaincodes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chaincode> getInstalledChaincodes() {
        return fabric2Service.getInstalledChaincodes();
    }

    /**
     * Get list of committed chaincodes on the channel
     *
     * @param channelId
     * @return stream of committed chaincodes
     */
    @GetMapping(path = "/channel/{channelId}/chaincodes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chaincode> getCommittedChaincodes(@PathVariable String channelId) {
        Flux<Chaincode> committedChaincodes = fabric2Service.getCommittedChaincodes(channelId);
        return committedChaincodes;
    }


    @PostMapping(path = "/chaincode/install", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    //TODO: {chaincodeName}/{version}/{lang}
    public Mono<InstallChaincodeResult> installChaincode(@PathVariable(required = false) String chaincodeName,
                                                         @PathVariable(required = false) String version,
                                                         @RequestPart Mono<FilePart> packageToRun) {

        return fabric2Service.installChaincode(packageToRun);
    }

    @PostMapping(path = {"/chaincode/approve/{channelId}/{chaincodeName}/{version}/{packageId}", "/chaincode/approve/{channelId}/{chaincodeName}/{version}/{packageId}/{initRequired}"}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Chaincode> approveChaincode(@PathVariable String channelId,
                                            @PathVariable String chaincodeName,
                                            @PathVariable String version,
                                            @PathVariable String packageId,
                                            @PathVariable(required = false) Boolean initRequired) {

        return fabric2Service.approveChaincode(channelId, chaincodeName, version, packageId, initRequired);
    }

    @PostMapping(path = "/chaincode/commit/{channelId}/{chaincodeName}/{version}/{newSequence}/{initRequired}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> commitChaincode(@PathVariable String channelId, @PathVariable String chaincodeName, @PathVariable String version, @PathVariable Integer newSequence,
                                        @PathVariable(required = false) Boolean initRequired) {
        return fabric2Service.commitChaincode(channelId, chaincodeName, version, newSequence, initRequired);
    }

    @GetMapping(path = "/chaincode/checkcommitreadiness/{channelId}/{chaincodeName}/{version}/{newSequence}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Boolean> checkCommitReadiness(@PathVariable String channelId,
                                             @PathVariable String chaincodeName,
                                             @PathVariable String version,
                                             @PathVariable Integer newSequence) {
        return null;//TODO: fabric2Service.checkCommitReadiness(channelId, chaincodeName, version, newSequence);
    }


    @GetMapping(path = "/ping", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> pingPong() {
        return Flux.just("pong");
    }

}
