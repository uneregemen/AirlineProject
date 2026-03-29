package com.ecommerce.airlineproject.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponseDTO {
    private Long id; // PNR no
    private String passengerName;
    private String passengerID;
    private String flightNumber;
    private LocalDateTime purchaseDate;
}