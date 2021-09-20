package com.example.fabric2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chaincode {

    public static final Chaincode empty = new Chaincode();

    private String name;
    private String version;
    private String packageId;
    private String label;
    private Integer approvedSequence;
    private Boolean initRequired;

    public static Chaincode ofInstalled(String name, String packageId, String label) {
        Chaincode result = new Chaincode();
        result.name = name;
        result.packageId = packageId;
        result.label = label;
        return result;
    }

    public static Chaincode ofApproved(String approvedSequence, String version, String packageId, String label, String initRequired) {
        Chaincode result = new Chaincode();
        result.approvedSequence = Integer.valueOf(approvedSequence);
        result.version = version;
        result.packageId = packageId;
        result.label = label;
        result.initRequired = BooleanUtils.toBoolean(initRequired);
        return result;
    }

    public Chaincode(String name, String version) {
        this.name = name;
        this.version = version;
    }

    /**
     * Parsing from CLI output of `peer lifecycle queryinstaleld` command when chaincode name is not specified
     * Command:
     * peer lifecycle chaincode querycommitted --channelID common
     * Example output:
     * Installed chaincodes on peer:
     * Package ID: dns_1.0:9e372cb9a05da4cf96550212c6ec8f7e17935cda9dbecf1a7d8ceccb99cf9c67, Label: dns_1.0
     * Package ID: testlabel:8b3c09500c292ed5d93733b0513e8212baf71751710c8284789521d4ff1f67e2, Label: testlabel
     * Package ID: testlabel:23254b4508b92ee16a5cb6703142c0b1a7ec9561f1419967d8e348e59a188236, Label: testlabel
     *
     * @param line
     * @return
     */
    public static Chaincode fromInstalledLine(String line) {
        Pattern p = Pattern.compile("Package ID: (.+):(.+), Label: (.+)");
        Matcher m = p.matcher(line);
        return m.find() ? Chaincode.ofInstalled(m.group(1), m.group(2), m.group(3)) : Chaincode.empty;
    }

    /**
     * Parsing from CLI output of `peer lifecycle queryapproved` command when chaincode name is not specified
     * Command:
     * peer lifecycle chaincode queryapproved --channelID common
     * Example output:
     * Approved chaincode definition for chaincode 'dns' on channel 'common':
     * sequence: 1, version: 1.0, init-required: false, package-id: dns_1.0:9e372cb9a05da4cf96550212c6ec8f7e17935cda9dbecf1a7d8ceccb99cf9c67, endorsement plugin: escc, validation plugin: vscc
     *
     * @param line
     * @return
     */
    public static Chaincode fromApprovedLine(String line) {
        Pattern p = Pattern.compile("sequence: (.+), version: (.+), init-required: (.+), package-id: (.+):(.+), endorsement plugin: (.+), validation plugin: (.+)");
        Matcher m = p.matcher(line);
        return m.find() ? Chaincode.ofApproved(m.group(1), m.group(2), m.group(5), m.group(4), m.group(3)) : Chaincode.empty;
    }


    /**
     * Parsing from CLI output of `peer lifecycle querycommitted` command for specified chaincode name
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
    public static Chaincode fromCommittedLine(String chaincodeName, String line) {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Parsing from CLI output of `peer lifecycle querycommitted` command when chaincode name is not specified
     * Command:
     * peer lifecycle chaincode querycommitted --channelID common
     * Example output:
     * Committed chaincode definitions on channel 'common':
     * Name: dns, Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc
     *
     * @param line
     * @return
     */
    public static Chaincode fromCommittedLine(String line) {
        Pattern p = Pattern.compile("Name: (.+), Version: (.+), Sequence: (.+), Endorsement Plugin: (.+), Validation Plugin: (.+)");
        Matcher m = p.matcher(line);
        return m.find() ? new Chaincode(m.group(1), m.group(2)) : Chaincode.empty;
    }
}
