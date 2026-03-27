package com.returdev.nexflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NexflowApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexflowApiApplication.class, args);
	}

}
