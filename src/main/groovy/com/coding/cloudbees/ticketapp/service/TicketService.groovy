package com.coding.cloudbees.ticketapp.service

import com.coding.cloudbees.ticketapp.entity.Seat
import com.coding.cloudbees.ticketapp.entity.TicketDetail
import com.coding.cloudbees.ticketapp.entity.TrainSection
import com.coding.cloudbees.ticketapp.logging.KeyValueLogger
import com.coding.cloudbees.ticketapp.entity.Ticket
import com.coding.cloudbees.ticketapp.entity.Train
import com.coding.cloudbees.ticketapp.entity.User
import com.coding.cloudbees.ticketapp.repository.SeatRepository
import com.coding.cloudbees.ticketapp.repository.TicketRepository
import com.coding.cloudbees.ticketapp.repository.TrainRepository
import com.coding.cloudbees.ticketapp.repository.TrainSectionRepository
import com.coding.cloudbees.ticketapp.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

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

    @Autowired
    SeatRepository seatRepository

    Ticket fetchTicketInfo(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow { new EmptyResultDataAccessException("No ticket with id ${ticketId} found", 1) }
    }

    Ticket fetchTicketByUserEmail(String email) {
        User dbUser = userRepository.findByEmailIgnoreCase(email)
        if (!dbUser) {
            throw new EmptyResultDataAccessException("No user found with them email ${email}", 1)
        }
        Ticket dbTicket = ticketRepository.findByUserId(dbUser.id)
        if (!dbTicket) {
            throw new EmptyResultDataAccessException("No ticket found with email ${email}", 1)
        }
        dbTicket
    }

    List fetchUsersBySection(String trainNumber, String section) {
        Train dbTrain = trainRepository.findTrainByTrainNumber(trainNumber)
        if (!dbTrain) {
            return []
        }
        List<Ticket> lstTicket = ticketRepository.findAllByTrainId(dbTrain.id)
        TrainSection dbSection = trainSectionRepository.findByTrainIdAndSectionName(dbTrain.id, section)
        List bookedSeats = []
        if (dbSection) {
            dbSection.seats.each { seat ->
                lstTicket.find { tmpTicket ->
                    if (tmpTicket.train.id == dbSection.train.id && seat.reserved) {
                        bookedSeats << [reserved: seat.reserved, seat: seat.seatNumber, email: tmpTicket.user.email, name: tmpTicket.user.firstName + tmpTicket.user.lastName]
                    }
                }
            }
        }

        log.info(KeyValueLogger.log('section and seat info', [section: dbSection?.sectionName]))
        return bookedSeats
    }

    @Transactional
    Ticket allocateSeat(Ticket newTicket) {
        List<Train> trains = trainRepository.findAllByOriginIgnoreCaseAndDestinationIgnoreCase(newTicket.fromLoc, newTicket.toLoc)
        if (trains.isEmpty()) {
            throw new EmptyResultDataAccessException("No train found between ${newTicket.fromLoc} and ${newTicket.toLoc}", 1)
        }
        Train retrievedTrain = trains?.first

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

    @Transactional
    Ticket modifySeat(Ticket requestedTicket) {
        Ticket updatedTicket = fetchTicketInfo(requestedTicket.id)
        Seat existingSeat = getSeatBooked(requestedTicket.ticketDetail.section, updatedTicket.train)
        if ((existingSeat.section.sectionName == requestedTicket.ticketDetail.section) && (existingSeat.seatNumber == requestedTicket.ticketDetail.seatNumber)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "seat number ${existingSeat?.seatNumber} and section ${existingSeat?.section?.sectionName} is same")
        }
        //check if requested seat is already booked
        if (existingSeat.reserved) {
            TrainSection requestedSection = trainSectionRepository.findByTrainIdAndSectionName(updatedTicket.train.id, requestedTicket.ticketDetail.section)
            Seat requestedSeat = requestedSection.seats.find { it.seatNumber == requestedTicket.ticketDetail.seatNumber }
            if (requestedSeat) {
                existingSeat.cancelReservation()
                requestedSeat.reserve()
                log.info('ticket updated', [ticketId: requestedSeat.seatNumber])
            } else {
                log.Error(KeyValueLogger.log("no requested seat to cancel", [requestedSeatNumber: requestedTicket.ticketDetail.seatNumber]))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "requested seat not found to cancel ${requestedTicket.ticketDetail.seatNumber}")
            }
            requestedTicket
        } else {
            log.info(KeyValueLogger.log('Ticket not updated', [ticketId: requestedTicket.id]))
        }
    }

    @Transactional
    void cancelReservationByTicketId(Long ticketId) {
        Ticket cancelTicket = fetchTicketInfo(ticketId)
        Seat bookedSeat = getSeatBooked(cancelTicket.user.id, cancelTicket.train)
        if (bookedSeat.reserved) {
            bookedSeat.cancelReservation()
            ticketRepository.deleteById(ticketId)
            log.info(KeyValueLogger.log('reservation cancelled', [ticketId: ticketId]))
        } else {
            log.info(KeyValueLogger.log('ticket not cancelled', [ticketId: ticketId]))
        }
    }

    Seat getAvailableSeatForTrain(Train train) {
        Seat seat = new Seat()
        List<TrainSection> trainSections = train.sections
        if (trainSections) {
            List<Seat> availableSeat = trainSections.seats.findAll { it.reserved }.first
            seat = availableSeat?.first
        }
        return seat
    }

    Seat getSeatBooked(String sectionName, Train train) {
        TrainSection section = train?.sections.find { it.sectionName == sectionName }
        section?.seats.find { it.reserved }
    }
}

