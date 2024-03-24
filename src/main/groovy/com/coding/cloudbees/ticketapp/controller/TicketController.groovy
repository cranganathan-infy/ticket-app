package com.coding.cloudbees.ticketapp.controller

import com.coding.cloudbees.ticketapp.entity.Ticket
import com.coding.cloudbees.ticketapp.service.TicketService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

@Slf4j
@RestController
@RequestMapping('ticket')
class TicketController {

    @Autowired
    TicketService ticketService

    @GetMapping("{ticketId}")
    ResponseEntity fetchTicketById(@PathVariable @Positive Long ticketId) {
        new ResponseEntity<Ticket>(ticketService.fetchTicketInfo(ticketId), HttpStatus.OK)
    }

    @GetMapping("/user/{email}")
    ResponseEntity fetchTicketByUserId(@PathVariable @Email @NotEmpty String email) {
        new ResponseEntity<Ticket>(ticketService.fetchTicketByUserEmail(email), HttpStatus.OK)
    }

    @PostMapping
    ResponseEntity allocateSeat(@RequestBody Ticket ticket) {
        new ResponseEntity<Ticket>(ticketService.allocateSeat(ticket), HttpStatus.CREATED)
    }

    @GetMapping('/seats/{trainNumber}')
    ResponseEntity fetchUsersBySection(@PathVariable @NotEmpty String trainNumber, @RequestParam String section) {
        new ResponseEntity(ticketService.fetchUsersBySection(trainNumber, section), HttpStatus.OK)
    }

    @PutMapping
    ResponseEntity modifySeat(@RequestBody Ticket ticket) {
        new ResponseEntity<Ticket>(ticketService.modifySeat(ticket), HttpStatus.OK)
    }

    @DeleteMapping("{ticketId}")
    ResponseEntity cancelTicket(@PathVariable @Positive Long ticketId) {
        new ResponseEntity(ticketService.cancelReservationByTicketId(ticketId), HttpStatus.NO_CONTENT)
    }
}
