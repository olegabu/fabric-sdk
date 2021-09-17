package com.example.fabric2.sdk.v2x;

import lombok.Data;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.util.Set;

@Data
public class FabricUser implements User {

    private final String mspId;
    private final String name;
    private final String enrollSecret;
    private final Enrollment enrollment;

    private Set<String> roles;
    private String account;
    private String affiliation;

}
