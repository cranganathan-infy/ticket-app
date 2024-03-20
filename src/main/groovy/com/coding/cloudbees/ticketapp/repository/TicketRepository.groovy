package com.coding.cloudbees.ticketapp.repository

import com.coding.cloudbees.ticketapp.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findByUserId(Long userId)
    List<Ticket > findAllByTrainId(Long trainId)
}
