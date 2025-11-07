package com.weatheria.weatheria.service;

import com.weatheria.weatheria.model.CityInfo;
import com.weatheria.weatheria.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";

    @Autowired
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CityInfo geocodeCity(String city) {
        URI uri = UriComponentsBuilder.fromUriString(GEOCODING_URL)
                .queryParam("name", city)
                .build()
                .toUri();

        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(uri, Map.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return null;
            }
            Map body = resp.getBody();
            Object resultsObj = body.get("results");
            if (resultsObj instanceof java.util.List && !((java.util.List) resultsObj).isEmpty()) {
                Map first = (Map) ((java.util.List) resultsObj).get(0);
                CityInfo info = new CityInfo();
                info.setName((String) first.getOrDefault("name", city));
                info.setCountry((String) first.getOrDefault("country", ""));
                Object lat = first.get("latitude");
                Object lon = first.get("longitude");
                if (lat instanceof Number) info.setLatitude(((Number) lat).doubleValue());
                if (lon instanceof Number) info.setLongitude(((Number) lon).doubleValue());
                return info;
            } else {
                return null;
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Geocoding API call failed", e);
        }
    }

    public WeatherResponse getWeatherForCity(String city) {
        CityInfo cityInfo = geocodeCity(city);
        if (cityInfo == null) return null;

        URI uri = UriComponentsBuilder.fromUriString(FORECAST_URL)
                .queryParam("latitude", cityInfo.getLatitude())
                .queryParam("longitude", cityInfo.getLongitude())
                .queryParam("current_weather", "true")
                .build()
                .toUri();

        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(uri, Map.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new RuntimeException("Weather API returned non-200");
            }
            Map body = resp.getBody();
            Object cw = body.get("current_weather");
            if (!(cw instanceof Map)) {
                throw new RuntimeException("No current_weather in response");
            }
            Map currentWeather = (Map) cw;
            WeatherResponse out = new WeatherResponse();
            out.setCity(cityInfo.getName());
            out.setLatitude(cityInfo.getLatitude());
            out.setLongitude(cityInfo.getLongitude());
            Object temp = currentWeather.get("temperature");
            Object wind = currentWeather.get("windspeed");
            Object code = currentWeather.get("weathercode");
            Object time = currentWeather.get("time");
            if (temp instanceof Number) out.setTemperature(((Number) temp).doubleValue());
            if (wind instanceof Number) out.setWindspeed(((Number) wind).doubleValue());
            if (code instanceof Number) {
                int weatherCode = ((Number) code).intValue();
                out.setWeathercode(weatherCode);
                out.setWeatherDescription(weatherCodeToDescription(weatherCode));
            }
            out.setTime(time != null ? time.toString() : null);
            return out;
        } catch (RestClientException e) {
            throw new RuntimeException("Forecast API call failed", e);
        }
    }

    public String weatherCodeToDescription(int weatherCode) {
        return switch (weatherCode) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45 -> "Fog";
            case 48 -> "Depositing rime fog";
            case 51 -> "Drizzle: Light intensity";
            case 53 -> "Drizzle: Moderate intensity";
            case 55 -> "Drizzle: Dense intensity";
            case 61 -> "Rain: Slight intensity";
            case 63 -> "Rain: Moderate intensity";
            case 65 -> "Rain: Heavy intensity";
            case 71 -> "Snow fall: Slight intensity";
            case 73 -> "Snow fall: Moderate intensity";
            case 75 -> "Snow fall: Heavy intensity";
            case 80 -> "Rain showers: Slight intensity";
            case 81 -> "Rain showers: Moderate intensity";
            case 82 -> "Rain showers: Violent intensity";
            case 95 -> "Thunderstorm: Slight or moderate";
            default -> "Unknown weather code";
        };
    }
}
