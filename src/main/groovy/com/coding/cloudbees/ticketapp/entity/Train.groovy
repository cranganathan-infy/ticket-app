package com.coding.cloudbees.ticketapp.entity


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Canonical
@Table(name = "trains")
@JsonIgnoreProperties(ignoreUnknown = true)
class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    @JsonProperty('train_number')
    String trainNumber
    @JsonProperty('train_name')
    String trainName
    String origin
    String destination

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    List<TrainSection> sections = []

}


/*class Train {
    @EmbeddedId
    @JsonProperty('train_section')
    TrainId trainId
    @JsonProperty('train_name')
    String trainName
    String origin
    String destination
    Integer seats
    List<Section> sections
    @JsonIgnore
    Integer totalSeats
}*/
