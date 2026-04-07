package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the fuel calculation logic used in Controller.
 * The calculation is extracted here to test without JavaFX dependencies.
 */
class ControllerTest {

    // Mirrors the calculation from Controller.handleCalculate()
    private double calculateFuel(double consumption, double distance) {
        return (consumption / 100) * distance;
    }

    private double calculateCost(double fuel, double price) {
        return fuel * price;
    }

    @Test
    void testFuelCalculationBasic() {
        double fuel = calculateFuel(8.0, 100.0);
        assertEquals(8.0, fuel, 0.001);
    }

    @Test
    void testFuelCalculationLongDistance() {
        double fuel = calculateFuel(6.5, 500.0);
        assertEquals(32.5, fuel, 0.001);
    }

    @Test
    void testCostCalculationBasic() {
        double cost = calculateCost(8.0, 1.50);
        assertEquals(12.0, cost, 0.001);
    }

    @Test
    void testCostCalculationExpensiveFuel() {
        double cost = calculateCost(32.5, 2.20);
        assertEquals(71.5, cost, 0.001);
    }

    @Test
    void testZeroDistance() {
        double fuel = calculateFuel(8.0, 0.0);
        assertEquals(0.0, fuel, 0.001);

        double cost = calculateCost(fuel, 1.50);
        assertEquals(0.0, cost, 0.001);
    }

    @Test
    void testZeroConsumption() {
        double fuel = calculateFuel(0.0, 100.0);
        assertEquals(0.0, fuel, 0.001);

        double cost = calculateCost(fuel, 1.50);
        assertEquals(0.0, cost, 0.001);
    }

    @Test
    void testZeroPrice() {
        double fuel = calculateFuel(8.0, 100.0);
        double cost = calculateCost(fuel, 0.0);
        assertEquals(0.0, cost, 0.001);
    }

    @Test
    void testHighConsumptionVehicle() {
        // e.g., heavy truck: 35 L/100km, 1000 km trip
        double fuel = calculateFuel(35.0, 1000.0);
        assertEquals(350.0, fuel, 0.001);

        double cost = calculateCost(fuel, 1.80);
        assertEquals(630.0, cost, 0.001);
    }

    @Test
    void testSmallDecimalValues() {
        double fuel = calculateFuel(4.3, 12.7);
        assertEquals(0.5461, fuel, 0.001);

        double cost = calculateCost(fuel, 1.65);
        assertEquals(0.9011, cost, 0.001);
    }

    @Test
    void testInvalidInputDetection() {
        // Mimics the NumberFormatException path in Controller
        assertThrows(NumberFormatException.class, () -> Double.parseDouble("abc"));
        assertThrows(NumberFormatException.class, () -> Double.parseDouble(""));
        assertThrows(NumberFormatException.class, () -> Double.parseDouble("12.3.4"));
    }

    @Test
    void testValidInputParsing() {
        assertEquals(100.0, Double.parseDouble("100.0"), 0.001);
        assertEquals(8.5, Double.parseDouble("8.5"), 0.001);
        assertEquals(1.5, Double.parseDouble("1.5"), 0.001);
    }

    @Test
    void testRTLLanguageDetection() {
        // Mirrors the RTL check in Controller.applyTextDirection()
        String[] rtlLanguages = {"fa", "ur", "ar", "he"};
        String[] ltrLanguages = {"en", "fr", "ja", "de"};

        for (String lang : rtlLanguages) {
            assertTrue(isRTL(lang), lang + " should be RTL");
        }

        for (String lang : ltrLanguages) {
            assertFalse(isRTL(lang), lang + " should be LTR");
        }
    }

    // Mirrors logic from Controller.applyTextDirection()
    private boolean isRTL(String lang) {
        return lang.equals("fa")
                || lang.equals("ur")
                || lang.equals("ar")
                || lang.equals("he");
    }
}
