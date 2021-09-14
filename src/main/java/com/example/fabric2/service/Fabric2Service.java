package com.example.fabric2.service;

import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeLocalHostService;
import com.example.fabric2.service.localfabric.LifecycleCLIOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class Fabric2Service {


    private final LifecycleCLIOperations cliOperations;
    private final ExternalChaincodeClientService chaincodeClientService;
    private final ExternalChaincodeLocalHostService chaincodeHostService;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        return cliOperations.getCommittedChaincodes(channelId);
    }


    public Mono<String> installExternalChaincode(ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection,
                                                 Mono<FilePart> filePartFlux) {

        return chaincodeClientService.requestRunExternalChaincode(sdkAgentConnection, filePartFlux);

//        filePartFlux.map(filePart ->
//                {
//                    Flux<String> map1 = filePart.content().remap(dataBuffer -> {
//                        String stringMono = /*Mono.just*/""; //chaincodeClientService.requestRunExternalChaincode(sdkAgentConnection, dataBuffer.asInputStream(true));
//                        return stringMono;
//                    });
//                    return map1;
//                }
//        )
    }

    private MultiValueMap<String, HttpEntity<?>> buildMultipartBody(Mono<FilePart> filePartFlux, ExternalChaincodeMetadata metadata) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.asyncPart("file", filePartFlux, FilePart.class);
        builder.part("label", metadata.getLabel());
        return builder.build();
    }


    public Flux<String> installChaincodeFromPackage(InputStream packageInStream) {
        return chaincodeHostService.installChaincodeFromPackage(packageInStream);
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
