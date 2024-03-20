package com.coding.cloudbees.ticketapp.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.GenerationType
import javax.persistence.Id

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient

@Entity
@Canonical
@Table(name = "tickets")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id

    @JsonProperty('price_paid')
    Double pricePaid

    @JsonProperty('from')
    String fromLoc

    @JsonProperty('to')
    String toLoc

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    User user

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="train_id")
    @JsonIgnore
    Train train

    @Transient
    @JsonProperty('ticket_detail')
    TicketDetail ticketDetail
}
