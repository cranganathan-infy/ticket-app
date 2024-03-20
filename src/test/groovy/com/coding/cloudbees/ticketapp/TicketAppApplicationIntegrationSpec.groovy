package com.coding.cloudbees.ticketapp

import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest(classes = TicketAppApplication, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = TicketAppApplication, initializers = ConfigFileApplicationContextInitializer)
abstract class TicketAppApplicationIntegrationSpec extends Specification {
    static {
        System.setProperty('environment', 'integration')
        System.setProperty('spring.main.allow-bean-definition-overriding', 'true')
    }
}
