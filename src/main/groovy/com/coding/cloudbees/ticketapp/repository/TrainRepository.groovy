package com.coding.cloudbees.ticketapp.repository

import com.coding.cloudbees.ticketapp.entity.Train
import com.coding.cloudbees.ticketapp.entity.User
import org.springframework.data.jpa.repository.JpaRepository

import javax.persistence.Id

interface TrainRepository extends JpaRepository<Train, Long > {
    List<Train> findAllByOriginIgnoreCaseAndDestinationIgnoreCase(String origin,String destination)
    Train findTrainByTrainNumber(String trainNumber)
}

