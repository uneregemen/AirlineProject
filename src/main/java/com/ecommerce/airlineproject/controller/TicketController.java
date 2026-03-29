package com.ecommerce.airlineproject.controller; // Paket ismini kontrol et

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

    // GET metodu ile bilet sorgulama
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicket(@PathVariable("id") Long id) {

        TicketResponseDTO ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            TransactionStatusDTO errorResponse = new TransactionStatusDTO("Failed", "Ticket not found with ID: " + id);
            return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticket, org.springframework.http.HttpStatus.OK);
    }

    // DELETE metodu ile bilet iptali
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
            @RequestParam(value = "page", defaultValue = "0") int page) {

        org.springframework.data.domain.Page<PassengerResponseDTO> passengers = ticketService.getPassengerList(flightNumber, page);

        return new ResponseEntity<>(passengers, org.springframework.http.HttpStatus.OK);
    }
}