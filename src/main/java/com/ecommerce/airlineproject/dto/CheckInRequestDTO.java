package com.ecommerce.airlineproject.dto;

import lombok.Data;

@Data
public class CheckInRequestDTO {
    private String flightNumber;
    private String date;
    private String passengerName;
}