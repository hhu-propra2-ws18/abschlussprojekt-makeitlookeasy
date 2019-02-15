package de.propra2.ausleiherino24;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Ausleiherino24Application {
	public static void main(String[] args) {
		SpringApplication.run(Ausleiherino24Application.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
