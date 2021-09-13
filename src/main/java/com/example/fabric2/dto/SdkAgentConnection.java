package com.example.fabric2.dto;

import lombok.Data;

@Data(staticConstructor = "of")
public class SdkAgentConnection {
    private final String address;
}
