package com.example;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerTest {

    private Controller controller;
    private MockedStatic<LocalizationService> localizationMock;

    private Label lblDistance, lblConsumption, lblPrice, lblResult;
    private TextField txtDistance, txtConsumption, txtPrice;
    private Button btnCalculate;
    private VBox root;
    private HBox langButtons;
    private RecordDao mockRecordDao;

    private Map<String, String> testStrings;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // toolkit already initialized
        }
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setUp() throws Exception {
        testStrings = new HashMap<>();
        testStrings.put("distance.label", "Distance (km)");
        testStrings.put("consumption.label", "Fuel Consumption (L/100 km)");
        testStrings.put("price.label", "Fuel Price (per liter)");
        testStrings.put("calculate.button", "Calculate Trip Cost");
        testStrings.put("distance.prompt", "Enter distance");
        testStrings.put("consumption.prompt", "Enter consumption");
        testStrings.put("price.prompt", "Enter price");
        testStrings.put("result.label", "Total fuel needed: {0} L | Total cost: {1}");
        testStrings.put("invalid.input", "Invalid input");

        localizationMock = mockStatic(LocalizationService.class);
        localizationMock.when(() -> LocalizationService.loadStrings(any(Locale.class)))
                .thenReturn(testStrings);

        lblDistance = new Label();
        lblConsumption = new Label();
        lblPrice = new Label();
        lblResult = new Label();
        txtDistance = new TextField();
        txtConsumption = new TextField();
        txtPrice = new TextField();
        btnCalculate = new Button();
        root = new VBox();
        langButtons = new HBox();
        mockRecordDao = mock(RecordDao.class);

        controller = new Controller();
        setField("lblDistance", lblDistance);
        setField("lblConsumption", lblConsumption);
        setField("lblPrice", lblPrice);
        setField("lblResult", lblResult);
        setField("txtDistance", txtDistance);
        setField("txtConsumption", txtConsumption);
        setField("txtPrice", txtPrice);
        setField("btnCalculate", btnCalculate);
        setField("root", root);
        setField("langButtons", langButtons);
        setField("rd", mockRecordDao);
    }

    @AfterEach
    void tearDown() {
        localizationMock.close();
    }

    private void setField(String name, Object value) throws Exception {
        Field field = Controller.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private Object getField(String name) throws Exception {
        Field field = Controller.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(controller);
    }

    private void invokePrivate(String name, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = Controller.class.getDeclaredMethod(name, paramTypes);
        method.setAccessible(true);
        method.invoke(controller, args);
    }

    private void waitForFxThread() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    // --- initialize ---

    @Test
    void testInitializeSetsEnglishLabels() throws Exception {
        invokePrivate("initialize", new Class<?>[]{});
        waitForFxThread();

        assertEquals("Distance (km)", lblDistance.getText());
        assertEquals("Fuel Consumption (L/100 km)", lblConsumption.getText());
        assertEquals("Fuel Price (per liter)", lblPrice.getText());
        assertEquals("Calculate Trip Cost", btnCalculate.getText());
        assertEquals("", lblResult.getText());
    }

    @Test
    void testInitializeSetsCurrentLocale() throws Exception {
        invokePrivate("initialize", new Class<?>[]{});

        Locale locale = (Locale) getField("currentLocale");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void testInitializeSetsPromptTexts() throws Exception {
        invokePrivate("initialize", new Class<?>[]{});

        assertEquals("Enter distance", txtDistance.getPromptText());
        assertEquals("Enter consumption", txtConsumption.getPromptText());
        assertEquals("Enter price", txtPrice.getPromptText());
    }

    // --- setLanguage ---

    @Test
    void testSetLanguageUpdatesLabels() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));
        waitForFxThread();

        assertEquals("Distance (km)", lblDistance.getText());
        assertEquals("Fuel Consumption (L/100 km)", lblConsumption.getText());
        assertEquals("Fuel Price (per liter)", lblPrice.getText());
        assertEquals("Calculate Trip Cost", btnCalculate.getText());
        assertEquals("", lblResult.getText());
    }

    @Test
    void testSetLanguageSetsPromptTexts() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        assertEquals("Enter distance", txtDistance.getPromptText());
        assertEquals("Enter consumption", txtConsumption.getPromptText());
        assertEquals("Enter price", txtPrice.getPromptText());
    }

    @Test
    void testSetLanguageCallsLoadStrings() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("fr", "FR"));

        localizationMock.verify(() -> LocalizationService.loadStrings(argThat(locale ->
                locale.getLanguage().equals("fr") && locale.getCountry().equals("FR")
        )));
    }

    @Test
    void testSetLanguageUsesDefaultsWhenKeysNotFound() throws Exception {
        testStrings.clear();
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        assertEquals("Distance (km)", lblDistance.getText());
        assertEquals("Fuel Consumption (L/100 km)", lblConsumption.getText());
        assertEquals("Fuel Price (per liter)", lblPrice.getText());
        assertEquals("Calculate Trip Cost", btnCalculate.getText());
        assertEquals("Enter distance", txtDistance.getPromptText());
        assertEquals("Enter consumption", txtConsumption.getPromptText());
        assertEquals("Enter price", txtPrice.getPromptText());
    }

    @Test
    void testSetLanguageUpdatesCurrentLocale() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("ja", "JP"));

        Locale locale = (Locale) getField("currentLocale");
        assertEquals("ja", locale.getLanguage());
        assertEquals("JP", locale.getCountry());
    }

    // --- applyTextDirection ---

    @Test
    void testApplyTextDirectionLTRForEnglish() throws Exception {
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("en", "US"));
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, langButtons.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionRTLForPersian() throws Exception {
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("fa", "IR"));
        waitForFxThread();

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, langButtons.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionRTLForArabic() throws Exception {
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("ar", "SA"));
        waitForFxThread();

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionRTLForUrdu() throws Exception {
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("ur", "PK"));
        waitForFxThread();

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionRTLForHebrew() throws Exception {
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("he", "IL"));
        waitForFxThread();

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionLTRForFrench() throws Exception {
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("fr", "FR"));
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionLTRForJapanese() throws Exception {
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("ja", "JP"));
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionWithNullRoot() throws Exception {
        setField("root", null);
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("en", "US"));
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, langButtons.getNodeOrientation());
    }

    @Test
    void testApplyTextDirectionRTLWithNullRoot() throws Exception {
        setField("root", null);
        invokePrivate("applyTextDirection", new Class<?>[]{Locale.class}, new Locale("fa", "IR"));
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, langButtons.getNodeOrientation());
    }

    // --- handleCalculate ---

    @Test
    void testHandleCalculateValidInput() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("100");
        txtConsumption.setText("8.0");
        txtPrice.setText("1.50");

        invokePrivate("handleCalculate", new Class<?>[]{});

        // fuel = (8.0/100)*100 = 8.0, cost = 8.0*1.5 = 12.0
        String result = lblResult.getText();
        assertTrue(result.contains(String.format("%.2f", 8.0)), "Result should contain fuel amount: " + result);
        assertTrue(result.contains(String.format("%.2f", 12.0)), "Result should contain cost: " + result);
    }

    @Test
    void testHandleCalculateSavesRecord() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("200");
        txtConsumption.setText("10.0");
        txtPrice.setText("2.00");

        invokePrivate("handleCalculate", new Class<?>[]{});

        verify(mockRecordDao).saveRecord(200.0, 10.0, 2.0, 20.0, 40.0, "en");
    }

    @Test
    void testHandleCalculateInvalidDistanceInput() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("abc");
        txtConsumption.setText("8.0");
        txtPrice.setText("1.50");

        invokePrivate("handleCalculate", new Class<?>[]{});

        assertEquals("Invalid input", lblResult.getText());
        verifyNoInteractions(mockRecordDao);
    }

    @Test
    void testHandleCalculateInvalidConsumptionInput() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("100");
        txtConsumption.setText("xyz");
        txtPrice.setText("1.50");

        invokePrivate("handleCalculate", new Class<?>[]{});

        assertEquals("Invalid input", lblResult.getText());
    }

    @Test
    void testHandleCalculateInvalidPriceInput() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("100");
        txtConsumption.setText("8.0");
        txtPrice.setText("not-a-number");

        invokePrivate("handleCalculate", new Class<?>[]{});

        assertEquals("Invalid input", lblResult.getText());
    }

    @Test
    void testHandleCalculateEmptyInput() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("");
        txtConsumption.setText("8.0");
        txtPrice.setText("1.50");

        invokePrivate("handleCalculate", new Class<?>[]{});

        assertEquals("Invalid input", lblResult.getText());
    }

    @Test
    void testHandleCalculateWithZeroValues() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("0");
        txtConsumption.setText("0");
        txtPrice.setText("0");

        invokePrivate("handleCalculate", new Class<?>[]{});

        assertTrue(lblResult.getText().contains(String.format("%.2f", 0.0)));
        verify(mockRecordDao).saveRecord(0.0, 0.0, 0.0, 0.0, 0.0, "en");
    }

    @Test
    void testHandleCalculateLargeValues() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("10000");
        txtConsumption.setText("25.0");
        txtPrice.setText("3.50");

        invokePrivate("handleCalculate", new Class<?>[]{});

        // fuel = (25/100)*10000 = 2500, cost = 2500*3.5 = 8750
        verify(mockRecordDao).saveRecord(10000.0, 25.0, 3.5, 2500.0, 8750.0, "en");
    }

    @Test
    void testHandleCalculateWithFrenchLocale() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("fr", "FR"));

        txtDistance.setText("150");
        txtConsumption.setText("7.5");
        txtPrice.setText("1.80");

        invokePrivate("handleCalculate", new Class<?>[]{});

        // fuel = (7.5/100)*150 = 11.25, cost = 11.25*1.8 = 20.25
        verify(mockRecordDao).saveRecord(150.0, 7.5, 1.80, 11.25, 20.25, "fr");
    }

    @Test
    void testHandleCalculateWithPersianLocale() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("fa", "IR"));

        txtDistance.setText("500");
        txtConsumption.setText("12.0");
        txtPrice.setText("0.50");

        invokePrivate("handleCalculate", new Class<?>[]{});

        // fuel = (12/100)*500 = 60, cost = 60*0.5 = 30
        verify(mockRecordDao).saveRecord(500.0, 12.0, 0.5, 60.0, 30.0, "fa");
    }

    @Test
    void testHandleCalculateUsesDefaultResultPattern() throws Exception {
        testStrings.remove("result.label");
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("100");
        txtConsumption.setText("10");
        txtPrice.setText("2");

        invokePrivate("handleCalculate", new Class<?>[]{});

        String result = lblResult.getText();
        assertTrue(result.contains(String.format("%.2f", 10.0)));
        assertTrue(result.contains(String.format("%.2f", 20.0)));
    }

    @Test
    void testHandleCalculateUsesDefaultInvalidInputMessage() throws Exception {
        testStrings.remove("invalid.input");
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("invalid");
        txtConsumption.setText("8.0");
        txtPrice.setText("1.50");

        invokePrivate("handleCalculate", new Class<?>[]{});

        assertEquals("Invalid input", lblResult.getText());
    }

    @Test
    void testHandleCalculateSmallDecimalValues() throws Exception {
        invokePrivate("setLanguage", new Class<?>[]{Locale.class}, new Locale("en", "US"));

        txtDistance.setText("12.7");
        txtConsumption.setText("4.3");
        txtPrice.setText("1.65");

        invokePrivate("handleCalculate", new Class<?>[]{});

        // fuel = (4.3/100)*12.7 = 0.5461, cost = 0.5461*1.65 = 0.901065
        String result = lblResult.getText();
        assertTrue(result.contains(String.format("%.2f", 0.55)), "Result should contain fuel: " + result);
        assertTrue(result.contains(String.format("%.2f", 0.90)), "Result should contain cost: " + result);
    }

    // --- Language setter methods ---

    @Test
    void testSetEnglish() throws Exception {
        invokePrivate("setEnglish", new Class<?>[]{});

        Locale locale = (Locale) getField("currentLocale");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void testSetFrench() throws Exception {
        invokePrivate("setFrench", new Class<?>[]{});

        Locale locale = (Locale) getField("currentLocale");
        assertEquals("fr", locale.getLanguage());
        assertEquals("FR", locale.getCountry());
    }

    @Test
    void testSetJapanese() throws Exception {
        invokePrivate("setJapanese", new Class<?>[]{});

        Locale locale = (Locale) getField("currentLocale");
        assertEquals("ja", locale.getLanguage());
        assertEquals("JP", locale.getCountry());
    }

    @Test
    void testSetPersian() throws Exception {
        invokePrivate("setPersian", new Class<?>[]{});

        Locale locale = (Locale) getField("currentLocale");
        assertEquals("fa", locale.getLanguage());
        assertEquals("IR", locale.getCountry());
    }

    @Test
    void testSetPersianSetsRTL() throws Exception {
        invokePrivate("setPersian", new Class<?>[]{});
        waitForFxThread();

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
    }

    @Test
    void testSetEnglishSetsLTR() throws Exception {
        invokePrivate("setEnglish", new Class<?>[]{});
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

    @Test
    void testSetFrenchSetsLTR() throws Exception {
        invokePrivate("setFrench", new Class<?>[]{});
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

    @Test
    void testSetJapaneseSetsLTR() throws Exception {
        invokePrivate("setJapanese", new Class<?>[]{});
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

    @Test
    void testSetLanguageLangButtonsAlwaysLTR() throws Exception {
        invokePrivate("setPersian", new Class<?>[]{});
        waitForFxThread();

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, langButtons.getNodeOrientation());
    }
}
