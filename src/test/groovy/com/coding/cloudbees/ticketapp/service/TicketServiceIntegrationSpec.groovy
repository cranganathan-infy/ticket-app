package com.coding.cloudbees.ticketapp.service

import com.coding.cloudbees.ticketapp.TicketAppApplicationIntegrationSpec
import com.coding.cloudbees.ticketapp.entity.Seat
import com.coding.cloudbees.ticketapp.entity.Ticket
import com.coding.cloudbees.ticketapp.entity.Train
import com.coding.cloudbees.ticketapp.entity.TrainSection
import com.coding.cloudbees.ticketapp.entity.User
import com.coding.cloudbees.ticketapp.repository.SeatRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import spock.lang.Unroll

@Unroll
class TicketServiceIntegrationSpec extends TicketAppApplicationIntegrationSpec {
    @Autowired
    TicketService sut

    @Autowired
    SeatRepository seatRepository

    User user1
    User user2
    Ticket ticket1
    Train tempTrain

    void setup() {
        user1 = new User(email: 'user1@email.com', firstName: 'Fname', lastName: 'Lname')
        user2 = new User(email: 'user2@email.com', firstName: 'Fname2', lastName: 'Lname2')
        sut.userRepository.saveAll([user1, user2])
        tempTrain = new Train(trainNumber: '1234', trainName: 'Test express', origin: 'London', destination: 'Paris')
        sut.trainRepository.save(tempTrain)
    }

    def 'fetchTicketByUserEmail-happy path'() {
        setup:
        ticket1 = new Ticket(pricePaid: 5, user: user1, fromLoc: 'from', toLoc: 'to')
        sut.ticketRepository.save(ticket1)

        when:
        Ticket savedTicket = sut.fetchTicketByUserEmail("user1@email.com")

        then:
        savedTicket == ticket1

        cleanup:
        sut.ticketRepository.delete(ticket1)
    }

    def 'fetchTicketByUserEmail-exception-#condition'() {
        when:
        sut.fetchTicketByUserEmail(email)

        then:
        EmptyResultDataAccessException exception = thrown()
        exception.message.contains(email)

        where:
        email                | condition
        "user3@email.com"    | 'no user'
        "someuser@email.com" | 'no ticket'
    }

    def 'fetchUsersBySection'() {
        setup:
        Seat tmpSeat = new Seat(reserved: true, seatNumber: 1)
        TrainSection tmpSection = new TrainSection(sectionName: 'A', seats: [tmpSeat])
        Train tmpTrain = new Train(trainName: 'TExpress', trainNumber: '111', origin: 'start', destination: 'stop', sections: [tmpSection])
        sut.trainSectionRepository.save(tmpTrain)

        when:
        List seats = sut.fetchUsersBySection(trainNum, section)

        then:
        seats == expected

        where:
        trainNum | section | expected
        '1231'   | 'B'     | []
        '111'    | 'B'     | []
    }

    void cleanup() {
        seatRepository.deleteAll()
        sut.trainSectionRepository.deleteAll()
        sut.trainRepository.deleteAll()
        sut.ticketRepository.deleteAll()
        sut.userRepository.deleteAll()
    }
}
