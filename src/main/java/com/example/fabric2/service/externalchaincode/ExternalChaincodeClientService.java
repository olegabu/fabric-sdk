package com.example.fabric2.service.externalchaincode;

import com.example.fabric2.dto.ExternalChaincodeConnection;
import com.example.fabric2.dto.ExternalChaincodeMetadata;
import com.example.fabric2.dto.SdkAgentConnection;
import com.example.fabric2.service.management.PortAssigner;
import com.example.fabric2.service.tar.Tar;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.HashMap;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExternalChaincodeClientService {

    private final ObjectMapper objectMapper;
    private final ExternalChaincodeLocalHostService chaincodeLocalService;
    private final PortAssigner portAssigner;
    private final Tar tar;

    private final WebClient webClient= WebClient.builder().build();

    @Data
    public static class Result {
        private int resultCode;
    }


    public Mono<String> requestRunExternalChaincode (SdkAgentConnection sdkAgentConnection, Mono<FilePart> filePartFlux) {
        return filePartFlux.flatMap(filePart->
                webClient.post().uri(sdkAgentConnection.getAddress()+"/control/runchaincode")
                .body(BodyInserters.fromPublisher(filePart.content(), DataBuffer.class))
                .exchangeToMono(resp->{
                    return resp.bodyToMono(String.class);
                })
        );
    }

    public Mono<String> requestRunExternalChaincode (SdkAgentConnection sdkAgentConnection, InputStream inputStream) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", inputStream);

        return webClient.post().uri(sdkAgentConnection.getAddress())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchangeToMono(resp->resp.bodyToMono(String.class));


//        Mono<String> stringMono = webClient.post().uri(sdkAgentConnection.getAddress())
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(BodyInserters.fromMultipartData(buildMultipartBody(filePartFlux, metadata)))
//                .retrieve()
//                .bodyToMono(String.class);
//        return stringMono;
    }

    public Flux<String> installExternalChaincode(ExternalChaincodeMetadata metadata, SdkAgentConnection sdkAgentConnection) {

        return Flux.from(portAssigner.assignRemotePort(sdkAgentConnection)
                .map(chaincodePort -> prepareConnectionJson(chaincodePort, sdkAgentConnection)))
                .map(connection -> prepareTarFromMetadataAndConnectionInStream(metadata, connection))
                .flatMap(tarInputStream -> chaincodeLocalService.installChaincodeFromPackage(tarInputStream));
    }

    private InputStream prepareTarFromMetadataAndConnectionInStream(ExternalChaincodeMetadata metadata, ExternalChaincodeConnection connection) {
        metadata.setConnection(connection);
        return Try.of(() ->
        {
            InputStream codeTarGz = this.tar.createTar("connection.json", objectMapper.writeValueAsString(connection).getBytes());
            return tar.createTar(HashMap.of(
                    "metadata.json", objectMapper.writeValueAsString(metadata).getBytes(),
                    "code.tar.gz", codeTarGz.readAllBytes()));
        })
                .getOrElseThrow(e -> new RuntimeException("Error serializing json:" + metadata.toString(), e));
    }

    private ExternalChaincodeConnection prepareConnectionJson(Integer chaincodePort, SdkAgentConnection sdkAgentConnection) {
        String chaincodeAddress = sdkAgentConnection.getHost() + ":" + chaincodePort;
        return ExternalChaincodeConnection.of(chaincodeAddress, "TODO");
    }



}
