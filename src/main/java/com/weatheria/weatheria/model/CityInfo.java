package com.weatheria.weatheria.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityInfo {
    private String name;
    private double latitude;
    private double longitude;
    private String country;
}
