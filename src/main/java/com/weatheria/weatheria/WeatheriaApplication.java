package com.weatheria.weatheria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WeatheriaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatheriaApplication.class, args);
    }
}
