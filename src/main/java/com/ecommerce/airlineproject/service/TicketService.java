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

    // her iki repoyu da eklemen gerek bağlamak için
    public TicketService(TicketRepository ticketRepository, FlightRepository flightRepository) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
    }

    // @Transactional: Eğer bilet kesilirken bilgisayar çökerse, yarım kalan işlemi geri alır
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

        // 3. Yer varsa yeni bileti (Ticket) oluştur
        Ticket ticket = new Ticket();
        ticket.setPassengerName(requestDTO.getPassengerName());
        ticket.setPassengerID(requestDTO.getPassengerID());
        ticket.setPurchaseDate(LocalDateTime.now()); // Satın alma saatini şu an olarak ayarla
        ticket.setFlight(flight); // BİLETİ UÇUŞA BAĞLADIĞIMIZ O KRİTİK SATIR!

        // 4. Uçağın boş koltuk sayısını 1 azalt
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        ticketRepository.save(ticket);
        flightRepository.save(flight);

        return new TransactionStatusDTO("Success", "Ticket successfully purchased for " + requestDTO.getPassengerName());
    }
    // Bilet numarasına (ID) göre bilet sorgulama
    public TicketResponseDTO getTicketById(Long id) {

        // bileti ID'sine göre bulmaya çalış bulamayabilir de, o yüzden Optional dönüyor
        java.util.Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isEmpty()) {
            return null;
        }
        Ticket ticket = ticketOptional.get();
        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setId(ticket.getId());
        responseDTO.setPassengerName(ticket.getPassengerName());
        responseDTO.setPassengerID(ticket.getPassengerID());
        responseDTO.setPurchaseDate(ticket.getPurchaseDate());
        responseDTO.setFlightNumber(ticket.getFlight().getFlightNumber());

        return responseDTO;
    }

    @org.springframework.transaction.annotation.Transactional
    public TransactionStatusDTO cancelTicket(Long ticketId) {

        // bileti ID'sine göre bul
        java.util.Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);

        // Eğer bilet yoksa hata dön
        if (ticketOptional.isEmpty()) {
            return new TransactionStatusDTO("Failed", "Ticket not found with ID: " + ticketId);
        }

        // Bilet varsa kutudan çıkar
        Ticket ticket = ticketOptional.get();
        //İŞTE CAN ALICI NOKTA: Biletin bağlı olduğu uçuşu bul!
        Flight flight = ticket.getFlight();
        //Koltuk iadesi: Uçağın boş koltuk sayısını 1 ARTIR
        flight.setAvailableSeats(flight.getAvailableSeats() + 1);
        //Uçağın yeni koltuk sayısını kaydet
        flightRepository.save(flight);
        //Bileti veritabanından kalıcı olarak sil (Çöpe at)
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

        // 3. Basit bir koltuk numarası üret (Örn: 1 ile 30 arası bir sayı ve A-F arası bir harf)
        int row = (int) (Math.random() * 30) + 1;
        char letter = (char) ('A' + Math.random() * 6);
        String generatedSeat = row + "" + letter;

        // 4. Koltuğu bilete kaydet ve veritabanını güncelle
        ticket.setSeatNumber(generatedSeat);
        ticketRepository.save(ticket);

        return new TransactionStatusDTO("Success", "Check-in successful. Your seat is: " + generatedSeat);
    }

    // Belirli bir uçuşun yolcu listesini sayfalama (Paging) ile getirme
    public org.springframework.data.domain.Page<PassengerResponseDTO> getPassengerList(String flightNumber, int pageNumber) {

        //Sayfa boyutu 10 olacak
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, 10);

        org.springframework.data.domain.Page<Ticket> ticketPage = ticketRepository.findByFlight_FlightNumber(flightNumber, pageable);
        return ticketPage.map(ticket -> {
            PassengerResponseDTO dto = new PassengerResponseDTO();
            dto.setPassengerName(ticket.getPassengerName());

            // Eğer check-in yapmamışsa "Not Checked-in" yazsın
            if (ticket.getSeatNumber() != null) {
                dto.setSeatNumber(ticket.getSeatNumber());
            } else {
                dto.setSeatNumber("Not Checked-in");
            }

            return dto;
        });
    }
}