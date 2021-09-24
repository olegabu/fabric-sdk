package com.example.fabric2.service.externalchaincode;

import com.example.fabric2.dto.SdkAgentConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExternalChaincodeClientService {

    private final ExternalChaincodeLocalHostService chaincodeLocalService;

    private final WebClient webClient = WebClient.builder().build();

    @Data
    public static class Result {
        private int resultCode;
    }


    public Mono<String> runExternalChaincode(SdkAgentConnection sdkAgentConnection, String label, Integer chaincodePort,
                                             Mono<FilePart> filePartFlux) {
        return filePartFlux.flatMap(filePart ->
                webClient.post().uri(sdkAgentConnection.getAddress() + "/control/run-package-on-system/" + label + "/" + chaincodePort)
                        .body(BodyInserters.fromPublisher(filePart.content(), DataBuffer.class))
                        .exchangeToMono(resp -> resp.bodyToMono(String.class))
                        .log()
        );
    }

}
