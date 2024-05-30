package com.example.cbrcurrencyparse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CbrcurrencyparseApplication {

	public static void main(String[] args) 
	{
		SpringApplication.run(CbrcurrencyparseApplication.class, args);
	}

}
