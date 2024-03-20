package com.coding.cloudbees.ticketapp.listener

import com.coding.cloudbees.ticketapp.entity.Seat
import com.coding.cloudbees.ticketapp.entity.Train
import com.coding.cloudbees.ticketapp.entity.TrainSection
import com.coding.cloudbees.ticketapp.logging.KeyValueLogger
import com.coding.cloudbees.ticketapp.repository.SeatRepository
import com.coding.cloudbees.ticketapp.repository.TrainRepository
import com.coding.cloudbees.ticketapp.repository.TrainSectionRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    TrainRepository trainRepository

    @Autowired
    SeatRepository seatRepository

    @Autowired
    TrainSectionRepository trainSectionRepository

    @Override
    void onApplicationEvent(ApplicationReadyEvent event) {
        log.info(KeyValueLogger.log('Application ready event received by ticket-app', [hello: 'world', environment: System.getProperty('environment')]))
        //creating a train
        Train tempTrain = new Train(trainNumber: 1111, trainName: 'One express', origin: 'London', destination: 'Paris')
        trainRepository.save(tempTrain)

        //creating a train sections
        List<TrainSection> tempSections = [new TrainSection(sectionName: 'A', train: tempTrain), new TrainSection(sectionName: 'B', train: tempTrain)]
        trainSectionRepository.saveAll(tempSections)

        tempSections.each { trainSection ->
            (1..10).each { seatNumber ->
                Seat tempSeat = new Seat(seatNumber: seatNumber, section: trainSection)
                seatRepository.save(tempSeat)
            }
        }
    }
}
