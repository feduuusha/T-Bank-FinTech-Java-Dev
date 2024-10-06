package org.tbank.fintech.exchange_rates_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Exchange Rates Api",
                description = "API for access to exchange rates and currency conversion", version = "1.0.0",
                contact = @Contact(
                        name = "Voropaev Fyodor",
                        email = "voropaevfedor2005@mail.ru"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://github.com/feduuusha/T-Bank-FinTech-Java-Dev/blob/main/lessons-1-3/LICENSE"
                )
        )
)
public class OpenApiConfig {
}
