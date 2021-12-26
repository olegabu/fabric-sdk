package com.example.fabric2.health;

import com.example.fabric2.service.localfabric.LifecycleCLIOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PeerAccessibilityHealthIndicator extends AbstractHealthIndicator {

    private final LifecycleCLIOperations cliOperations;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        Mono.from(cliOperations.getInstalledChaincodes())
                .map(res ->
                    builder.up()
                            .withDetail("Request installed chaincodes", res.getPackageId())
                )
                .block();
    }
}
