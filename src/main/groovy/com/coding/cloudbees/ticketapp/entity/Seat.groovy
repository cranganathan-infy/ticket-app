package com.coding.cloudbees.ticketapp.entity

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    int seatNumber
    boolean isReserved

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "train_section_id")
    TrainSection section

    void reserve() {
        isReserved = true
    }

    void cancelReservation() {
        isReserved = false
    }

}
