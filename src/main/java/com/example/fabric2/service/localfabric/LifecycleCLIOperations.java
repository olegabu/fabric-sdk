package com.example.fabric2.service.localfabric;

import com.example.fabric2.flowtools.cli.ConsoleOutputParsers;
import com.example.fabric2.flowtools.cli.FlowCmdExec;
import com.example.fabric2.model.Chaincode;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LifecycleCLIOperations {

    @Value("${core_peer_localmspid}")
    private String CORE_PEER_LOCALMSPID;
    @Value("${orderer_name}")
    private String ORDERER_NAME;
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
    @Value("${orderer_listen_port:7050}")
    private String ORDERER_GENERAL_LISTENPORT;
    @Value("${fabric.peer.command:peer}")
    private String peerCommand;

    private final FlowCmdExec<Chaincode> chaincodeCmdExec;
    private final FlowCmdExec<String> plainCmdExec;


    public Flux<Chaincode> getInstalledChaincodes() {
        String[] command = joinTLSOpts(joinCommand(peerCommand, "lifecycle chaincode queryinstalled"));
        return chaincodeCmdExec.exec(command, ConsoleOutputParsers.ConsoleInstalledListToChaincodesParser, prepareEnvironment());
    }

    public Flux<Chaincode> getApprovedChaincodes(String channelId, String chaincodeName) {
        String[] command = joinTLSOpts(joinCommand(peerCommand, "lifecycle chaincode queryapproved", "--channelID", channelId, "--name", chaincodeName));
        return chaincodeCmdExec.exec(command, ConsoleOutputParsers.ConsoleApprovedListToChaincodesParser, prepareEnvironment());
    }

    public Flux<String> getApprovedString(String channelId, String chaincodeName) {
        String[] command = joinTLSOpts(joinCommand(peerCommand, "lifecycle chaincode queryapproved", "--channelID", channelId, "--name", chaincodeName));
        return plainCmdExec.exec(command, ConsoleOutputParsers.ConsoleOutputToStringParser, prepareEnvironment());
    }

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        String[] command = joinTLSOpts(joinCommand(peerCommand, "lifecycle chaincode querycommitted --channelID", channelId));
        return chaincodeCmdExec.exec(command, ConsoleOutputParsers.ConsoleCommitedListToChaincodesParser, prepareEnvironment());
    }

    public Mono<String> installChaincodeFromPackage(Path pkgTempPath) {
        String[] command = joinCommand(peerCommand, "lifecycle chaincode install ", pkgTempPath.toAbsolutePath().toString());
        return Mono.from(plainCmdExec.exec(command, ConsoleOutputParsers.ConsoleOutputToStringParser, prepareEnvironment()));
    }

    public Mono<String> approveChaincode(String channelId, String chaincodeName, String version, String packageId, Integer sequence, Boolean initRequired) {
        String[] command = joinTLSOpts(joinCommand(peerCommand, "lifecycle chaincode approveformyorg",
                "--channelID", channelId,
                "--name", chaincodeName,
                "--version", version,
                "--package-id", packageId,
                "--sequence", String.valueOf(sequence),
                "-o", getOrdererAddressParam()
        ));
        command = joinIsInitRequired(command, initRequired);
        return Mono.from(plainCmdExec.exec(command, ConsoleOutputParsers.ConsoleOutputToStringParser, prepareEnvironment()));
    }

    public Mono<Boolean> checkCommitReadiness(String org, String channelId, String chaincodeName, String version, Integer sequence) {
        String[] command = joinTLSOpts(joinCommand(peerCommand, "lifecycle chaincode checkcommitreadiness",
                "--channelID", channelId,
                "--name", chaincodeName,
                "--version", version,
                "--sequence", String.valueOf(sequence),
                "-o", getOrdererAddressParam()));

        Flux<String> commandOutput = plainCmdExec.exec(command, ConsoleOutputParsers.ConsoleOutputToStringParser, prepareEnvironment());

        return Mono.from(commandOutput.skip(1)
                .map(orgStatus -> StringUtils.split(":"))
                .filter(arr -> StringUtils.equals(org, ArrayUtils.get(arr, 0)))
                .map(arr -> BooleanUtils.toBoolean(ArrayUtils.get(arr, 1)))
                .defaultIfEmpty(false));
    }

    public Mono<String> commitChaincode(String channelId, String chaincodeName, String version, Integer newSequence, Boolean initRequired) {
        String[] command = joinTLSOpts(joinCommand(peerCommand, "lifecycle chaincode commit",
                "--channelID", channelId,
                "--name", chaincodeName,
                "--version", version,
                "--sequence", String.valueOf(newSequence),
                "-o", getOrdererAddressParam()));
        command = joinIsInitRequired(command, initRequired);
        return Mono.from(plainCmdExec.exec(command, ConsoleOutputParsers.ConsoleOutputToStringParser, prepareEnvironment())).log();
    }


    private String getOrdererAddressParam() {
        return String.format("%s.%s:%s", ORDERER_NAME, ORDERER_DOMAIN, ORDERER_GENERAL_LISTENPORT);
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

    private String[] joinCommand(String[] command, String... opts) {
        return Stream.concat(Arrays.stream(command), Stream.of(opts)).toArray(String[]::new);
    }

    private String[] joinTLSOpts(String[] command) {
        return joinCommand(command, "--tls", "--cafile",
                String.format("/etc/hyperledger/crypto-config/ordererOrganizations/%s/msp/tlscacerts/tlsca.%s-cert.pem", ORDERER_DOMAIN, ORDERER_DOMAIN));
    }

    private String[] joinIsInitRequired(String[] command, Boolean initRequired) {
        if (BooleanUtils.toBoolean(initRequired)) {
            command = joinCommand(command, "--init-required");
        }
        return command;
    }

}

