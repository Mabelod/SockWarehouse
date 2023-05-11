package ru.skypro.sockwarehouse;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class SockWarehouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SockWarehouseApplication.class, args);
	}

}
