package com.example.fabric2.flowtools.cli.parser;

import com.example.fabric2.model.Chaincode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsoleLinesToChaincodeParserTest {

    @Test
    public void testParsing() {
        Chaincode chaincode = Chaincode.fromLine("Name: dns, Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc");
        assertThat(chaincode).isEqualTo(new Chaincode("dns", "1.0"));
    }
}
