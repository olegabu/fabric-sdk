package com.example.fabric2.api;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.InstallChaincodeResult;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.service.Fabric2Service;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/externalchaincode")
@RequiredArgsConstructor
public class ExternalChaincodeController {

    private final Fabric2Service fabric2Service;


    @CrossOrigin
    @PostMapping(path = "/deploy/{label}/{type}/{version}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> deployExternalChaincode(
            @PathVariable String label,
            @PathVariable String type,
            @PathVariable String version,
            @ModelAttribute SdkAgentConnection sdkAgentConnection,
            @ModelAttribute ExternalChaincodeConnection chaincodeConnection,
            @RequestPart("files") Mono<FilePart> filePartFlux) {

        return fabric2Service.deployExternalChaincode(
                ExternalChaincodeMetadata.of(label, type, version),
                sdkAgentConnection, chaincodeConnection, filePartFlux);
    }

    @CrossOrigin
    @PostMapping(path = "/install/{name}/{version}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<InstallChaincodeResult> installExternalChaincodePeerPart(
            @PathVariable String name,
            @PathVariable String version,
            @ModelAttribute SdkAgentConnection sdkAgentConnection,
            @ModelAttribute ExternalChaincodeConnection chaincodeConnection
    ) {

        return fabric2Service.installExternalChaincodePeerPart(
                ExternalChaincodeMetadata.of(name, "external", version),
                sdkAgentConnection, chaincodeConnection);
    }

    @CrossOrigin
    @PostMapping(path = "/run/{label}/{packageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> runExternalChaincode(
            @PathVariable String label,
            @PathVariable String packageId,
            @ModelAttribute SdkAgentConnection sdkAgentConnection,
            @ModelAttribute ExternalChaincodeConnection chaincodeConnection,
            @RequestPart("files") Mono<FilePart> filePartMono) {

        return fabric2Service.runExternalChaincode(label, packageId,
                sdkAgentConnection, chaincodeConnection, filePartMono);
    }

}
