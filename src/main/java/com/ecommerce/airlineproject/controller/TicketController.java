package com.ecommerce.airlineproject.controller;

import com.ecommerce.airlineproject.dto.*;
import com.ecommerce.airlineproject.service.TicketService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Purchase a ticket for a flight", description = "Pre-Condition: User must provide a valid JWT token. The specified flight must exist and have available seats.<br>Post-Condition: A new ticket is created and saved for the passenger. The available seats of the flight are reduced by one.")
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
    @Operation(summary = "View ticket details", description = "Pre-Condition: The ticket with the specified ID must exist in the database.<br>Post-Condition: Returns the details of the ticket, including flight and passenger information.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicket(@PathVariable("id") Long id) {
        TicketResponseDTO ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            return new ResponseEntity<>(new TransactionStatusDTO("Failed", "Ticket not found with ID: " + id), org.springframework.http.HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticket, org.springframework.http.HttpStatus.OK);
    }

    // DELETE Bilet Numarası ile bileti iptal et
    @Operation(summary = "Cancel an existing ticket", description = "Pre-Condition: User must provide a valid JWT token. The ticket with the given ID must exist.<br>Post-Condition: The ticket is successfully deleted from the database. The available seats of the associated flight are incremented by one.")
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
    @Operation(summary = "Perform a check-in for a ticket", description = "Pre-Condition: The ticket must exist and must not be checked-in already.<br>Post-Condition: A seat number is assigned to the ticket and check-in status is confirmed.")
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
    @Operation(summary = "Get passenger list for a specific flight", description = "Pre-Condition: User must provide a valid JWT token. The flight number must exist.<br>Post-Condition: Returns a paginated view of all passengers who bought tickets for the target flight.")
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