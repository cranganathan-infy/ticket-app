package com.coding.cloudbees.ticketapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@PropertySource(value = ['classpath:default.properties'])
class TicketAppApplication {
	static void main(String[] args) {
		SpringApplication.run(TicketAppApplication, args)
	}
}
