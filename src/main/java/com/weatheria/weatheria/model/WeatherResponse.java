package com.weatheria.weatheria.model;

public class WeatherResponse {
    private String city;
    private double latitude;
    private double longitude;
    private double temperature;
    private double windspeed;
    private int weathercode;
    private String time;

    // getters & setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getWindspeed() { return windspeed; }
    public void setWindspeed(double windspeed) { this.windspeed = windspeed; }
    public int getWeathercode() { return weathercode; }
    public void setWeathercode(int weathercode) { this.weathercode = weathercode; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
