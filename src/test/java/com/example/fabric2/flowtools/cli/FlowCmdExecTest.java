package com.example.fabric2.flowtools.cli;

import com.example.fabric2.model.Chaincode;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Profile("integration-tests")
@SpringBootTest
@Log4j2
public class FlowCmdExecTest {

    @Value("${fabric2-api.cli.container}")
    private String cliDockerContainerName;

    @Autowired
    private FlowCmdExec<String> stringFlowCmdExec;
    @Autowired
    private FlowCmdExec<Chaincode> chaincodeFlowCmdExec;

    @Test
    public void testGettingCommitedChaincodesList() {
        Flux<Chaincode> exec = chaincodeFlowCmdExec.exec(
                new String[]{"docker", "exec", cliDockerContainerName, "peer", "lifecycle", "chaincode",
                        "querycommitted", "--channelID", "common"},
                ConsoleOutputParsers.ConsoleLinesToChaincodeParser);

        StepVerifier.create(exec)
                .expectNext(new Chaincode("dns", "1.0"))
                .verifyComplete();
    }

    @Test
    public void testPeerVersion() {

        Flux<String> exec = stringFlowCmdExec.exec(
                new String[]{"docker", "exec", cliDockerContainerName, "peer", "version"},
                ConsoleOutputParsers.ConsoleLinesToStringParser);

        StepVerifier.create(exec).
                expectNext("peer:").
                assertNext(versionInfo -> {
                    log.debug(versionInfo);
                    assertThat(versionInfo).containsPattern("Version: 2\\.\\d\\.\\d?");
                })
                .expectNextCount(7)
                .verifyComplete();

    }

}
