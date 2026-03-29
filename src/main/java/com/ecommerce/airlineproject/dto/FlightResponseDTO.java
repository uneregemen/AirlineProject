package com.ecommerce.airlineproject.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlightResponseDTO {

    private String flightNumber;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private String airportFrom;
    private String airportTo;
    private Integer availableSeats;
}