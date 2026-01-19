package com.weatheria.weatheria.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.weatheria.weatheria.model.WeatherResponse;

@Service
public class WeatherCacheService {

    private final WeatherService weatherService;

    public WeatherCacheService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Cacheable(
        cacheNames = "weather",
        key = "T(com.weatheria.weatheria.service.WeatherService).cityKey(#city)",
        unless = "#result == null"
    )
    public WeatherResponse getWeatherForCity(String city) {
        return weatherService.getWeatherForCityInternal(city);
    }
}
