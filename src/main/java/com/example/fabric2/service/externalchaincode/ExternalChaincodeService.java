package com.example.fabric2.service.externalchaincode;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Log4j2
public class ExternalChaincodeService {

    public enum DeploymentTargetPlatform {
        NATIVE,
        DOCKER,
        KUBERNETES
    }

    @Data
    public static class Result{
        private int resultCode;
    }


    public Flux<Result> runChaincodeOnThisHost(DeploymentTargetPlatform targetPlatform, ChaincodeServerParams params) {
        return null;
    }


}
