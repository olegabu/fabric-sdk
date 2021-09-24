package com.example.fabric2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SdkAgentConnection {
    private String agentHost;
    private Integer agentPort;

    public String getAddress() {
        return agentHost + ":" + agentPort;
    }
}
