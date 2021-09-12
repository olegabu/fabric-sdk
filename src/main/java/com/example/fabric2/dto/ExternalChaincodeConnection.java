package com.example.fabric2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@RequiredArgsConstructor(staticName = "of")
public class ExternalChaincodeConnection {
    private final String address;
    private final String root_cert;
    private String dial_timout="10s";
    private String client_auth_required="true";
    private String client_key;
    private String client_cert;

}
