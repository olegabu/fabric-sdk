package com.example.fabric2.flowtools.cli;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Log4j2
public class FlowCmdExec<T> {

    private final BlockingQueue<Process> queue = new LinkedBlockingQueue<>();

    public Flux<T> exec(String[] command, Function<InputStream, Flux<T>> recordParser) {
        return exec(command, recordParser, HashMap.empty());
    }

    public Flux<T> exec(String[] command, Function<InputStream, Flux<T>> recordParser, Map<String, String> environment) {

        log.info("Running command {}", (Object) command);
        Process run = new CmdRunner().run(command, environment);
        run.onExit().thenApply(process -> queue.offer(process));

        log.info("Parsing output");
        Flux<T> successOutput = recordParser.apply(run.getInputStream()).log();
        log.info("Wait for exit code");
        Flux<T> errorOutput = waitForExitCodeAndGetErrorOutput(queue);

        return Flux.merge(successOutput, errorOutput);
    }


    public static class CmdRunner {
        public Process run(String[] command, Map<String, String> environment) {
            return Try.of(() -> {
                ProcessBuilder processBuilder = new ProcessBuilder(command);

                java.util.Map<String, String> processEnv = processBuilder.environment();
                environment.forEach(processEnv::put);

                return processBuilder.start();
            }).getOrElseThrow(e -> new RuntimeException(String.format("Error at exec command ", (Object[]) command), e));
        }
    }

    private Flux<T> waitForExitCodeAndGetErrorOutput(BlockingQueue<Process> queue) {
        return Flux.create(fluxSink -> {
            try {
                Process process = queue.take();
                if (process.exitValue() == 0) {
                    log.info("Success");
                    fluxSink.complete();
                } else {
                    try {
                        String errorMessage = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                        log.info("Error exit code: {}, {}", process.exitValue(), errorMessage);
                        fluxSink.error(new RuntimeException(errorMessage));
                    } catch (IOException e) {
                        log.error(e);
                        fluxSink.error(e);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}


