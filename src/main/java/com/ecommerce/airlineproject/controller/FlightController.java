package com.ecommerce.airlineproject.controller;

import com.ecommerce.airlineproject.dto.FlightCreateRequestDTO;
import com.ecommerce.airlineproject.dto.FlightResponseDTO;
import com.ecommerce.airlineproject.dto.TransactionStatusDTO;
import com.ecommerce.airlineproject.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    // POST metodu ile uçuş ekleme
    @PostMapping("/add")
    public ResponseEntity<TransactionStatusDTO> addFlight(@Valid @RequestBody FlightCreateRequestDTO requestDTO) {
        TransactionStatusDTO response = flightService.addFlight(requestDTO);

        if (response.getStatus().equals("Failed")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET metodu ile uçuşları listeleme
    @GetMapping("/all")
    public ResponseEntity<java.util.List<FlightResponseDTO>> getAllFlights() {
        java.util.List<FlightResponseDTO> flights = flightService.getAllFlights();
        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    // GET metodu ile uçuş arama
    @GetMapping("/search")
    public ResponseEntity<org.springframework.data.domain.Page<FlightResponseDTO>> searchFlights(
            @RequestParam("DateFrom") String dateFrom,
            @RequestParam("DateTo") String dateTo,
            @RequestParam("AirportFrom") String airportFrom,
            @RequestParam("AirportTo") String airportTo,
            @RequestParam("NumberOfPeople") Integer numberOfPeople,
            @RequestParam("IsRoundTrip") Boolean isRoundTrip,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {


        int actualPage = pageNumber > 0 ? pageNumber - 1 : 0;

        org.springframework.data.domain.Page<FlightResponseDTO> flights = flightService.searchFlights(airportFrom, airportTo, numberOfPeople, actualPage);

        return new ResponseEntity<>(flights, org.springframework.http.HttpStatus.OK);
    }


    // POST metodu ile csv yükleme
    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TransactionStatusDTO> addFlightsByFile(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {

        TransactionStatusDTO response = flightService.addFlightsByFile(file);

        if (response.getStatus().equals("Success")) {
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
    }
}


