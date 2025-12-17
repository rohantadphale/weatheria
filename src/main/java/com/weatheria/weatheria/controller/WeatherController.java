package com.weatheria.weatheria.controller;

import com.weatheria.weatheria.model.WeatherResponse;
import com.weatheria.weatheria.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Weather API", description = "Endpoints for weather and health checks")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
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
    public ResponseEntity<?> getWeather(@RequestParam("city") String city) {
        try {
            WeatherResponse resp = weatherService.getWeatherForCity(city);
            if (resp == null) {
                return ResponseEntity.status(404).body(Map.of("error", "City not found"));
            }
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        }
    }
}
