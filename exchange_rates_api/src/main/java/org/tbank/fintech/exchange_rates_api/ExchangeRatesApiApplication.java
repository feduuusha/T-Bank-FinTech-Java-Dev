package org.tbank.fintech.exchange_rates_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ExchangeRatesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeRatesApiApplication.class, args);
	}

}
