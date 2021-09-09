package com.example.fabric2.service.fabriclowlevel;

import com.example.fabric2.flowtools.cli.ConsoleOutputParsers;
import com.example.fabric2.flowtools.cli.FlowCmdExec;
import com.example.fabric2.model.Chaincode;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LifecycleCLIOperations {

    @Value("CORE_PEER_LOCALMSPID")
    private String CORE_PEER_LOCALMSPID;
    @Value("CORE_PEER_TLS_ROOTCERT_FILE")
    private String CORE_PEER_TLS_ROOTCERT_FILE;
    @Value("CORE_PEER_MSPCONFIGPATH")
    private String CORE_PEER_MSPCONFIGPATH;
    @Value("CRYPTO_CONFIG_DIR")
    private String cryptoConfigDir;
    @Value("${fabric.peer.command:peer}")
    private String peerCommand;

    private final FlowCmdExec<Chaincode> flowCmdExec;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        String[] command = joinCommand(peerCommand, "lifecycle chaincode querycommitted --channelID ", channelId);
        Map<String, String> env = prepareEnvironment();
        return flowCmdExec.exec(command, ConsoleOutputParsers.ConsoleLinesToChaincodeParser, env);
    }

    private Map<String, String> prepareEnvironment() {
        return HashMap.of("CORE_PEER_LOCALMSPID", CORE_PEER_LOCALMSPID,
                "CORE_PEER_TLS_ROOTCERT_FILE", CORE_PEER_TLS_ROOTCERT_FILE,
                "CORE_PEER_MSPCONFIGPATH", CORE_PEER_MSPCONFIGPATH);
    }

    private String[] joinCommand(String... commands) {
        return Stream.of(commands)
                .map(s -> s.split(" "))
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }
}