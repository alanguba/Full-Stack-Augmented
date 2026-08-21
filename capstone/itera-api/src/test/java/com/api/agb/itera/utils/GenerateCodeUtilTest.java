package com.api.agb.itera.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenerateCodeUtilTest {

    @Test
    void generateCode_returnsFourDigitCode() {

        String code = GenerateCodeUtil.generateCode();

        assertNotNull(code);
        assertEquals(4, code.length());
    }

    @Test
    void generateCode_returnsNumericValue() {

        String code = GenerateCodeUtil.generateCode();

        assertTrue(code.matches("\\d{4}"));
    }

    @Test
    void generateCode_returnsValueBetween1000And9999() {

        String code = GenerateCodeUtil.generateCode();

        int value = Integer.parseInt(code);

        assertTrue(value >= 1000);
        assertTrue(value <= 9999);
    }

    @Test
    void generateCode_generatesValidCodesMultipleTimes() {

        for (int i = 0; i < 100; i++) {

            String code = GenerateCodeUtil.generateCode();

            assertNotNull(code);
            assertEquals(4, code.length());

            int value = Integer.parseInt(code);

            assertTrue(value >= 1000);
            assertTrue(value <= 9999);
        }
    }
}