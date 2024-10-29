package org.tbank.fintech.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tbank.fintech.command.Command;
import org.tbank.fintech.service.impl.CommandsServiceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandsServiceTest {
    @Mock(name = "command1")
    private Command<String> command1;
    @Mock(name = "command2")
    private Command<String> command2;
    @Mock
    private ExecutorService executorService;

    @Test
    @DisplayName("CommandsServiceImpl constructor should convert List<Command> to Map<String, Command> and then .invokeCommandOfType(command) should not throw any exception")
    public void commandsServiceImplConstructorTest() {
        // Arrange
        when(command1.getType()).thenReturn("command1");
        when(command2.getType()).thenReturn("command2");
        when(executorService.submit(command1)).thenReturn(CompletableFuture.completedFuture(null));
        when(executorService.submit(command2)).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        var service = new CommandsServiceImpl(List.of(command1, command2), executorService);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThatCode(() -> service.invokeCommandOfType(command1.getType())).doesNotThrowAnyException();
        softly.assertThatCode(() -> service.invokeCommandOfType(command2.getType())).doesNotThrowAnyException();

        softly.assertAll();
    }

    @Test
    @DisplayName("invokeCommandOfType that exist should work correctly")
    public void invokeCommandOfTypeSuccessfulTest() {
        // Arrange
        when(command1.getType()).thenReturn("command1");
        when(executorService.submit(command1)).thenReturn(CompletableFuture.completedFuture("String"));
        var service = new CommandsServiceImpl(List.of(command1, command2), executorService);

        // Act
        var result = service.invokeCommandOfType("command1");

        // Assert
        assertThat(result).isEqualTo("String");
    }

    @Test
    @DisplayName("invokeCommandOfType should throw UnsupportedOperationException if command is not exist")
    public void invokeCommandOfTypeUnSuccessfulTest() {
        // Arrange
        var service = new CommandsServiceImpl(List.of(), executorService);

        // Act
        // Assert
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> service.invokeCommandOfType("command1")).withMessage("Command of type: command1 is unsupported");
    }
}
