package com.ramaccioni.api_clean_arch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class ApiCleanArchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCleanArchApplication.class, args);
	}

}
