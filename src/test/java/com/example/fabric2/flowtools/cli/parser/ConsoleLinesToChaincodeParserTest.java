package com.example.fabric2.flowtools.cli.parser;

import com.example.fabric2.model.Chaincode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsoleLinesToChaincodeParserTest {

    @Test
    public void testInstalledOutputParsing() {
        String packageId = "8b3c09500c292ed5d93733b0513e8212baf71751710c8284789521d4ff1f67e2";
        String label = "testlabel";
        Chaincode chaincode = Chaincode.fromInstalledLine("Package ID: testlabel:" + packageId + ", Label: " + label);
        assertThat(chaincode).isEqualTo(new Chaincode(label, null, packageId, label, null, null));
    }

    @Test
    public void testCommittedOutputParsing() {
        Chaincode chaincode = Chaincode.fromCommittedLine("Name: dns, Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc");
        assertThat(chaincode).isEqualTo(new Chaincode("dns", "1.0"));
    }
}
