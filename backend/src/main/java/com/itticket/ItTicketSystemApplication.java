package com.itticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ItTicketSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItTicketSystemApplication.class, args);
    }
}
