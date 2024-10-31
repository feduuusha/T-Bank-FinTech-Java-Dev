package org.tbank.fintech.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tbank.fintech.exception.UnavailableServiceException;
import org.tbank.fintech.command.Command;
import org.tbank.fintech.service.CommandsService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class CommandsServiceImpl implements CommandsService {

    private final ExecutorService commandExecutorService;
    private final Map<String, Command<?>> map;

    @Autowired
    public CommandsServiceImpl(List<Command<?>> commands, ExecutorService commandExecutorService) {
        this.map = commands.stream().collect(toMap(Command::getType, Function.identity()));
        this.commandExecutorService = commandExecutorService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invokeCommandOfType(String type) {
        if (!map.containsKey(type)) throw new UnsupportedOperationException("Command of type: " + type + " is unsupported");
        Command<T> command = (Command<T>) map.get(type);
        var future = commandExecutorService.submit(command);
        try {
            return future.get(15, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        } catch (TimeoutException e) {
            throw new UnavailableServiceException("Foreign service is unavailable");
        }
    }
}
