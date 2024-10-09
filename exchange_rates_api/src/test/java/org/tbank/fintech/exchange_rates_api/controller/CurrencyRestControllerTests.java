package org.tbank.fintech.exchange_rates_api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.fintech.exchange_rates_api.exception.BadRequestException;
import org.tbank.fintech.exchange_rates_api.exception.UnavailableServiceException;
import org.tbank.fintech.exchange_rates_api.model.response.Conversion;
import org.tbank.fintech.exchange_rates_api.model.response.CurrencyRate;
import org.tbank.fintech.exchange_rates_api.service.CurrencyService;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link CurrencyRestController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    @DisplayName("Method: get, endpoint:/api/v1/currencies/rates/RUB should return correct response, because RUB is correct code")
    public void findRatesOfCodeSuccessfulTest() throws Exception {
        // Arrange
        String currencyCode = "USD";
        Double currencyRate = 80.99;
        when(currencyService.findRatesOfCode(currencyCode)).thenReturn(new CurrencyRate(currencyCode, currencyRate));

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/currencies/rates/" + currencyCode))
                .andExpect(status().isOk())
                .andExpect(header().exists("Cache-Control"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"currency\":\"" + currencyCode + "\",\"rate\":"+ currencyRate +"}"));
    }

    @Test
    @DisplayName("Method: get, endpoint:/api/v1/currencies/rates/LOL should return 400 code, because LOL is incorrect code")
    public void findRatesOfCodeUnsuccessfulCodeIsIncorrectTest() throws Exception {
        // Arrange
        String incorrectCurrencyCode = "LOL";
        when(currencyService.findRatesOfCode(incorrectCurrencyCode)).thenThrow(new BadRequestException("Incorrect currency code: " + incorrectCurrencyCode));

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/currencies/rates/" + incorrectCurrencyCode))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"code\":400,\"message\":\"Incorrect currency code: " + incorrectCurrencyCode + "\"}"));
    }

    @Test
    @DisplayName("Method: get, endpoint:/api/v1/currencies/rates/    should return 400 code, because code is blank")
    public void findRatesOfCodeUnsuccessfulCodeIsBlankTest() throws Exception {
        // Arrange
        String incorrectCurrencyCode = "  ";

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/currencies/rates/" + incorrectCurrencyCode))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"code\":400,\"message\":\"currency code cannot be blank\"}"));
    }

    @Test
    @DisplayName("Method: post, endpoint:/api/v1/currencies/convert should return correct response, because request body is correct")
    public void convertCurrencySuccessfulTest() throws Exception {
        // Arrange
        String codeFrom = "USD";
        String codeTo = "RUB";
        Double amount = 100.5;
        when(currencyService.convertCurrency(codeFrom, codeTo, amount)).thenReturn(new Conversion(codeFrom, codeTo, 900.5));

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/currencies/convert")
                        .content("{\"fromCurrency\": \"USD\", \"toCurrency\": \"RUB\", \"amount\": 100.5}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"fromCurrency\":\"" + codeFrom + "\",\"toCurrency\":\"" + codeTo + "\",\"convertedAmount\":900.5}"));
    }

    @Test
    @DisplayName("Method: post, endpoint:/api/v1/currencies/convert should return 400, because request body is incorrect")
    public void convertCurrencyUnsuccessfulIncorrectRequestBodyTest() throws Exception {
        // Arrange
        String incorrectRequestBody = "{}";
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/currencies/convert")
                        .content(incorrectRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @MethodSource("argumentsForHandleExceptionTest")
    @DisplayName("Checking the handling of all exceptions")
    public void handleExceptionTest(int status, Exception exception) throws Exception {
        // Arrange
        when(currencyService.findRatesOfCode("RUB")).thenThrow(exception);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/currencies/rates/RUB"))
                .andExpect(status().is(status))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"code\":" + status + ",\"message\":\"" + exception.getMessage() + "\"}"));
    }


    static Stream<Arguments> argumentsForHandleExceptionTest() {
        return Stream.of(
                Arguments.arguments(400, new BadRequestException("bad request")),
                Arguments.arguments(404, new NoSuchElementException("element not found")),
                Arguments.arguments(500, new IllegalStateException("illegal state")),
                Arguments.arguments(500, new IllegalArgumentException("exception")),
                Arguments.arguments(503, new UnavailableServiceException("unavailable service"))
        );
    }
}
