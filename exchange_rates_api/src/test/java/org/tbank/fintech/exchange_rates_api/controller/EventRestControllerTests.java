package org.tbank.fintech.exchange_rates_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.fintech.exchange_rates_api.ExchangeRatesApiApplication;
import org.tbank.fintech.exchange_rates_api.service.EventService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link EventRestController}
 */
@SpringBootTest(classes = {EventRestController.class, ExchangeRatesApiApplication.class})
@AutoConfigureMockMvc
public class EventRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    public void findPopularEventsFromPeriodSuccessfulTest() throws Exception {
        // Arrange
        when(eventService.findPopularEventsFromPeriod(eq(300), eq("RUB"), any(), any(), eq("kzn"), eq(1), eq(20))).thenReturn(CompletableFuture.supplyAsync(List::of));
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/events?budget=300&currency=RUB&dateFrom=15-09-2007&dateTo=16-09-2007&location=kzn&page=1&pageSize=20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }
}
