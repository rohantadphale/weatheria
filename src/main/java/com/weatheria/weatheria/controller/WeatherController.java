package com.weatheria.weatheria.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weatheria.weatheria.service.WeatherService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Weather API", description = "Endpoints for weather and health checks")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService service) {
        this.weatherService = service;
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of("message", "pong"));
    }

    @GetMapping("/weather")
    public CompletableFuture<ResponseEntity<?>> getWeather(@RequestParam("city") String city) {
        return weatherService
            .getWeatherForCityAsync(city)
            .thenApply(resp -> {
                if (resp == null) {
                    return ResponseEntity.status(404).body(
                        Map.of("error", "City not found")
                    );
                }
                return ResponseEntity.ok(resp);
            })
            .exceptionally(ex -> {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                return ResponseEntity.status(502).body(
                    Map.of("error", cause.getMessage())
                );
            });
    }
}
