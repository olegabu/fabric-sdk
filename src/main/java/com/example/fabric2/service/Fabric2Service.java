package com.example.fabric2.service;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeHostService;
import com.example.fabric2.service.localfabric.LifecycleCLIOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class Fabric2Service {


    private final LifecycleCLIOperations cliOperations;
    private final ExternalChaincodeClientService chaincodeClientService;
    private final ExternalChaincodeHostService chaincodeServerService;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        return cliOperations.getCommittedChaincodes(channelId);
    }


    public Mono<String> installExternalChaincode(ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection,
                                                      Mono<FilePart> filePartFlux) {
        return Mono.just("");
    }

    private MultiValueMap<String, HttpEntity<?>> buildMultipartBody(Mono<FilePart> filePartFlux, ExternalChaincodeMetadata metadata) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.asyncPart("file", filePartFlux, FilePart.class);
        builder.part("label", metadata.getLabel());
        return builder.build();
    }


    public void packageExternalChaincode(ExternalChaincodeMetadata metadata, ExternalChaincodeConnection connection,
                                         Mono<FilePart> filePartFlux) {


    }

    public Flux<String> installChaincodeFromPackage(InputStream packageInStream) {
        return chaincodeServerService.installChaincodeFromPackage(packageInStream);
    }

/*    public Flux<String> installChaincode(String label, String version, String lang, String path, ) {
        Try.of(()-> {
                LifecycleChaincodePackage lifecycleChaincodePackage = LifecycleChaincodePackage
                .fromSource(label, Paths.get("src/test/fixture/sdkintegration/gocc/sample1"),
                        TransactionRequest.Type.valueOf(lang),
                        "github.com/example_cc", Paths.get("src/test/fixture/meta-infs/end2endit"));
    })

    }*/

}
