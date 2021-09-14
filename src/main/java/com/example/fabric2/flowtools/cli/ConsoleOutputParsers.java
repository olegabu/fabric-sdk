package com.example.fabric2.flowtools.cli;

import com.example.fabric2.model.Chaincode;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

public class ConsoleOutputParsers {

    public static final Function<InputStream, Flux<String>> ConsoleLinesToStringParser = (inputStream) -> {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return Flux.fromStream(bufferedReader.lines()).log();
    };

    public static final Function<InputStream, Flux<Chaincode>> ConsoleLinesToChaincodeParser = (inputStream) -> {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return Flux.fromStream(bufferedReader.lines()).log().skip(1).map(Chaincode::fromLine);
    };
}