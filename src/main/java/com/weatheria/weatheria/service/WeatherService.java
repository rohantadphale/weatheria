package com.weatheria.weatheria.service;

import java.net.URI;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.weatheria.weatheria.model.CityInfo;
import com.weatheria.weatheria.model.WeatherResponse;
import com.weatheria.weatheria.util.CityInputValidator;
import com.weatheria.weatheria.util.ElapsedTimeUtil;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final ElapsedTimeUtil elapsedTimeUtil = new ElapsedTimeUtil();
    private final RestTemplate restTemplate;
    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CityInfo geocodeCity(String city) {
        long startNanos = System.nanoTime();
        String normalizedCity = CityInputValidator.normalize(city);
        URI uri = UriComponentsBuilder.fromUriString(GEOCODING_URL)
            .queryParam("name", city)
            .build()
            .toUri();

        try {
            RequestEntity<Void> request = RequestEntity.get(uri).build();
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = resp.getBody();
            if (!resp.getStatusCode().is2xxSuccessful() || body == null) {
                return null;
            }
            Object resultsObj = body.get("results");
            if (
                resultsObj instanceof java.util.List<?> list && !list.isEmpty()
            ) {
                Object firstObj = list.get(0);
                if (!(firstObj instanceof Map<?, ?> first)) {
                    return null;
                }
                CityInfo info = new CityInfo();
                String name = getString(first, "name");
                info.setName(name != null ? name : normalizedCity);
                String country = getString(first, "country");
                info.setCountry(country != null ? country : "");
                Double lat = getDouble(first, "latitude");
                Double lon = getDouble(first, "longitude");
                if (lat != null) info.setLatitude(lat);
                if (lon != null) info.setLongitude(lon);
                logger.info(
                    "Geocoding completed for city={} elapsedMs={}",
                    info.getName(),
                    elapsedTimeUtil.calculateElapsedTime(startNanos)
                );
                return info;
            } else {
                logger.info(
                    "Geocoding completed for city={} status=not_found elapsedMs={}",
                    normalizedCity,
                    elapsedTimeUtil.calculateElapsedTime(startNanos)
                );
                return null;
            }
        } catch (RestClientException e) {
            logger.warn(
                "Geocoding failed for city={} elapsedMs={} error={}",
                normalizedCity,
                elapsedTimeUtil.calculateElapsedTime(startNanos),
                e.getMessage()
            );
            throw new RuntimeException("Geocoding API call failed", e);
        }
    }

    public WeatherResponse getWeatherForCityInternal(String city) {
        long startNanos = System.nanoTime();
        CityInfo cityInfo = geocodeCity(city);
        if (cityInfo == null) return null;

        URI uri = UriComponentsBuilder.fromUriString(FORECAST_URL)
            .queryParam("latitude", cityInfo.getLatitude())
            .queryParam("longitude", cityInfo.getLongitude())
            .queryParam("current_weather", "true")
            .build()
            .toUri();

        try {
            RequestEntity<Void> request = RequestEntity.get(uri).build();
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = resp.getBody();
            if (!resp.getStatusCode().is2xxSuccessful() || body == null) {
                throw new RuntimeException("Weather API returned non-200");
            }
            Object cw = body.get("current_weather");
            if (!(cw instanceof Map<?, ?> currentWeather)) {
                throw new RuntimeException("No current_weather in response");
            }
            WeatherResponse out = new WeatherResponse();
            out.setCity(cityInfo.getName());
            out.setLatitude(cityInfo.getLatitude());
            out.setLongitude(cityInfo.getLongitude());
            Double temp = getDouble(currentWeather, "temperature");
            Double wind = getDouble(currentWeather, "windspeed");
            Integer code = getInt(currentWeather, "weathercode");
            Object time = currentWeather.get("time");
            if (temp != null) out.setTemperature(temp);
            if (wind != null) out.setWindspeed(wind);
            if (code != null) {
                int weatherCode = code;
                out.setWeathercode(weatherCode);
                out.setWeatherDescription(
                    weatherCodeToDescription(weatherCode)
                );
            }
            out.setTime(time != null ? time.toString() : null);
            logger.info(
                "Weather lookup completed for city={} elapsedMs={}",
                cityInfo.getName(),
                elapsedTimeUtil.calculateElapsedTime(startNanos)
            );
            return out;
        } catch (RestClientException e) {
            logger.warn(
                "Weather lookup failed for city={} elapsedMs={} error={}",
                city,
                elapsedTimeUtil.calculateElapsedTime(startNanos),
                e.getMessage()
            );
            throw new RuntimeException("Forecast API call failed", e);
        }
    }

    public void logMapRenderLatency(String city, long durationMs) {
        if (durationMs < 0) {
            logger.info("Map render latency reported for city={} durationMs=unknown", city);
            return;
        }
        logger.info("Map render latency reported for city={} durationMs={}", city, durationMs);
    }

    private static final Map<Integer, String> WEATHER_DESCRIPTIONS = new java.util.HashMap<>();

    static {
        WEATHER_DESCRIPTIONS.put(0, "Clear sky");
        WEATHER_DESCRIPTIONS.put(1, "Mainly clear");
        WEATHER_DESCRIPTIONS.put(2, "Partly cloudy");
        WEATHER_DESCRIPTIONS.put(3, "Overcast");
        WEATHER_DESCRIPTIONS.put(45, "Fog");
        WEATHER_DESCRIPTIONS.put(48, "Depositing rime fog");
        WEATHER_DESCRIPTIONS.put(51, "Drizzle: Light intensity");
        WEATHER_DESCRIPTIONS.put(53, "Drizzle: Moderate intensity");
        WEATHER_DESCRIPTIONS.put(55, "Drizzle: Dense intensity");
        WEATHER_DESCRIPTIONS.put(61, "Rain: Slight intensity");
        WEATHER_DESCRIPTIONS.put(63, "Rain: Moderate intensity");
        WEATHER_DESCRIPTIONS.put(65, "Rain: Heavy intensity");
        WEATHER_DESCRIPTIONS.put(71, "Snow fall: Slight intensity");
        WEATHER_DESCRIPTIONS.put(73, "Snow fall: Moderate intensity");
        WEATHER_DESCRIPTIONS.put(75, "Snow fall: Heavy intensity");
        WEATHER_DESCRIPTIONS.put(80, "Rain showers: Slight intensity");
        WEATHER_DESCRIPTIONS.put(81, "Rain showers: Moderate intensity");
        WEATHER_DESCRIPTIONS.put(82, "Rain showers: Violent intensity");
        WEATHER_DESCRIPTIONS.put(95, "Thunderstorm: Slight or moderate");
    }

    public static String cityKey(String city) {
        if (city == null) return "";
        return city.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static String getString(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : null;
    }

    private static Double getDouble(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }

    private static Integer getInt(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    public String weatherCodeToDescription(int weatherCode) {
        return WEATHER_DESCRIPTIONS.getOrDefault(weatherCode,"Unknown weather code");
    }
}
