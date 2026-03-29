package com.ecommerce.airlineproject.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;
@Entity
@Table(name = "flights")
@Data
public class Flight {

    @Id//primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)//auto increment of ids
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @Column(name = "date_to", nullable = false)
    private LocalDateTime dateTo;

    @Column(name = "date_from", nullable = false)
    private LocalDateTime dateFrom;

    @Column(name = "airport_from", nullable = false)
    private String airportFrom;

    @Column(name = "airport_to", nullable = false)
    private String airportTo;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;


}
