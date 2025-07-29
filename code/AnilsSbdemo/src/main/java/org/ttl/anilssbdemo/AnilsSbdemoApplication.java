package org.ttl.anilssbdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AnilsSbdemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(AnilsSbdemoApplication.class, args);
   }

}

//Create a bean that implements the CommandLineRunner interface
//Print out some message from the "run" method.