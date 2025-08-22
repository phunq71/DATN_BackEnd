package com.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatnBeApplication {

	public static void main(String[] args) { SpringApplication.run(DatnBeApplication.class, args);
	}

}
