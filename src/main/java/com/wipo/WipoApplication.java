package com.wipo;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class WipoApplication {

	@PostConstruct
	void started(){
	    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	
	public static void main(String[] args) {
		SpringApplication.run(WipoApplication.class, args);
	}

}
