package com.example.fabric2.dto;

import com.example.fabric2.model.Chaincode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallChaincodeResult {
    private ExternalChaincodeConnection chaincodeConnection;
    private Chaincode chaincode;
}
