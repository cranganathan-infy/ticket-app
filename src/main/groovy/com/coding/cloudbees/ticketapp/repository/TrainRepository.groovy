package com.coding.cloudbees.ticketapp.repository

import com.coding.cloudbees.ticketapp.entity.Train
import org.springframework.data.jpa.repository.JpaRepository

interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findAllByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination)

    Train findTrainByTrainNumber(String trainNumber)
}

