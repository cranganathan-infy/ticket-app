package com.coding.cloudbees.ticketapp.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Canonical
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    String email
    @JsonProperty('first_name')
    String firstName
    @JsonProperty('last_name')
    String lastName
}
