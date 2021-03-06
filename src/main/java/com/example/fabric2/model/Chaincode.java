package com.example.fabric2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chaincode {

    public static final Chaincode empty = new Chaincode();

    private String chaincodeName;
    private String version;
    private String packageId;
    private String label;
    private Integer sequence;
    private Boolean initRequired;

    public static Chaincode ofInstalled(String packageId, String label) {
        Chaincode result = new Chaincode();
        result.packageId = packageId;
        result.label = label;
        return result;
    }

    public static Chaincode ofApproved(String sequence, String version, String packageId, String initRequired) {
        return ofApproved(Integer.valueOf(sequence), version, packageId, BooleanUtils.toBoolean(initRequired));
    }

    public static Chaincode ofApproved(Integer sequence, String version, String packageId, Boolean initRequired) {
        Chaincode result = new Chaincode();
        result.sequence = Integer.valueOf(sequence);
        result.version = version;
        result.packageId = packageId;
        result.initRequired = BooleanUtils.toBoolean(initRequired);
        return result;
    }

    public static Chaincode ofCommitted(String name, String version, String committedSequence) {
        Chaincode result = new Chaincode();
        result.chaincodeName = name;
        result.version = version;
        result.sequence = Integer.valueOf(committedSequence);
        return result;
    }

    public Chaincode(String name, String version, Integer sequence) {
        this.chaincodeName = name;
        this.version = version;
        this.sequence = sequence;
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
        Pattern p = Pattern.compile("Package ID: (.+), Label: (.+)");
        Matcher m = p.matcher(line);
        return m.find() ? Chaincode.ofInstalled(m.group(1), m.group(2)) : Chaincode.empty;
    }

    public static Chaincode fromInstallChaincodeCmdResult(String consoleOutput) {
        Pattern p = Pattern.compile("Chaincode code package identifier: (.+):(.+)", Pattern.MULTILINE);
        Matcher m = p.matcher(consoleOutput);
        return m.find() ? Chaincode.ofInstalled(m.group(2), m.group(1)) : Chaincode.empty;
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
        Pattern p = Pattern.compile("sequence: (.+), version: (.+), init-required: (.+), package-id: (.+), endorsement plugin: (.+), validation plugin: (.+)");
        Matcher m = p.matcher(line);
        return m.find() ? Chaincode.ofApproved(m.group(1), m.group(2), m.group(4), m.group(3)) : Chaincode.empty;
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
        return m.find() ? Chaincode.ofCommitted(m.group(1), m.group(2), m.group(3)) : Chaincode.empty;
    }
}
