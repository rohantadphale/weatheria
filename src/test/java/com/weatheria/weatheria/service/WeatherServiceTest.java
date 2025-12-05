package com.weatheria.weatheria.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void testWeatherCodeToDescription() {
        assertEquals("Clear sky", weatherService.weatherCodeToDescription(0));
        assertEquals("Mainly clear", weatherService.weatherCodeToDescription(1));
        assertEquals("Partly cloudy", weatherService.weatherCodeToDescription(2));
        assertEquals("Overcast", weatherService.weatherCodeToDescription(3));
        assertEquals("Fog", weatherService.weatherCodeToDescription(45));
        assertEquals("Depositing rime fog", weatherService.weatherCodeToDescription(48));
        assertEquals("Drizzle: Light intensity", weatherService.weatherCodeToDescription(51));
        assertEquals("Drizzle: Moderate intensity", weatherService.weatherCodeToDescription(53));
        assertEquals("Drizzle: Dense intensity", weatherService.weatherCodeToDescription(55));
        assertEquals("Rain: Slight intensity", weatherService.weatherCodeToDescription(61));
        assertEquals("Rain: Moderate intensity", weatherService.weatherCodeToDescription(63));
        assertEquals("Rain: Heavy intensity", weatherService.weatherCodeToDescription(65));
        assertEquals("Snow fall: Slight intensity", weatherService.weatherCodeToDescription(71));
        assertEquals("Snow fall: Moderate intensity", weatherService.weatherCodeToDescription(73));
        assertEquals("Snow fall: Heavy intensity", weatherService.weatherCodeToDescription(75));
        assertEquals("Rain showers: Slight intensity", weatherService.weatherCodeToDescription(80));
        assertEquals("Rain showers: Moderate intensity", weatherService.weatherCodeToDescription(81));
        assertEquals("Rain showers: Violent intensity", weatherService.weatherCodeToDescription(82));
        assertEquals("Thunderstorm: Slight or moderate", weatherService.weatherCodeToDescription(95));
        assertEquals("Unknown weather code", weatherService.weatherCodeToDescription(100));
    }
}
