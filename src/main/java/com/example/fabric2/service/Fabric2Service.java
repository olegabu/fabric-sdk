package com.example.fabric2.service;

import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.fabriclowlevel.LifecycleCLIOperations;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.hyperledger.fabric.sdk.LifecycleChaincodePackage;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class Fabric2Service {


    private final LifecycleCLIOperations cliOperations;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        return cliOperations.getCommittedChaincodes(channelId);
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
