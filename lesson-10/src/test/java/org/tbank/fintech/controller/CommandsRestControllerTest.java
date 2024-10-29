package org.tbank.fintech.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.fintech.service.CommandsService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link CommandsRestController}
 */
@SpringBootTest(classes = CommandsRestController.class)
@AutoConfigureMockMvc
public class CommandsRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommandsService commandsService;

    @Test
    @DisplayName("Method: POST Endpoint: /api/v1/commands should return 200 because type is exist")
    public void invokeCommandTest() throws Exception {
        // Arrange
        String type = "type";
        when(commandsService.invokeCommandOfType(type)).thenReturn("ok");

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/commands?type={type}", type))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

    }

    @Test
    @DisplayName("Method: POST Endpoint: /api/v1/commands should return 400 because type is not provided")
    public void invokeCommandUnSuccessfulTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/commands"))
                .andExpect(status().isBadRequest());
    }

}
