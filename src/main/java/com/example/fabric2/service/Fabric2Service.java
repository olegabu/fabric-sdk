package com.example.fabric2.service;

import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.fabriclowlevel.LifecycleCLIOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class Fabric2Service {

    @Value("fabric2-api.crypto-config.dir")
    private String cryptoConfigDir;

    private final LifecycleCLIOperations cliOperations;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        return cliOperations.getCommittedChaincodes(channelId);
    }

}
