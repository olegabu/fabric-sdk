package com.example.fabric2.dto;

import com.example.fabric2.service.externalchaincode.ChaincodeTargetPlatform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@RequiredArgsConstructor(staticName = "of")
public class ExternalChaincodeMetadata {

    private final String label;
    private final String type;
    private final ChaincodeTargetPlatform targetPlatform;
    private String version = "1.0";

    private ExternalChaincodeConnection connection;
}
