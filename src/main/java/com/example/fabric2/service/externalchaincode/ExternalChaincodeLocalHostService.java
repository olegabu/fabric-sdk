package com.example.fabric2.service.externalchaincode;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.model.Chaincode;
import com.example.fabric2.service.localfabric.LifecycleCLIOperations;
import com.example.fabric2.util.ChaincodeUtils;
import com.example.fabric2.util.FileUtils;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExternalChaincodeLocalHostService {

    private final LifecycleCLIOperations cliOperations;
    private final FileUtils fileUtils;
    private final ChaincodeUtils chaincodeUtils;

    public Mono<Chaincode> installExternalChaincodePeerPart(ExternalChaincodeMetadata metadata, ExternalChaincodeConnection connectionJson) {
        return chaincodeUtils.prepareLifecyclePackageStreamForExternalChaincode(metadata, connectionJson)
                .flatMap(inputStreamOfPackage -> installChaincodeFromInputStreamPackage(inputStreamOfPackage));
    }


    public Mono<Chaincode> installChaincodeFromInputStreamPackage(InputStream packageInStream) {
        Path path = fileUtils.savePackageToFile(packageInStream);
        return Try.of(() -> cliOperations.installChaincodeFromPackage(path)
                .map(Chaincode::fromInstallChaincodeCmdResult)
                .filter(chaincode -> chaincode != Chaincode.empty)
                .doFinally((signal) -> Try.of(() ->
                {
                    log.info("Files.deleteIfExists(path)");
                    Files.deleteIfExists(path);
                            return true;
                })))
                .get();

        /*    public Flux<String> installChaincode(String label, String version, String lang, String path, ) {
                Try.of(()-> {
                        LifecycleChaincodePackage lifecycleChaincodePackage = LifecycleChaincodePackage
                        .fromSource(label, Paths.get("src/test/fixture/sdkintegration/gocc/sample1"),
                                TransactionRequest.Type.valueOf(lang),
                                "github.com/example_cc", Paths.get("src/test/fixture/meta-infs/end2endit"));
            })

            }*/
    }


    public Mono<Boolean> checkCommitReadiness(String org, String channelId, String chaincodeName, String version, Integer sequence) {
        return cliOperations.checkCommitReadiness(org, channelId, chaincodeName, version, sequence);
    }

    public Mono<String> commitChaincode(String channelId, String chaincodeName, String version, Integer newSequence) {
        return cliOperations.commitChaincode(channelId, chaincodeName, version, newSequence);
    }
}
