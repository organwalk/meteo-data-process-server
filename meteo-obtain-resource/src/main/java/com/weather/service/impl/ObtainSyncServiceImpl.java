package com.weather.service.impl;

import com.weather.common.support.SyncTaskKey;
import com.weather.service.ObtainSyncService;
import com.weather.service.UdpRequestService;
import com.weather.service.support.SyncTaskCoordinator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ObtainSyncServiceImpl implements ObtainSyncService {
    private final UdpRequestService udpRequestService;
    private final SyncTaskCoordinator syncTaskCoordinator;

    @Override
    public boolean getToken(String name) {
        String taskKey = SyncTaskKey.token(name);
        CompletableFuture<Boolean> future = syncTaskCoordinator.register(taskKey);
        boolean ready = udpRequestService.getToken(name);
        if (ready) {
            syncTaskCoordinator.complete(taskKey, true);
            return true;
        }
        return syncTaskCoordinator.await(taskKey, future);
    }

    @Override
    public boolean voidToken(String name) {
        return udpRequestService.voidToken(name);
    }

    @Override
    public boolean getStationCode(String name) {
        String taskKey = SyncTaskKey.stationCode(name);
        CompletableFuture<Boolean> future = syncTaskCoordinator.register(taskKey);
        if (!udpRequestService.getAllStationCode(name)) {
            return false;
        }
        return syncTaskCoordinator.await(taskKey, future);
    }

    @Override
    public boolean getDateRange(String name, String station) {
        String taskKey = SyncTaskKey.dateRange(station);
        CompletableFuture<Boolean> future = syncTaskCoordinator.register(taskKey);
        if (!udpRequestService.getAllStationDataRange(name, station)) {
            return false;
        }
        return syncTaskCoordinator.await(taskKey, future);
    }

    @Override
    public boolean getMeteoData(String name, String station, String start, String end) {
        String taskKey = SyncTaskKey.meteoData(station, start, end);
        CompletableFuture<Boolean> future = syncTaskCoordinator.register(taskKey);
        if (!udpRequestService.getMeteoData(name, station, start, end)) {
            return false;
        }
        return syncTaskCoordinator.await(taskKey, future);
    }
}
