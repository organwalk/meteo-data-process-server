package com.weather.handler.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.common.constants.UdpCodes;
import com.weather.common.support.StationTableNameResolver;
import com.weather.common.support.SyncTaskKey;
import com.weather.config.UdpProperties;
import com.weather.entity.table.MeteoData;
import com.weather.mapper.SaveToMySQLMapper;
import com.weather.repository.RedisRepository;
import com.weather.service.support.SyncTaskCoordinator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UdpResponseProcessor {
    private final ObjectMapper objectMapper;
    private final RedisRepository redisRepository;
    private final SaveToMySQLMapper saveToMySQLMapper;
    private final SyncTaskCoordinator syncTaskCoordinator;
    private final StationTableNameResolver tableNameResolver;
    private final UdpProperties udpProperties;

    public void handle(JsonNode response) {
        int code = response.path("code").asInt();
        switch (code) {
            case UdpCodes.TOKEN_RESPONSE -> handleToken(response);
            case UdpCodes.VOID_TOKEN_RESPONSE -> handleVoidToken(response);
            case UdpCodes.STATION_CODE_RESPONSE -> handleStationCode(response);
            case UdpCodes.DATE_RANGE_RESPONSE -> handleDateRange(response);
            case UdpCodes.METEO_DATA_RESPONSE -> handleMeteoData(response);
            default -> {
            }
        }
    }

    private void handleToken(JsonNode response) {
        String username = text(response, "username");
        String token = text(response, "token");
        if (username != null && token != null) {
            redisRepository.saveToken(username, token);
            redisRepository.saveToken(udpProperties.getAuthUsername(), token);
            syncTaskCoordinator.complete(SyncTaskKey.token(username), true);
            syncTaskCoordinator.completeFirstByPrefix("TOKEN:", true);
        }
    }

    private void handleVoidToken(JsonNode response) {
        String username = text(response, "username");
        if (username != null) {
            redisRepository.deleteToken(username);
        }
    }

    private void handleStationCode(JsonNode response) {
        List<StationInfo> stationInfos = extractStations(response);
        for (StationInfo stationInfo : stationInfos) {
            saveToMySQLMapper.insertStation(stationInfo.getStation(), stationInfo.getName());
            saveToMySQLMapper.createMeteoDataTableIfNotExists(tableNameResolver.resolve(stationInfo.getStation()));
        }
        syncTaskCoordinator.completeFirstByPrefix("STATION_CODE:", !stationInfos.isEmpty());
    }

    private void handleDateRange(JsonNode response) {
        String station = text(response, "station");
        List<String> dateRange = extractStringList(response.get("date"));
        for (String date : dateRange) {
            redisRepository.saveDateRange(station, date);
            saveToMySQLMapper.insertStationDateRange(date, station);
        }
        syncTaskCoordinator.complete(SyncTaskKey.dateRange(station), !dateRange.isEmpty());
    }

    private void handleMeteoData(JsonNode response) {
        String station = text(response, "station");
        String date = text(response, "date");
        int last = response.path("last").asInt();
        JsonNode dataNode = normalizeDataNode(response.get("data"));
        if (station == null || date == null || dataNode == null || !dataNode.isArray()) {
            syncTaskCoordinator.completeFirstByPrefix("METEO_DATA:" + station + ":", false);
            return;
        }

        for (JsonNode row : dataNode) {
            if (row.isArray() && row.size() >= 9) {
                String payload = row.toString();
                double score = ZonedDateTime.of(
                                LocalDateTime.parse(date + " " + row.get(0).asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                ZoneId.of("Asia/Shanghai"))
                        .toEpochSecond();
                redisRepository.saveMeteoData(station, date, payload, score);
            }
        }

        if (last == 1) {
            saveMeteoToMySQL(station, date);
            syncTaskCoordinator.completeFirstByPrefix("METEO_DATA:" + station + ":", true);
        }
    }

    private void saveMeteoToMySQL(String station, String date) {
        try {
            List<MeteoData> meteoDataList = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            var meteoSet = redisRepository.getMeteoData(station, date);
            if (meteoSet == null) {
                return;
            }
            for (var tuple : meteoSet) {
                JsonNode row = objectMapper.readTree(tuple.getValue().toString());
                meteoDataList.add(new MeteoData(
                        station,
                        simpleDateFormat.parse(date),
                        new Date((long) (tuple.getScore() * 1000)),
                        Time.valueOf(row.get(0).asText()),
                        (float) row.get(1).asDouble(),
                        (float) row.get(2).asDouble(),
                        (float) row.get(3).asDouble(),
                        (float) row.get(4).asDouble(),
                        (float) row.get(5).asDouble(),
                        (float) row.get(6).asDouble(),
                        (float) row.get(7).asDouble(),
                        (float) row.get(8).asDouble()
                ));
            }
            String tableName = tableNameResolver.resolve(station);
            saveToMySQLMapper.createMeteoDataTableIfNotExists(tableName);
            saveToMySQLMapper.insertMeteoData(tableName, meteoDataList);
        } catch (Exception exception) {
            syncTaskCoordinator.completeFirstByPrefix("METEO_DATA:" + station + ":", false);
        }
    }

    private JsonNode normalizeDataNode(JsonNode dataNode) {
        if (dataNode == null || dataNode.isNull()) {
            return null;
        }
        if (dataNode.isTextual()) {
            try {
                return objectMapper.readTree(dataNode.asText());
            } catch (Exception exception) {
                return null;
            }
        }
        return dataNode;
    }

    private List<StationInfo> extractStations(JsonNode node) {
        List<StationInfo> stationInfos = new ArrayList<>();
        walkStations(node, stationInfos);
        return stationInfos;
    }

    private void walkStations(JsonNode node, List<StationInfo> stationInfos) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            if (node.hasNonNull("station") && node.hasNonNull("name")) {
                stationInfos.add(new StationInfo(node.get("station").asText(), node.get("name").asText()));
            }
            Iterator<JsonNode> fields = node.elements();
            while (fields.hasNext()) {
                walkStations(fields.next(), stationInfos);
            }
            return;
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                walkStations(child, stationInfos);
            }
        }
    }

    private List<String> extractStringList(JsonNode node) {
        List<String> results = new ArrayList<>();
        if (node == null || node.isNull()) {
            return results;
        }
        if (node.isArray()) {
            for (JsonNode item : node) {
                results.add(item.asText());
            }
            return results;
        }
        if (node.isTextual()) {
            try {
                JsonNode parsed = objectMapper.readTree(node.asText());
                return extractStringList(parsed);
            } catch (Exception ignored) {
                String[] parts = node.asText().split(",");
                for (String part : parts) {
                    results.add(part.replace("[", "").replace("]", "").replace("\"", "").trim());
                }
            }
        }
        return results;
    }

    private String text(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName) || node.get(fieldName).isNull()) {
            return null;
        }
        return node.get(fieldName).asText();
    }

    @Data
    @AllArgsConstructor
    private static class StationInfo {
        private String station;
        private String name;
    }
}
