package com.coding.cloudbees.ticketapp.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    int seatNumber
    boolean reserved

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "train_section_id")
    TrainSection section

    void reserve() {
        reserved = true
    }

    void cancelReservation() {
        reserved = false
    }

}
