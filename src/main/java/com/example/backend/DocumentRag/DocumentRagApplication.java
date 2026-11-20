package com.example.backend.DocumentRag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocumentRagApplication {

	public static void main(String[] args) {
		System.out.println("Java Home: "
				+ System.getProperty("java.home"));

		System.out.println("Java Version: "
				+ System.getProperty("java.version"));
		SpringApplication.run(DocumentRagApplication.class, args);
	}

}
