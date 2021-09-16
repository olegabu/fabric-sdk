package com.example.fabric2.service.localfabric;

import com.example.fabric2.sdk.SdkFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SdkOperations {

    private final SdkFacade sdkFacade;


    public String installChaincode() {
        return sdkFacade.installChaincode();
    }
}
