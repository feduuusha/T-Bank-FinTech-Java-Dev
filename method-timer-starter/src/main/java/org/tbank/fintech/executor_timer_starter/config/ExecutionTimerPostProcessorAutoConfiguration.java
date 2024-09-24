package org.tbank.fintech.executor_timer_starter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbank.fintech.executor_timer_starter.postprocessor.ExecutionTimerPostProcessor;

@Configuration
@ConditionalOnProperty(name = "execution-timer.enabled", havingValue = "true")
public class ExecutionTimerPostProcessorAutoConfiguration {

    @Bean
    public static ExecutionTimerPostProcessor executionTimerPostProcessor(@Value("${execution-timer.logger.level:info}") String level) {
        ExecutionTimerPostProcessor.LOGGING_LEVEL=level;
        return new ExecutionTimerPostProcessor();
    }
}
