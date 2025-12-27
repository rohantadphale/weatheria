package com.weatheria.weatheria.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weatheria.weatheria.model.WeatherResponse;
import com.weatheria.weatheria.service.WeatherService;
import com.weatheria.weatheria.util.ElapsedTimeUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Weather API", description = "Endpoints for weather and health checks")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    private final ElapsedTimeUtil elapsedTimeUtil = new ElapsedTimeUtil();
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
        long startNanos = System.nanoTime();
        String trimmedCity = city == null ? "" : city.trim();
        logger.info("Weather request received for city={}", trimmedCity);
        try {
            WeatherResponse resp = weatherService.getWeatherForCity(city);
            if (resp == null) {
                logger.info(
                    "Weather request completed for city={} status=not_found",
                    trimmedCity
                );
                return ResponseEntity.status(404).body(
                    Map.of("error", "City not found")
                );
            }
            logger.info(
                "Weather request completed for city={} status=ok elapsedMs={}",
                trimmedCity,
                elapsedTimeUtil.calculateElapsedTime(startNanos)
            );
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            logger.warn(
                "Weather request failed for city={} elapsedMs={} error={}",
                trimmedCity,
                elapsedTimeUtil.calculateElapsedTime(startNanos),
                e.getMessage()
            );
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/metrics/map-rendered")
    public ResponseEntity<?> mapRendered(@RequestBody Map<String, Object> payload) {
        String city = payload.get("city") instanceof String value
            ? value.trim()
            : "unknown";
        long durationMs = -1L;
        Object durationValue = payload.get("durationMs");
        if (durationValue instanceof Number number) {
            durationMs = number.longValue();
        }
        weatherService.logMapRenderLatency(city, durationMs);
        return ResponseEntity.ok(Map.of("status", "logged"));
    }
}
