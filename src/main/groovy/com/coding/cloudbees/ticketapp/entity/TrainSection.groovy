package com.coding.cloudbees.ticketapp.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class TrainSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    String sectionName

    @OneToMany(mappedBy = "section", cascade = CascadeType.MERGE)
    List<Seat> seats

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id")
    Train train
}
