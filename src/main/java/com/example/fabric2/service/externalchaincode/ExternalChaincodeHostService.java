package com.example.fabric2.service.externalchaincode;

import com.example.fabric2.service.localfabric.LifecycleCLIOperations;
import com.example.fabric2.util.CommonUtils;
import com.example.fabric2.util.TmpFiles;
import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExternalChaincodeHostService {

    private final LifecycleCLIOperations cliOperations;
    private final CommonUtils utils;
    private final TmpFiles tmpFiles;

    @Data
    public static class Result {
        private int resultCode;
    }


    public Flux<Result> runChaincodeOnThisHost(ChaincodeTargetPlatform targetPlatform, ChaincodeServerParams params) {
        return null;
    }


    public Flux<String> installChaincodeFromPackage(InputStream packageInStream) {
        Path path = savePackageToFile(packageInStream);
        return Try.of(() -> cliOperations.installChaincodeFromPackage(path))
                .andFinally(() -> Try.of(() -> Files.deleteIfExists(path)))
                .get();
    }


    private Path savePackageToFile(InputStream packageInStream) {
        Path pkgFilePath = tmpFiles.generateTmpFileName("chaincode-from-package", "tar.gz");
        pkgFilePath = utils.saveStreamToFile(packageInStream, pkgFilePath);
        return pkgFilePath;
    }

}
