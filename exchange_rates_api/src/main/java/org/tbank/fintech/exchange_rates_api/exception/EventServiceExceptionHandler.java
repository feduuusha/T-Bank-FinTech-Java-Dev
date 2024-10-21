package org.tbank.fintech.exchange_rates_api.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.tbank.fintech.exchange_rates_api.model.Event;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

@Component("eventServiceExceptionHandler")
public class EventServiceExceptionHandler implements Function<Throwable, List<Event>> {
    @Override
    public List<Event> apply(Throwable exception) {
        if (exception instanceof CompletionException) {
            exception = exception.getCause();
        }

        if (exception instanceof HttpClientErrorException.BadRequest e) {
            throw new BadRequestException(e.getMessage());
        } else if (exception instanceof BadRequestException e) {
            throw e;
        } else if (exception instanceof HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException(e.getMessage());
        } else if (exception instanceof NoSuchElementException e) {
            throw e;
        } else if (exception instanceof HttpServerErrorException e) {
            throw new UnavailableServiceException(e.getMessage());
        } else {
            throw new IllegalStateException(exception.getMessage());
        }
    }
}
