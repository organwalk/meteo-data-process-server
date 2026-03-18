package com.weather.common.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StationTableNameResolverTest {
    private final StationTableNameResolver resolver = new StationTableNameResolver();

    @Test
    void shouldResolveValidStationCode() {
        assertEquals("58367_meteo_data", resolver.resolve("58367"));
    }

    @Test
    void shouldRejectInvalidStationCode() {
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve("58-367"));
    }
}
