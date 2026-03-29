package com.ecommerce.airlineproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketCreateRequestDTO {

    @NotBlank(message = "Passenger name cannot be empty")
    private String passengerName;

    @NotBlank(message = "Passenger ID cannot be empty")
    private String passengerID;

    @NotBlank(message = "Flight number cannot be empty")
    private String flightNumber; //Müşteri uçuş ID'sini bilemez, uçuş numarasını gönderir.
}