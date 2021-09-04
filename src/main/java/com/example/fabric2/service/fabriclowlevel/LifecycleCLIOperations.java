package com.example.fabric2.service.fabriclowlevel;

import com.example.fabric2.flowtools.cli.ConsoleOutputParsers;
import com.example.fabric2.flowtools.cli.FlowCmdExec;
import com.example.fabric2.model.Chaincode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LifecycleCLIOperations {

    @Value("${fabric.peer.command:peer}")
    private String peerCommand;

    private final FlowCmdExec<Chaincode> flowCmdExec;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        String[] command = joinCommand(peerCommand, "lifecycle chaincode querycommitted --channelID ", channelId);
        return flowCmdExec.exec(command, ConsoleOutputParsers.ConsoleLinesToChaincodeParser);
    }

    private String[] joinCommand(String... commands) {
        return Stream.of(commands)
                .map(s -> s.split(" "))
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }
}