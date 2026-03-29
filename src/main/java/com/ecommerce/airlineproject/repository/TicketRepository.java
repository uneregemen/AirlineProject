package com.ecommerce.airlineproject.repository; // Kendi paket adına göre düzelt

import com.ecommerce.airlineproject.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Şimdilik burası boş kalabilir, JpaRepository bize save() vb. metotları zaten veriyor.
    java.util.Optional<Ticket> findByFlight_FlightNumberAndPassengerName(String flightNumber, String passengerName);
    // Uçuş numarasına göre biletleri (yolcuları) bulur ve 10'arlı sayfalar halinde döndürür
    org.springframework.data.domain.Page<Ticket> findByFlight_FlightNumber(String flightNumber, org.springframework.data.domain.Pageable pageable);
}