package com.example.fabric2.service;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.fabriclowlevel.LifecycleCLIOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class Fabric2Service {


    private final LifecycleCLIOperations cliOperations;
    private final WebClient webClient;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        return cliOperations.getCommittedChaincodes(channelId);
    }


    public Mono<String> installExternalChaincode(ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection,
                                                      Mono<FilePart> filePartFlux) {

        Mono<String> stringMono = webClient.post().uri(sdkAgentConnection.getAddress())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(buildMultipartBody(filePartFlux, metadata)))
                .retrieve()
                .bodyToMono(String.class);
        return stringMono;
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

/*    public Flux<String> installChaincode(String label, String version, String lang, String path, ) {
        Try.of(()-> {
                LifecycleChaincodePackage lifecycleChaincodePackage = LifecycleChaincodePackage
                .fromSource(label, Paths.get("src/test/fixture/sdkintegration/gocc/sample1"),
                        TransactionRequest.Type.valueOf(lang),
                        "github.com/example_cc", Paths.get("src/test/fixture/meta-infs/end2endit"));
    })

    }*/

}
