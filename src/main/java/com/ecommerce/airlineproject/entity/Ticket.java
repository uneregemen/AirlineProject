package com.ecommerce.airlineproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "passenger_id", nullable = false)
    private String passengerID;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    // ticket - flight ilişkisi burada yapılıyor (foreign key ile)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "seat_number")
    private String seatNumber;
}