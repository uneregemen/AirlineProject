package com.ecommerce.airlineproject.repository;
import com.ecommerce.airlineproject.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long>{

    boolean existsByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE f.airportFrom = :from AND f.airportTo = :to AND f.availableSeats >= :people")
    Page<Flight> findAvailableFlights(
            @Param("from") String from,
            @Param("to") String to,
            @Param("people") Integer people,
            Pageable pageable
    );

    Flight findByFlightNumber(String flightNumber);//PNR a göre uçuşu getirir
}


// Repository sayesinde artık veri kaydetmek için save(), tümünü bulmak için findAll(),
// id ile bulmak için findById() gibi metotları sıfırdan yazmamıza gerek kalmayacak, hepsi
// JpaRepository'den miras olarak gelecek.
