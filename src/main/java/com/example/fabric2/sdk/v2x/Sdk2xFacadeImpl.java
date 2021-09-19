package com.example.fabric2.sdk.v2x;

import com.example.fabric2.model.Chaincode;
import com.example.fabric2.sdk.SdkFacade;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.springframework.stereotype.Component;

@Component
public class Sdk2xFacadeImpl implements SdkFacade {
    @Override
    public List<Map<String, Chaincode>> getInstalledChaincodes() {

        return null;
    }

    @Override
    public String installChaincode() {

        return "";
        //TODO
    }
}
