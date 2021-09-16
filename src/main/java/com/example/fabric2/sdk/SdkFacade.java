package com.example.fabric2.sdk;

import com.example.fabric2.model.Chaincode;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.springframework.stereotype.Component;

@Component
public interface SdkFacade {

    public List<Map<String, Chaincode>> getInstalledChaincodes();

    String installChaincode();
}
