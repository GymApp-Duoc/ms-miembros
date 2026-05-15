package com.gymapp.ms_miembros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsMiembrosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsMiembrosApplication.class, args);
	}

}