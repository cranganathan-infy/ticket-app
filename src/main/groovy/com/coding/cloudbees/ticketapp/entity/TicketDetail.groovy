package com.coding.cloudbees.ticketapp.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class TicketDetail{
    @JsonProperty('train_number')
    String trainNumber
    @JsonProperty('train_name')
    String trainName
    String section
    @JsonProperty('seat_number')
    int seatNumber
}