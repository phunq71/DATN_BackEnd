package com.main;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DatnBeApplication {

	public static void main(String[] args) { SpringApplication.run(DatnBeApplication.class, args);
	}

	@Bean
	public CommandLineRunner check(MultipartProperties props) {
		return args -> System.out.println(
				"Max file size: " + props.getMaxFileSize() +
						", Max request size: " + props.getMaxRequestSize()
		);
	}
}
