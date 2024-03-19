package com.coding.cloudbees.ticketapp.service

import com.coding.cloudbees.ticketapp.entity.Seat
import com.coding.cloudbees.ticketapp.entity.TicketDetail
import com.coding.cloudbees.ticketapp.entity.TrainSection
import com.coding.cloudbees.ticketapp.logging.KeyValueLogger
import com.coding.cloudbees.ticketapp.entity.Ticket
import com.coding.cloudbees.ticketapp.entity.Train
import com.coding.cloudbees.ticketapp.entity.User
import com.coding.cloudbees.ticketapp.repository.TicketRepository
import com.coding.cloudbees.ticketapp.repository.TrainRepository
import com.coding.cloudbees.ticketapp.repository.TrainSectionRepository
import com.coding.cloudbees.ticketapp.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class TicketService {

    @Autowired
    TicketRepository ticketRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TrainRepository trainRepository

    @Autowired
    TrainSectionRepository trainSectionRepository

    Ticket fetchTicketInfo(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow { new EmptyResultDataAccessException("No ticket ${ticketId} found", 1) }
    }

    Ticket fetchTicketByUserEmail(String email) {
        User dbUser = userRepository.findByEmailIgnoreCase(email)
        if (!dbUser) {
            throw new EmptyResultDataAccessException("No user found with them email ${email}", 1)
        }
        ticketRepository.findByUserId(dbUser.id).get()
    }

    List fetchUsersBySection(String trainNumber, String section) {
        Train dbTrain = trainRepository.findTrainByTrainNumber(trainNumber)
        if (!dbTrain) {
            throw new EmptyResultDataAccessException("No train found with  ${trainNumber}", 1)
        }
        List<Ticket> lstTicket = ticketRepository.findAllByTrainId(dbTrain.id)
        TrainSection dbSection = trainSectionRepository.findByTrainIdAndSectionName(dbTrain.id, section)
        List bookedSeats = []
        dbSection.seats.each { seat ->
            lstTicket.find() { tmpTicket ->
                if (tmpTicket.train.id == dbSection.train.id && seat.isReserved) {
                    bookedSeats << [reserved: seat.isReserved, seat: seat.seatNumber, email: tmpTicket.user.email, name: tmpTicket.user.firstName + tmpTicket.user.lastName]
                }
            }
        }

        log.info(KeyValueLogger.log('fetched section and seat info'))
        return bookedSeats
    }

    @Transactional
    Ticket allocateSeat(Ticket newTicket) {
        List<Train> trains = trainRepository.findAllByOriginIgnoreCaseAndDestinationIgnoreCase(newTicket.fromLoc, newTicket.toLoc)
        if (trains.isEmpty()) {
            throw new EmptyResultDataAccessException("No train found between ${newTicket.fromLoc} and ${newTicket.toLoc}", 1)
        }
        Train retrievedTrain = trains?.getFirst()

        Seat availableSeat = getAvailableSeatForTrain(retrievedTrain)
        if (!availableSeat) {
            throw new EmptyResultDataAccessException("No seats available for train ${retrievedTrain.trainNumber}", 1)
        }
        log.info(KeyValueLogger.log('Train found with seats', [trainNumber: retrievedTrain.id, availalbeSeat: availableSeat.seatNumber, section: availableSeat.section?.sectionName]))
        //get or create user
        User dbUser = userRepository.findByEmailIgnoreCase(newTicket.user.email)
        if (!dbUser) {
            log.info(KeyValueLogger.log('User not found in db', [email: newTicket?.user?.email]))
            userRepository.save(newTicket.user)
        }
        availableSeat.reserve()
        TrainSection updatedSection = retrievedTrain.sections.find { it.seats.contains(availableSeat) }
        TicketDetail updatedDetail = new TicketDetail(trainName: retrievedTrain.trainName, trainNumber: retrievedTrain.trainNumber, section: updatedSection.sectionName, seatNumber: updatedSection.seats.first.seatNumber)
        Ticket updatedTicket = ticketRepository.save(new Ticket(train: retrievedTrain, fromLoc: newTicket.fromLoc, toLoc: newTicket.toLoc, pricePaid: newTicket.pricePaid, user: newTicket.user))
        updatedTicket.ticketDetail = updatedDetail
        return updatedTicket
    }

    Ticket modifySeat(Ticket ticket) {
        //TODO-update seat
    }

    Ticket cancelReservation(Ticket ticket) {
        ticketRepository.deleteByUserId(userId)
    }

    Seat getAvailableSeatForTrain(Train train) {
        Seat seat = new Seat()
        List<TrainSection> trainSections = train.sections
        if (trainSections) {
            List<Seat> availableSeat = trainSections.seats.findAll { it.isReserved }.getFirst()
            seat = availableSeat?.getFirst()
        }
        return seat
    }
}

