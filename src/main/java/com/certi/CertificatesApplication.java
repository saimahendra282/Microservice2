package com.certi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
@EnableMongoRepositories(basePackages = "com.certi")
@SpringBootApplication
public class CertificatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertificatesApplication.class, args);
	}

}
