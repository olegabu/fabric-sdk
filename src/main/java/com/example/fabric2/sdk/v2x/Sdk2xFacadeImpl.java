package com.example.fabric2.sdk.v2x;

import com.example.fabric2.model.Chaincode;
import com.example.fabric2.sdk.SdkFacade;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

public class Sdk2xFacadeImpl implements SdkFacade {
    @Override
    public List<Map<String, Chaincode>> getInstalledChaincodes() {

        return null;
    }

    @Override
    public String installChaincode() {

        HFClient orgClient = HFClient.createNewInstance();
        Try.of(()->{orgClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());return true;}).get();
        return "";
        //TODO
    }
}
