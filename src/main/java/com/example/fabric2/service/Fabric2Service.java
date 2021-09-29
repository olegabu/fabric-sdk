package com.example.fabric2.service;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.InstallChaincodeResult;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.chaincode.ChaincodeLocalHostService;
import com.example.fabric2.service.externalchaincode.ExternalChaincodeClientService;
import com.example.fabric2.service.localfabric.LifecycleCLIOperations;
import com.example.fabric2.service.localfabric.SdkOperations;
import com.example.fabric2.service.management.PackageHandler;
import com.example.fabric2.service.management.PortAssigner;
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
    private final ChaincodeLocalHostService chaincodeHostService;
    private final PortAssigner portAssigner;
    private final PackageHandler packageHandler;

    public Flux<Chaincode> getInstalledChaincodes() {
        return cliOperations.getInstalledChaincodes();
    }

    public Flux<Chaincode> getApprovedChaincodes(String channelId, String chaincodeName) {
        return cliOperations.getApprovedChaincodes(channelId, chaincodeName);
    }

    public Flux<Chaincode> getCommittedChaincodes(String channelId) {
        return cliOperations.getCommittedChaincodes(channelId);
    }

    public Mono<InstallChaincodeResult> installChaincode(Mono<FilePart> packageToRun) {
        return packageHandler.convertToInputStream(packageToRun)
                .flatMap(chaincodePackageInputStream -> chaincodeHostService.installChaincodeFromInputStream(chaincodePackageInputStream)
                        .map(result -> new InstallChaincodeResult(result, null)))
                .onErrorMap(e -> {
                    throw new RuntimeException(e);
                });

    }

    public Mono<InstallChaincodeResult> installExternalChaincodePeerPart(
            ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection, ExternalChaincodeConnection chaincodeConnection) {

        return Mono.justOrEmpty(chaincodeConnection.getChaincodePort())
                .switchIfEmpty(portAssigner.assignRemotePort(sdkAgentConnection))
                .map(chaincodePort -> prepareConnectionJson(chaincodePort, sdkAgentConnection))
                .flatMap(connectionJson -> chaincodeHostService.prepareMetadataPackageForExternalChaincode(metadata, connectionJson)
                        .flatMap(chaincodeHostService::installChaincodeFromInputStream)
                        .map(result -> new InstallChaincodeResult(result, connectionJson)));

    }

    public Mono<Chaincode> approveChaincode(String channelId, String chaincodeName, String version, String packageId, Boolean initRequired) {
        return /*cliOperations.getInstalledChaincodes()
                .filter(c -> StringUtils.equals(packageId, c.getPackageId()))
                .take(1)
                .flatMap(c ->*/
                getChaincodeApprovalSequence(channelId, chaincodeName, packageId)
                        .map(lastApprovalSequence -> lastApprovalSequence + 1)
                        .flatMap(newSequenceNum ->
                                cliOperations.approveChaincode(channelId, chaincodeName, version, packageId, newSequenceNum, initRequired)
                                        .map(notUsed -> Chaincode.ofApproved(newSequenceNum, version, packageId, initRequired))
                                        .onErrorReturn(Chaincode.ofApproved(newSequenceNum, version, packageId, initRequired))); //TODO: other errors than existing approval
    }

    @NotNull
    private Mono<Integer> getChaincodeApprovalSequence(String channelId, String chaincodeName, String packageId) {
        return Mono.from(cliOperations.getCommittedChaincodes(channelId)
                .filter(committedChaincode -> StringUtils.equals(chaincodeName, committedChaincode.getChaincodeName())) //TODO: add getCommittedChaincodes (channel, chaincodeName)
//                .filter(a -> StringUtils.equals(packageId, a.getPackageId()))
                .map(Chaincode::getSequence))
                .onErrorReturn(0)
                .defaultIfEmpty(0);
    }


    public Mono<String> deployExternalChaincode(ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection,
                                                ExternalChaincodeConnection chaincodeConnection, Mono<FilePart> filePartFlux) {

        return installExternalChaincodePeerPart(metadata, sdkAgentConnection, chaincodeConnection)
                .flatMap((installResult) ->
                        runExternalChaincode(metadata.getLabel(), installResult.getChaincode().getPackageId(), sdkAgentConnection, chaincodeConnection, filePartFlux));
    }

    public Mono<String> runExternalChaincode(String label, String packageId,
                                             SdkAgentConnection sdkAgentConnection,
                                             ExternalChaincodeConnection chaincodeConnection,
                                             Mono<FilePart> filePartFlux) {
        return chaincodeClientService.runExternalChaincode(sdkAgentConnection, label, packageId, chaincodeConnection.getChaincodePort(), filePartFlux);
    }


    private ExternalChaincodeConnection prepareConnectionJson(Integer chaincodePort, SdkAgentConnection sdkAgentConnection) {
        return ExternalChaincodeConnection.of(sdkAgentConnection.getAgentHost(), chaincodePort, "TODO");
    }

    private MultiValueMap<String, HttpEntity<?>> buildMultipartBody(Mono<FilePart> filePartFlux, ExternalChaincodeMetadata metadata) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.asyncPart("file", filePartFlux, FilePart.class);
        builder.part("label", metadata.getLabel());
        return builder.build();
    }


    public Mono<Chaincode> installChaincodeFromPackage(InputStream packageInStream) {
        return chaincodeHostService.installChaincodeFromInputStream(packageInStream);
    }

    public Mono<Boolean> checkCommitReadiness(String org, String channelId, String chaincodeName, String version, Integer sequence) {
        return chaincodeHostService.checkCommitReadiness(org, channelId, chaincodeName, version, sequence);
    }

    public Mono<String> commitChaincode(String channelId, String chaincodeName, String version, Integer newSequence) {
        return chaincodeHostService.commitChaincode(channelId, chaincodeName, version, newSequence);
    }
}
