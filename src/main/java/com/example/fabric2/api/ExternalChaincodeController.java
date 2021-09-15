package com.example.fabric2.api;

import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.service.Fabric2Service;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController(value = "/externalchaincode")
@RequiredArgsConstructor
public class ExternalChaincodeController {

    private final Fabric2Service fabric2Service;


    @CrossOrigin
    @PostMapping(path = "/deploy/{label}/{type}/{version}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> installExternalChaincode(
            @PathVariable String label,
            @PathVariable String type,
            @PathVariable String version,
            @RequestParam SdkAgentConnection sdkAgentConnection,
            @RequestPart("files") Mono<FilePart> filePartFlux) {

        return fabric2Service.deployExternalChaincode(ExternalChaincodeMetadata.of(label, type, version), sdkAgentConnection, filePartFlux);
    }

}
