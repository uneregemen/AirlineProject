package com.ecommerce.airlineproject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightCreateRequestDTO {

    @NotBlank(message = "Flight number can not be empty.")
    private String flightNumber;

    @NotNull(message ="Start date can not be null." )
    private LocalDateTime dateFrom;

    @NotNull(message = "End date cannot be null")
    private LocalDateTime dateTo;

    @NotBlank(message = "Departure airport can not be empty.")
    private String airportFrom;

    @NotBlank(message = "Arrival airport can not be empty.")
    private String airportTo;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer duration;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be greater than 0")
    private Integer capacity;
}
