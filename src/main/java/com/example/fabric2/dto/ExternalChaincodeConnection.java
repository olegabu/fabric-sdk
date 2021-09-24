package com.example.fabric2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ExternalChaincodeConnection {
    private String chaincodeHost;
    private Integer chaincodePort;
    private String root_cert;
    private String dial_timout = "10s";
    private String client_auth_required = "true";
    private String client_key;
    private String client_cert;

    public static ExternalChaincodeConnection of(String chaincodeHost, Integer chaincodePort, String root_cert) {
        var result = new ExternalChaincodeConnection();
        result.chaincodeHost = chaincodeHost;
        result.chaincodePort = chaincodePort;
        result.root_cert = root_cert;
        return result;
    }

    public String getAddress() {
        return chaincodeHost + ":" + chaincodePort;
    }
}
