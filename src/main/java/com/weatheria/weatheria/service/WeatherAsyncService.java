package com.weatheria.weatheria.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.weatheria.weatheria.model.WeatherResponse;

@Service
public class WeatherAsyncService {

    private final WeatherCacheService weatherCacheService;

    public WeatherAsyncService(WeatherCacheService weatherCacheService) {
        this.weatherCacheService = weatherCacheService;
    }

    @Async("weatherTaskExecutor")
    public CompletableFuture<WeatherResponse> getWeatherForCityAsync(String city) {
        return CompletableFuture.completedFuture(
            weatherCacheService.getWeatherForCity(city)
        );
    }
}
