package com.example.fabric2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SdkAgentConnection {
    private String host;
    private Integer port;

    public String getAddress() {
        return host + ":" + port;
    }
}
