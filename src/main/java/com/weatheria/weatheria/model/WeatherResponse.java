package com.weatheria.weatheria.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String city;
    private double latitude;
    private double longitude;
    private double temperature;
    private double windspeed;
    private int weathercode;
    private String weatherDescription;
    private String time;
}
