package com.example.fabric2.sdk;

import com.example.fabric2.model.Chaincode;
import io.vavr.collection.List;
import io.vavr.collection.Map;


public interface SdkFacade {

    List<Map<String, Chaincode>> getInstalledChaincodes();

    String installChaincode();
}
