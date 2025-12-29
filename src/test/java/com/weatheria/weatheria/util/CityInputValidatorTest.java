package com.weatheria.weatheria.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CityInputValidatorTest {

    @Test
    void normalizeTrimsAndCollapsesWhitespace() {
        assertEquals("New York", CityInputValidator.normalize("  New   York "));
    }

    @Test
    void invalidCharactersAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CityInputValidator.normalize("http://example.com"));
        assertFalse(CityInputValidator.isValid("http://example.com"));
    }

    @Test
    void allowsCommonPunctuation() {
        assertTrue(CityInputValidator.isValid("St. John's"));
    }
}
