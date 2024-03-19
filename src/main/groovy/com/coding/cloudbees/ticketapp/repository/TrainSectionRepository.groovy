package com.coding.cloudbees.ticketapp.repository

import com.coding.cloudbees.ticketapp.entity.TrainSection
import org.springframework.data.jpa.repository.JpaRepository

interface TrainSectionRepository extends JpaRepository<TrainSection, Long> {
    TrainSection findByTrainIdAndSectionName(Long trainId, String sectionName)
}
