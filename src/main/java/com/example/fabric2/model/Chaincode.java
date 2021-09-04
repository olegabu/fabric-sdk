package com.example.fabric2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class Chaincode {

    public static final Chaincode empty = new Chaincode(null, null);

    private final String name;
    private final String version;

    /**
     * Parsing from CLI output for specified chaincode name
     * Command:
     * peer lifecycle chaincode querycommitted --channelID common --name dns
     * Example output:
     * Committed chaincode definition for chaincode 'dns' on channel 'common':
     * Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc, Approvals: [org1: true]
     *
     * @param chaincodeName
     * @param line
     * @return
     */
    public static Chaincode fromLine(String chaincodeName, String line) {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Parsing from CLI output when chaincode name is not specified
     * Command:
     * peer lifecycle chaincode querycommitted --channelID common
     * Example output:
     * Committed chaincode definitions on channel 'common':
     * Name: dns, Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc
     *
     * @param line
     * @return
     */
    public static Chaincode fromLine(String line) {
        Pattern p = Pattern.compile("Name: (.+), Version: (.+), Sequence: (.+), Endorsement Plugin: (.+), Validation Plugin: (.+)");
        Matcher m = p.matcher(line);
        return m.find() ? new Chaincode(m.group(1), m.group(2)) : Chaincode.empty;
    }
}
