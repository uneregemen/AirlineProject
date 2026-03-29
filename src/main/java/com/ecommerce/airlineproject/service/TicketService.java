package com.ecommerce.airlineproject.service;

import com.ecommerce.airlineproject.dto.*;
import com.ecommerce.airlineproject.entity.Flight;
import com.ecommerce.airlineproject.entity.Ticket;
import com.ecommerce.airlineproject.repository.FlightRepository;
import com.ecommerce.airlineproject.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;

    public TicketService(TicketRepository ticketRepository, FlightRepository flightRepository) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public TransactionStatusDTO buyTicket(TicketCreateRequestDTO requestDTO) {

        // 1. Müşterinin istediği uçuşu bul
        Flight flight = flightRepository.findByFlightNumber(requestDTO.getFlightNumber());

        if (flight == null) {
            return new TransactionStatusDTO("Failed", "Flight not found: " + requestDTO.getFlightNumber());
        }

        if (flight.getAvailableSeats() <= 0) {
            return new TransactionStatusDTO("Failed", "Sorry, no available seats on this flight.");
        }

        // 2. Yer varsa yeni bilet oluştur
        Ticket ticket = new Ticket();
        ticket.setPassengerName(requestDTO.getPassengerName());
        ticket.setPassengerID("1");
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setFlight(flight);

        // 3. Uçağın boş koltuk sayısını azalt
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        ticketRepository.save(ticket);
        flightRepository.save(flight);

        return new TransactionStatusDTO("Success", "Ticket successfully purchased for " + requestDTO.getPassengerName());
    }

    // Bileti Bulma
    public TicketResponseDTO getTicketById(Long id) {
        java.util.Optional<Ticket> ticketOpt = ticketRepository.findById(id);

        if (ticketOpt.isEmpty()) return null;
        Ticket ticket = ticketOpt.get();
        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setId(ticket.getId());
        responseDTO.setPassengerName(ticket.getPassengerName());
        responseDTO.setPurchaseDate(ticket.getPurchaseDate());
        responseDTO.setPassengerID(ticket.getPassengerID());
        if (ticket.getFlight() != null) {
            responseDTO.setFlightNumber(ticket.getFlight().getFlightNumber());
        }
        return responseDTO;
    }

    // Bileti İptal Etme
    @org.springframework.transaction.annotation.Transactional
    public TransactionStatusDTO cancelTicket(Long id) {
        java.util.Optional<Ticket> ticketOpt = ticketRepository.findById(id);

        if (ticketOpt.isEmpty()) {
            return new TransactionStatusDTO("Failed", "Ticket not found with ID: " + id);
        }

        Ticket ticket = ticketOpt.get();
        Flight flight = ticket.getFlight();

        flight.setAvailableSeats(flight.getAvailableSeats() + 1);
        flightRepository.save(flight);
        ticketRepository.delete(ticket);

        return new TransactionStatusDTO("Success", "Ticket successfully canceled. Seat returned to the flight.");
    }

    // Check-in İşlemi
    @org.springframework.transaction.annotation.Transactional
    public TransactionStatusDTO checkIn(CheckInRequestDTO request) {

        // 1. Müşterinin bu uçuşta bileti var mı kontrol et
        java.util.Optional<Ticket> ticketOpt = ticketRepository.findByFlight_FlightNumberAndPassengerName(
                request.getFlightNumber(), request.getPassengerName());

        if (ticketOpt.isEmpty()) {
            return new TransactionStatusDTO("Failed", "Ticket not found for passenger: " + request.getPassengerName());
        }

        Ticket ticket = ticketOpt.get();

        // 2. Müşteri zaten check-in yapmış mı kontrol et
        if (ticket.getSeatNumber() != null) {
            return new TransactionStatusDTO("Failed", "Passenger is already checked in. Seat: " + ticket.getSeatNumber());
        }

        int row = (int) (Math.random() * 30) + 1;
        char letter = (char) ('A' + Math.random() * 6);
        String generatedSeat = row + "" + letter;

        // 4. Koltuğu bilete kaydet ve veritabanını güncelle
        ticket.setSeatNumber(generatedSeat);
        ticketRepository.save(ticket);

        return new TransactionStatusDTO("Success", "Check-in successful. Your seat is: " + generatedSeat);
    }

    public org.springframework.data.domain.Page<PassengerResponseDTO> getPassengerList(String flightNumber, int pageNumber) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, 10);

        org.springframework.data.domain.Page<Ticket> ticketPage = ticketRepository.findByFlight_FlightNumber(flightNumber, pageable);
        return ticketPage.map(ticket -> {
            PassengerResponseDTO dto = new PassengerResponseDTO();
            dto.setPassengerName(ticket.getPassengerName());
            if (ticket.getSeatNumber() != null) {
                dto.setSeatNumber(ticket.getSeatNumber());
            } else {
                dto.setSeatNumber("Not Checked-in");
            }

            return dto;
        });
    }
}