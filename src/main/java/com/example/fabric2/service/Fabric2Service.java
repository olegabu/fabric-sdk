package com.example.fabric2.service;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeLocalHostService;
import com.example.fabric2.service.localfabric.LifecycleCLIOperations;
import com.example.fabric2.service.localfabric.SdkOperations;
import com.example.fabric2.service.management.PortAssigner;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class Fabric2Service {


    private final LifecycleCLIOperations cliOperations;
    private final SdkOperations sdkOperations;
    private final ExternalChaincodeClientService chaincodeClientService;
    private final ExternalChaincodeLocalHostService chaincodeHostService;
    private final PortAssigner portAssigner;


    public Flux<Chaincode> getInstalledChaincodes() {
        return cliOperations.getInstalledChaincodes();
    }

    public Flux<Chaincode> getApprovedChaincodes(String channelId, String chaincodeName) {
        return cliOperations.getApprovedChaincodes(channelId, chaincodeName);
    }

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        return cliOperations.getCommittedChaincodes(channelId);
    }

    public Mono<Tuple2<String, Integer>> approveChaincode(String channelId, String chaincodeName, String version, String packageId) {
        return /*cliOperations.getInstalledChaincodes()
                .filter(c -> StringUtils.equals(packageId, c.getPackageId()))
                .take(1)
                .flatMap(c ->*/
                getChaincodeApprovalSequence(channelId, chaincodeName, packageId)
                        .map(lastApprovalSequence -> lastApprovalSequence + 1)
                        .flatMap(newSequenceNum ->
                                cliOperations.approveChaincode(channelId, chaincodeName, version, packageId, newSequenceNum)
                                        .map(out->Tuple.of(packageId, newSequenceNum)));
    }

    @NotNull
    private Mono<Integer> getChaincodeApprovalSequence(String channelId, String chaincodeName, String packageId) {
        return Mono.from(cliOperations.getCommittedChaincodes(channelId)
                .filter(committedChaincode->StringUtils.equals(chaincodeName, committedChaincode.getName())) //TODO: add getCommittedChaincodes (channel, chaincodeName)
//                .filter(a -> StringUtils.equals(packageId, a.getPackageId()))
                .map(Chaincode::getSequence))
                .onErrorReturn(0)
                .defaultIfEmpty(0);
    }


    public Mono<String> deployExternalChaincode(ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection,
                                                Mono<FilePart> filePartFlux) {

        return installExternalChaincodePeerPart(metadata, sdkAgentConnection).flatMap(
                (tuple2) -> chaincodeClientService.requestRunExternalChaincode(sdkAgentConnection, metadata.getLabel(), tuple2._1.getChaincodePort(), filePartFlux));
    }

    public Mono<Tuple2<ExternalChaincodeConnection, Chaincode>> installExternalChaincodePeerPart(ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection) {

        return portAssigner.assignRemotePort(sdkAgentConnection).map(
                chaincodePort -> prepareConnectionJson(chaincodePort, sdkAgentConnection)).flatMap(
                connectionJson -> chaincodeHostService.installExternalChaincodePeerPart(metadata, connectionJson)
                        .map(result -> Tuple.of(connectionJson, result)));

    }


    public Mono<String> approveChaincode(String channelId, String chaincodeName) {
        return null;//TODO
    }

    private ExternalChaincodeConnection prepareConnectionJson(Integer chaincodePort, SdkAgentConnection sdkAgentConnection) {
        return ExternalChaincodeConnection.of(sdkAgentConnection.getHost(), chaincodePort, "TODO");
    }

    private MultiValueMap<String, HttpEntity<?>> buildMultipartBody(Mono<FilePart> filePartFlux, ExternalChaincodeMetadata metadata) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.asyncPart("file", filePartFlux, FilePart.class);
        builder.part("label", metadata.getLabel());
        return builder.build();
    }


    public Mono<Chaincode> installChaincodeFromPackage(InputStream packageInStream) {
        return chaincodeHostService.installChaincodeFromInputStreamPackage(packageInStream);
    }

    public Mono<Boolean> checkCommitReadiness(String org, String channelId, String chaincodeName, String version, Integer sequence) {
        return chaincodeHostService.checkCommitReadiness(org, channelId, chaincodeName, version, sequence);
    }

    public Mono<String> commitChaincode(String channelId, String chaincodeName, String version, Integer sequence) {
        return chaincodeHostService.commitChaincode(channelId, chaincodeName, version, sequence);
    }
}
