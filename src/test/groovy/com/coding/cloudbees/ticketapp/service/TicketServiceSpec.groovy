package com.coding.cloudbees.ticketapp.service

import com.coding.cloudbees.ticketapp.entity.Seat
import com.coding.cloudbees.ticketapp.entity.Ticket
import com.coding.cloudbees.ticketapp.entity.TicketDetail
import com.coding.cloudbees.ticketapp.entity.Train
import com.coding.cloudbees.ticketapp.entity.TrainSection
import com.coding.cloudbees.ticketapp.entity.User
import com.coding.cloudbees.ticketapp.repository.SeatRepository
import com.coding.cloudbees.ticketapp.repository.TicketRepository
import com.coding.cloudbees.ticketapp.repository.TrainRepository
import com.coding.cloudbees.ticketapp.repository.TrainSectionRepository
import com.coding.cloudbees.ticketapp.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class TicketServiceSpec extends Specification {
    TicketService sut = new TicketService(
            ticketRepository: Mock(TicketRepository),
            userRepository: Mock(UserRepository),
            trainSectionRepository: Mock(TrainSectionRepository),
            trainRepository: Mock(TrainRepository),
            seatRepository: Mock(SeatRepository)
    )

    def setup() {
        ObjectMapper mapper = new ObjectMapper()
    }

    def 'updateSeatForUser'() {
        setup:
        Ticket reqTicket = new Ticket(id: 1, ticketDetail: new TicketDetail(section: 'A', seatNumber: 2, trainNumber: '1111'))
        Seat reqSeat = new Seat(id: 1, seatNumber: 1, reserved: true, section: new TrainSection(id: 1, sectionName: 'A'))
        TrainSection dbTrainSection = new TrainSection(id: 1, sectionName: 'A', seats: [reqSeat, new Seat(id: 2, seatNumber: 2, reserved: false)])
        Train dbTrain = new Train(id: 1, origin: 'London', destination: 'Paris',
                sections: [dbTrainSection])
        Ticket dbTicket = new Ticket(
                id: 1, pricePaid: 5, fromLoc: 'London', toLoc: 'Paris',
                user: new User(id: 1, email: 'soeme@email.com'),
                train: dbTrain)
        when:
        Ticket modifiedTicket = sut.modifySeat(reqTicket)

        then:
        1 * sut.ticketRepository.findById(reqTicket.id) >> Optional.of(dbTicket)
        1 * sut.trainSectionRepository.findByTrainIdAndSectionName(dbTicket.train.id, reqTicket.ticketDetail.section) >> dbTrainSection
        modifiedTicket
    }
}
