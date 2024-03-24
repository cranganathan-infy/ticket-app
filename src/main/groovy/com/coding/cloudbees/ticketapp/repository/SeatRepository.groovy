package com.coding.cloudbees.ticketapp.repository

import com.coding.cloudbees.ticketapp.entity.Seat
import org.springframework.data.jpa.repository.JpaRepository

interface SeatRepository extends JpaRepository<Seat, Long> {
}
