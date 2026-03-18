package com.weather.service.support;

import com.weather.config.UdpProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SyncTaskCoordinatorTest {
    @Test
    void shouldCompleteRegisteredTask() {
        UdpProperties properties = new UdpProperties();
        properties.setAwaitTimeout(Duration.ofSeconds(1));
        SyncTaskCoordinator coordinator = new SyncTaskCoordinator(properties);

        CompletableFuture<Boolean> future = coordinator.register("TOKEN:test");
        coordinator.complete("TOKEN:test", true);

        assertTrue(coordinator.await("TOKEN:test", future));
    }
}
