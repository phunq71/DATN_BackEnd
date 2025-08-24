package com.main;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


@SpringBootApplication
@EnableScheduling
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

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		System.out.println("Application running in timezone: " + TimeZone.getDefault().getID());
	}

}
