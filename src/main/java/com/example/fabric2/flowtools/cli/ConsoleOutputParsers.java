package com.example.fabric2.flowtools.cli;

import com.example.fabric2.model.Chaincode;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

public class ConsoleOutputParsers {

    public static final Function<InputStream, Publisher<String>> ConsoleLinesToStringParser = (inputStream) -> {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));//TODO: check if it should be closed
        return Flux.fromStream(bufferedReader.lines()).log();
    };

    public static final Function<InputStream, Publisher<Chaincode>> ConsoleInstalledListToChaincodesParser = (inputStream) -> {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return Flux.fromStream(bufferedReader.lines()).log().skip(1).map(Chaincode::fromInstalledLine); //TODO: move skip(1) to caller
    };

    public static final Function<InputStream, Publisher<Chaincode>> ConsoleApprovedListToChaincodesParser = (inputStream) -> {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return Flux.fromStream(bufferedReader.lines()).log().skip(1).map(Chaincode::fromApprovedLine); //TODO: move skip(1) to caller
    };

    public static final Function<InputStream, Publisher<Chaincode>> ConsoleCommitedListToChaincodesParser = (inputStream) -> {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return Flux.fromStream(bufferedReader.lines()).log().skip(1).map(Chaincode::fromCommittedLine); //TODO: move skip(1) to caller
    };



    public static final Function<InputStream, Publisher<String>> ConsoleOutputToStringParser = (inputStream) -> {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return Flux.fromStream(bufferedReader.lines())
                .reduce((result, current) ->
                        result + "\n" + current) //TODO: get string right from stream
                .log();
    };

}