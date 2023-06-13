package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SbdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbdemoApplication.class, args);
	}

}

@Component
class MyRunner implements CommandLineRunner
{

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello from MyRunner");
	}
}
