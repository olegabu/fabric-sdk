package com.example.fabric2.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data(staticConstructor = "of")
public class ExternalChaincodeMetadata {

    private final String name;
    private final String type;
    private final String version;

    public String getLabel() {
        return StringUtils.isBlank(version) ? name : name + "_" + version;
    }
}
