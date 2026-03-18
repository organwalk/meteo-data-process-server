package com.weather.service.support;

import com.weather.config.UdpProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class SyncTaskCoordinator {
    private final Map<String, CompletableFuture<Boolean>> tasks = new ConcurrentHashMap<>();
    private final UdpProperties properties;

    public CompletableFuture<Boolean> register(String key) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        tasks.put(key, future);
        return future;
    }

    public boolean await(String key, CompletableFuture<Boolean> future) {
        try {
            return future.get(properties.getAwaitTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException | TimeoutException exception) {
            return false;
        } finally {
            tasks.remove(key, future);
        }
    }

    public void complete(String key, boolean success) {
        CompletableFuture<Boolean> future = tasks.remove(key);
        if (future != null) {
            future.complete(success);
        }
    }

    public void completeFirstByPrefix(String prefix, boolean success) {
        for (Map.Entry<String, CompletableFuture<Boolean>> entry : tasks.entrySet()) {
            if (entry.getKey().startsWith(prefix) && tasks.remove(entry.getKey(), entry.getValue())) {
                entry.getValue().complete(success);
                return;
            }
        }
    }
}
