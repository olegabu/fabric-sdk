package com.example.fabric2.dto;

import lombok.Data;

@Data(staticConstructor = "of")
public class SdkAgentConnection {
    private final String host;
    private final Integer port;

    public String getAddress() {
        return host + ":" + port;
    }
}
