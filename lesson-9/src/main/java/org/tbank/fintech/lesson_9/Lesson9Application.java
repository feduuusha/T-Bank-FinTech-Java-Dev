package org.tbank.fintech.lesson_9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Lesson9Application {

	public static void main(String[] args) {
		SpringApplication.run(Lesson9Application.class, args);
	}

}
