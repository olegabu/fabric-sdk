package com.example.fabric2.api;

import com.example.fabric2.service.management.PackageRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController()
@RequestMapping(value = "/control")
@RequiredArgsConstructor
public class ManagementController {

    @Value("${start_ports_pool:9990}")
    private Integer firstPortInPool;

    private final PackageRunner packageRunner;

    @GetMapping(path = "/assignport")
    public Mono<Integer> reserveNextFreePort() {
        return Mono.just(firstPortInPool); //TODO: dynamic, store
    }

    @PostMapping(path = {"/run-package-on-system/{chaincodeName}/{port}",  "/run-package-on-system/{chaincodeName}/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> runChaincodeOnHost(@PathVariable String chaincodeName, @PathVariable (required = false) Integer port,
                                           @RequestPart Map<String, String> env,
                                           @RequestPart Mono<FilePart> packageToRun) {

        return packageRunner.runTarGzPackage(chaincodeName, port, io.vavr.collection.HashMap.ofAll(env), packageToRun);
    }

}
