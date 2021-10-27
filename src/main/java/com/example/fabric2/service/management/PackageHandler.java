package com.example.fabric2.service.management;

import com.example.fabric2.flowtools.cli.ConsoleOutputParsers;
import com.example.fabric2.flowtools.cli.FlowCmdExec;
import com.example.fabric2.util.FileUtils;
import com.example.fabric2.util.Tar;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Log4j2
public class PackageHandler {

    @Value("${remote.vm.apps.dir:/apps}")
    private String appsDir;


    private final FlowCmdExec<String> cmdExec;
    private final Tar tar;
    private final FileUtils fileUtils;
    private final Executor executor;

    private static InputStream initialEmptyStream = new ByteArrayInputStream(new byte[]{});

    public Mono<String> runTarGzPackage(String name, Integer port, Map<String, String> env, Mono<FilePart> tarGzPartFlux) {
        return convertToInputStream(tarGzPartFlux)
                .map(inputStream -> tar.extractTarGz(Path.of(appsDir, name), inputStream))
                .flatMap(resultDirPath -> {
                            fileUtils.setExecutionPermissions(resultDirPath.resolve("run.sh"));
                            return Mono.from(cmdExec.exec(resultDirPath.toFile(), new String[]{"/bin/sh", "run.sh"},
                                    ConsoleOutputParsers.ConsoleOutputToStringParser,
                                    env.put("PORT", String.valueOf(port)))
                            );
                        }
                );
    }



    public Mono<Path> convertToFile(Mono<FilePart> filePartMono) {
        AtomicInteger fileWriteOffset = new AtomicInteger(0);
        val pos = new PipedOutputStream();
        return Try.of(() -> {
            Path path = fileUtils.generateTmpFileName("package-", ".tar.gz");
//            Path path=Path.of("/tmp/ppp.tar.gz");
            Files.createFile(path);
            final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return filePartMono.flatMap(filePart->filePart.content()
                    .collect(()->pos, (result, currentBuf) -> Try.of(()->{
                        log.info("OUTPUT");
                        int bufSize = currentBuf.readableByteCount();
                        return fileChannel.write(currentBuf.asByteBuffer(), fileWriteOffset.getAndAdd(bufSize));
                    }).get()))
                    .map(notUsed->path);
        }).get();

    }


    public Mono<InputStream> convertToInputStream(Mono<FilePart> filePartMono) {
        val pos = new PipedOutputStream();
        AtomicInteger fileWriteOffset = new AtomicInteger(0);

        
        return Try.of(() -> {
            InputStream pis = new PipedInputStream(pos);
            final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Path.of("/tmp/aaaa"), StandardOpenOption.WRITE);

            return filePartMono.flatMap(filePart -> filePart.content()
                    .collect(()->pos, (result, currentBuf) -> Try.of(()->{
                        log.info("OUTPUT");
//                        InputStream input = currentBuf.asInputStream(true);
                        int delta = currentBuf.readableByteCount();
                        return fileChannel.write(currentBuf.asByteBuffer(), fileWriteOffset.getAndAdd(delta));
                    }).get()))
                    .map(p->{
                        return pis;
                    });

/*
                    .map(dataBufferFlux -> DataBufferUtils.write(dataBufferFlux, pos)
                            .subscribeOn(Sche)
                            .subscribe(DataBufferUtils.releaseConsumer()))
                    .map(d->(InputStream)pis);
*/
//                    .map(dataBufferFlux -> Tuple.of((InputStream) pis, dataBufferFlux));
        })
                /* .andFinally(() -> {
                     try {
                         pos.close();
                     } catch (IOException ignored) {
                     }
                 })*/.get();
    }
/*        return filePartMono.flatMap(filePart -> filePart.content()
                .reduce(InputStream.nullInputStream(), (resultStream, buf1) ->
                        new SequenceInputStream(resultStream, buf1.asInputStream())));
*/

/*    private static class InputStreamCollector {
        private InputStream is;

        public void collectInputStream(InputStream is) {
            if (this.is == null) this.is = is;
            this.is = new SequenceInputStream(this.is, is);
        }

        public InputStream getInputStream() {
            return this.is;
        }
    }*/

/*    @NotNull
    public void convertToInputStream(Mono<FilePart> filePartMono, PipedOutputStream pos, PipedInputStream pis) {
        executor.execute(()-> {
            filePartMono.flatMap(filePart -> {
                Flux<DataBuffer> content = filePart.content();
                DataBufferUtils.write(content, pos).subscribe(DataBufferUtils.releaseConsumer());
                Try.of(()->{pos.close();return 0;}).get();
                return Mono.empty();
            }).subscribe(s->{
                log.debug(s);
            });
        });
    }*/
}


