package org.ttl.sbdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SbdemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(SbdemoApplication.class, args);
   }
}

//Create a Bean that implements command line runner.
//Print out a message from the run method of your bean.
