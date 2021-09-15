package com.example.fabric2.service.localfabric;

import com.example.fabric2.flowtools.cli.ConsoleOutputParsers;
import com.example.fabric2.flowtools.cli.FlowCmdExec;
import com.example.fabric2.model.Chaincode;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LifecycleCLIOperations {

    @Value("${core_peer_localmspid}")
    private String CORE_PEER_LOCALMSPID;
    @Value("${orderer_domain:${domain}}")
    private String ORDERER_DOMAIN;
    @Value("${core_peer_tls_rootcert_file}")
    private String CORE_PEER_TLS_ROOTCERT_FILE;
    @Value("${core_peer_mspconfigpath}")
    private String CORE_PEER_MSPCONFIGPATH;
    @Value("${core_peer_address}")
    private String CORE_PEER_ADDRESS;
    @Value("${core_peer_tls_enabled}")
    private String CORE_PEER_TLS_ENABLED;
    @Value("${crypto_config_dir}")
    private String cryptoConfigDir;
    @Value("${fabric.peer.command:peer}")
    private String peerCommand;

    private final FlowCmdExec<Chaincode> chaincodeCmdExec;
    private final FlowCmdExec<String> plainCmdExec;

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        String[] command = joinCommand(peerCommand, "lifecycle chaincode querycommitted --channelID ", channelId,
                "--tls", "--cafile ",
                String.format("/etc/hyperledger/crypto-config/ordererOrganizations/%s/msp/tlscacerts/tlsca.%s-cert.pem", ORDERER_DOMAIN, ORDERER_DOMAIN));
        return chaincodeCmdExec.exec(command, ConsoleOutputParsers.ConsoleLinesToChaincodeParser, prepareEnvironment());
    }

    private Map<String, String> prepareEnvironment() {
        return HashMap.of(
                "CORE_PEER_LOCALMSPID", CORE_PEER_LOCALMSPID,
                "CORE_PEER_ADDRESS", CORE_PEER_ADDRESS,
                "CORE_PEER_TLS_ROOTCERT_FILE", CORE_PEER_TLS_ROOTCERT_FILE,
                "CORE_PEER_MSPCONFIGPATH", CORE_PEER_MSPCONFIGPATH,
                "CORE_PEER_TLS_ENABLED", CORE_PEER_TLS_ENABLED);
    }

    private String[] joinCommand(String... commands) {
        return Stream.of(commands)
                .map(s -> s.split(" "))
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }

    public Mono<String> installChaincodeFromPackage(Path pkgTempPath) {
        String[] command = joinCommand(peerCommand, "lifecycle chaincode install ", pkgTempPath.toAbsolutePath().toString());
        return Mono.from(plainCmdExec.exec(command, ConsoleOutputParsers.ConsoleLinesToStringParser, prepareEnvironment()));

    }

}