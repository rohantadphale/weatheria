package com.weatheria.weatheria.util;

import java.util.regex.Pattern;

public final class CityInputValidator {

    private static final Pattern CITY_PATTERN = Pattern.compile("^[\\p{L}\\p{M} .'-]{1,64}$");

    private CityInputValidator() {}

    public static String normalize(String city) {
        if (city == null) {
            throw new IllegalArgumentException("City is required");
        }
        String trimmed = city.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (!CITY_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("City contains invalid characters");
        }
        return trimmed.replaceAll("\\s+", " ");
    }

    public static boolean isValid(String city) {
        try {
            normalize(city);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
