package com.ecommerce.airlineproject.controller;

import com.ecommerce.airlineproject.dto.*;
import com.ecommerce.airlineproject.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // POST metodu ile bilet satın alma işlemi
    @PostMapping("/buy")
    public ResponseEntity<TransactionStatusDTO> buyTicket(@Valid @RequestBody TicketCreateRequestDTO requestDTO) {

        TransactionStatusDTO response = ticketService.buyTicket(requestDTO);
        if (response.getStatus().equals("Success")) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // GET Bilet Numarası ile bilet detayını gör
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicket(@PathVariable("id") Long id) {
        TicketResponseDTO ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            return new ResponseEntity<>(new TransactionStatusDTO("Failed", "Ticket not found with ID: " + id), org.springframework.http.HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticket, org.springframework.http.HttpStatus.OK);
    }

    // DELETE Bilet Numarası ile bileti iptal et
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<TransactionStatusDTO> cancelTicket(@PathVariable("id") Long id) {
        TransactionStatusDTO response = ticketService.cancelTicket(id);
        if (response.getStatus().equals("Success")) {
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.NOT_FOUND);
        }
    }

    // POST metodu ile Check-in işlemi
    @PostMapping("/checkin")
    public ResponseEntity<TransactionStatusDTO> checkIn(@RequestBody CheckInRequestDTO request) {

        TransactionStatusDTO response = ticketService.checkIn(request);

        if (response.getStatus().equals("Success")) {
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
    }

    // GET metodu ile Yolcu Listesi
    @GetMapping("/passengers")
    public ResponseEntity<org.springframework.data.domain.Page<PassengerResponseDTO>> getPassengerList(
            @RequestParam("flightNumber") String flightNumber,
            @RequestParam("date") String date,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        int actualPage = pageNumber > 0 ? pageNumber - 1 : 0;

        org.springframework.data.domain.Page<PassengerResponseDTO> passengers = ticketService.getPassengerList(flightNumber, actualPage);

        return new ResponseEntity<>(passengers, org.springframework.http.HttpStatus.OK);
    }
}