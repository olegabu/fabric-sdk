package com.example.fabric2.service.management;

import com.example.fabric2.flowtools.cli.ConsoleOutputParsers;
import com.example.fabric2.flowtools.cli.FlowCmdExec;
import com.example.fabric2.service.tar.Tar;
import com.example.fabric2.util.FileUtils;
import io.vavr.collection.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class PackageRunner {

    @Value("${remote.vm.apps.dir:/apps}")
    private String appsDir;


    private final FlowCmdExec<String> cmdExec;
    private final Tar tar;
    private final FileUtils fileUtils;

    private static InputStream initialEmptyStream = new ByteArrayInputStream(new byte[]{});

    public Mono<String> runTarGzPackage(String name, Integer port, Mono<FilePart> tarGzPartFlux) {
        return tarGzPartFlux.flatMap(filePart ->
                filePart.content().reduce(initialEmptyStream, (resultStream, buf1) ->
                        new SequenceInputStream(resultStream, buf1.asInputStream())))
                .map(inputStream ->
                        tar.extractTarGz(Path.of(appsDir, name), inputStream))
                .flatMap(resultDirPath -> {
                    fileUtils.setExecutionPermissions(resultDirPath.resolve("run.sh"));
                            return cmdExec.exec(resultDirPath.toFile(), new String[]{"/bin/sh", "run.sh"},
                                    ConsoleOutputParsers.ConsoleOutputToStringParser,
                                    HashMap.of("PORT", String.valueOf(port)))
                                    .reduce((s1, s2) -> s1 + s2);
                        }
                );
    }
}
