package com.example.fabric2.service.externalchaincode;

import lombok.Data;

@Data
public class ChaincodeServerParams {
    private String ccId;
    private String host;
    private String port;
    private String tlsRootCert;
}
