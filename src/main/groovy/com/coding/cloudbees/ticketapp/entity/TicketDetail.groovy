package com.coding.cloudbees.ticketapp.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class TicketDetail {
    @JsonProperty('train_number')
    String trainNumber
    @JsonProperty('train_name')
    String trainName
    String section
    @JsonProperty('seat_number')
    int seatNumber
}
