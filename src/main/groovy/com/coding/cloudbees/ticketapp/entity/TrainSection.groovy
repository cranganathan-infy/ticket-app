package com.coding.cloudbees.ticketapp.entity

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
class TrainSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    String sectionName

    @OneToMany(mappedBy = "section", cascade = CascadeType.MERGE)
    List<Seat> seats

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id")
    Train train
}
