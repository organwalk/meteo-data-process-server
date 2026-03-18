package com.weather.service.sync.impl;

import com.weather.client.ObtainClient;
import com.weather.common.response.DataResult;
import com.weather.common.support.StationTableNameResolver;
import com.weather.entity.MeteoSyncReq;
import com.weather.mapper.MySQL.station.StationMapper;
import com.weather.service.sync.MeteorologySyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeteorologySyncServiceImpl implements MeteorologySyncService {
    private final ObtainClient obtainClient;
    private final StationMapper stationMapper;
    private final StationTableNameResolver tableNameResolver;

    @Override
    public DataResult connectDataSaveServer(String name) {
        return obtainClient.getToken(name)
                ? DataResult.success("宸叉垚鍔熶笌鏁版嵁瀛樺偍鏈嶅姟鍣ㄥ缓绔嬭繛鎺?")
                : DataResult.fail("鏃犳硶鏁版嵁鍚屾锛屽師鍥狅細鏈兘涓庢暟鎹瓨鍌ㄦ湇鍔″櫒寤虹珛杩炴帴");
    }

    @Override
    public DataResult syncStationData(String name) {
        return obtainClient.getStationCode(name)
                ? DataResult.success("宸叉垚鍔熷悓姝ユ洿鏂版皵璞＄珯鐐规暟鎹?")
                : DataResult.fail("鏈兘鍚屾鏇存柊姘旇薄绔欑偣鏁版嵁");
    }

    @Override
    public DataResult syncDateRange(String name) {
        List<String> stationList = stationMapper.getStationList();
        if (stationList.isEmpty()) {
            return DataResult.fail("鏈兘鍚屾鏇存柊姘旇薄鏁版嵁鏈夋晥鏃ユ湡");
        }

        boolean success = false;
        for (String station : stationList) {
            success = obtainClient.getDateRange(name, station) || success;
        }
        return success
                ? DataResult.success("宸叉垚鍔熷悓姝ユ洿鏂版皵璞℃暟鎹湁鏁堟棩鏈?")
                : DataResult.fail("姘旇薄绔欑偣鏆傛棤鏈夋晥鏃ユ湡");
    }

    @Override
    public DataResult syncLatestDate(String station) {
        String latestDate = stationMapper.meteoDataLatestDate(tableNameResolver.resolve(station), station);
        return latestDate == null
                ? DataResult.fail("璇ユ皵璞＄珯鐐规渶鏂板疄闄呴噰闆嗘棩鏈熶负绌?")
                : DataResult.success(latestDate);
    }

    @Override
    public DataResult syncHavingData(String station) {
        Integer count = stationMapper.havingDataByStationCode(tableNameResolver.resolve(station), station);
        return DataResult.success(count == null || count == 0 ? "false" : "true");
    }

    @Override
    public DataResult syncMeteoData(String name, MeteoSyncReq req) {
        boolean success = obtainClient.getMeteoData(name, req.getStation(), req.getStart(), req.getEnd());
        return success
                ? DataResult.success("宸叉垚鍔熷悓姝?" + req.getStart() + "姘旇薄鏁版嵁")
                : DataResult.fail("鏈兘鎴愬姛鍚屾" + req.getStart() + "姘旇薄鏁版嵁");
    }

    @Override
    public DataResult closeDataSaveServer(String name) {
        return obtainClient.voidToken(name)
                ? DataResult.success("宸叉垚鍔熸柇寮€涓庡瓨鍌ㄦ湇鍔″櫒鐨勮繛鎺?")
                : DataResult.fail("鏁版嵁瀛樺偍鏈嶅姟鍣ㄥ嚭閿?");
    }
}
