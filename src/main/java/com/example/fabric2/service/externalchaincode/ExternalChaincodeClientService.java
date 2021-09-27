package com.example.fabric2.service.externalchaincode;

import com.example.fabric2.dto.SdkAgentConnection;
import io.vavr.collection.HashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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

    private static InputStream initialEmptyStream = new ByteArrayInputStream(new byte[]{});


    private ParameterizedTypeReference<DataBuffer> typeRef = new ParameterizedTypeReference<>() {
    };

    public Mono<String> runExternalChaincode(SdkAgentConnection sdkAgentConnection,
                                             String label,
                                             String packageId, Integer chaincodePort,
                                             Mono<FilePart> filePartFlux) {


        return filePartFlux.flatMap((FilePart filePart) -> {
                    MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
                    multipartBodyBuilder.part("packageToRun", filePart, MediaType.APPLICATION_OCTET_STREAM);
                    multipartBodyBuilder.part("env", HashMap.of("PACKAGE_ID", packageId).toJavaMap());

                    MultiValueMap<String, HttpEntity<?>> build = multipartBodyBuilder.build();
                    return webClient.post().uri(sdkAgentConnection.getAddress() + "/control/run-package-on-system/" + label + "/" + ObjectUtils.defaultIfNull(chaincodePort, ""))
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(build))
                            .exchangeToMono(resp -> resp.bodyToMono(String.class));
                }
        );
    }

}
