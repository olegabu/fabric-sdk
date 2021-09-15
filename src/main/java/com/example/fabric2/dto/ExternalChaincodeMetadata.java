package com.example.fabric2.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data(staticConstructor = "of")
public class ExternalChaincodeMetadata {

    private final String label;
    private final String type;
    private final String version;

    public String label() {
        return StringUtils.isBlank(version) ? label : label + "_" + version;
    }
}
