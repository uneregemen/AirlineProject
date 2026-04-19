package com.ecommerce.airlineproject.service;

import com.ecommerce.airlineproject.dto.FlightCreateRequestDTO;
import com.ecommerce.airlineproject.dto.FlightResponseDTO;
import com.ecommerce.airlineproject.dto.TransactionStatusDTO;
import com.ecommerce.airlineproject.entity.Flight;
import com.ecommerce.airlineproject.repository.FlightRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public TransactionStatusDTO addFlight(FlightCreateRequestDTO requestDTO) {
        if (flightRepository.existsByFlightNumber(requestDTO.getFlightNumber())) {
            return new TransactionStatusDTO("Failed", "Flight number already exists: " + requestDTO.getFlightNumber());
        }
        Flight newFlight = new Flight();
        newFlight.setFlightNumber(requestDTO.getFlightNumber());
        newFlight.setDateFrom(requestDTO.getDateFrom());
        newFlight.setDateTo(requestDTO.getDateTo());
        newFlight.setAirportFrom(requestDTO.getAirportFrom());
        newFlight.setAirportTo(requestDTO.getAirportTo());
        newFlight.setDuration(requestDTO.getDuration());
        newFlight.setCapacity(requestDTO.getCapacity());
        newFlight.setAvailableSeats(requestDTO.getCapacity());
        flightRepository.save(newFlight);
        return new TransactionStatusDTO("Success", "Flight added successfully");
    }

    public java.util.List<FlightResponseDTO> getAllFlights() {
        java.util.List<Flight> flights = flightRepository.findAll();
        java.util.List<FlightResponseDTO> responseList = new java.util.ArrayList<>();
        for (Flight flight : flights) {
            FlightResponseDTO dto = new FlightResponseDTO();
            dto.setFlightNumber(flight.getFlightNumber());
            dto.setDateFrom(flight.getDateFrom());
            dto.setDateTo(flight.getDateTo());
            dto.setAirportFrom(flight.getAirportFrom());
            dto.setAirportTo(flight.getAirportTo());
            dto.setAvailableSeats(flight.getAvailableSeats());

            responseList.add(dto);
        }
        return responseList;
    }

    public org.springframework.data.domain.Page<FlightResponseDTO> searchFlights(String from, String to, Integer numberOfPeople, int pageNumber, String dateFromStr, String dateToStr, Boolean isRoundTrip) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, 10);

        java.time.LocalDateTime parsedDateFrom = null;
        if (dateFromStr != null && !dateFromStr.isEmpty()) {
            try {
                parsedDateFrom = java.time.LocalDateTime.parse(dateFromStr);
            } catch (Exception e) {
                try { parsedDateFrom = java.time.LocalDate.parse(dateFromStr).atStartOfDay(); } catch (Exception ex) {}
            }
        }

        java.time.LocalDateTime parsedDateTo = null;
        if (dateToStr != null && !dateToStr.isEmpty()) {
            try {
                parsedDateTo = java.time.LocalDateTime.parse(dateToStr);
            } catch (Exception e) {
                try { parsedDateTo = java.time.LocalDate.parse(dateToStr).atTime(23, 59, 59); } catch (Exception ex) {}
            }
        }

        org.springframework.data.domain.Page<Flight> flightPage = flightRepository.findAvailableFlights(from, to, numberOfPeople, parsedDateFrom, parsedDateTo, pageable);

        return flightPage.map(flight -> {
            FlightResponseDTO dto = new FlightResponseDTO();
            dto.setFlightNumber(flight.getFlightNumber());
            dto.setDateFrom(flight.getDateFrom());
            dto.setDateTo(flight.getDateTo());
            dto.setAirportFrom(flight.getAirportFrom());
            dto.setAirportTo(flight.getAirportTo());
            dto.setAvailableSeats(flight.getAvailableSeats());
            return dto;
        });
    }

    // CSV Dosyasından Toplu Uçuş Ekleme
    public TransactionStatusDTO addFlightsByFile(org.springframework.web.multipart.MultipartFile file) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;

            // Dosyayı satır satır oku
            while ((line = br.readLine()) != null) {
                // İlk satır genelde başlıklardır (FlightNumber,DateFrom...), onu atlıyoruz
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Satırı virgüllerden böl
                String[] data = line.split(",");

                // Eğer satırda 7 sütun veri varsa kaydetme işlemine başla
                if (data.length >= 7) {
                    Flight flight = new Flight();
                    flight.setFlightNumber(data[0].trim());
                    flight.setDateFrom(java.time.LocalDateTime.parse(data[1].trim()));
                    flight.setDateTo(java.time.LocalDateTime.parse(data[2].trim()));
                    flight.setAirportFrom(data[3].trim());
                    flight.setAirportTo(data[4].trim());
                    flight.setDuration(Integer.parseInt(data[5].trim()));
                    flight.setCapacity(Integer.parseInt(data[6].trim()));
                    flight.setAvailableSeats(flight.getCapacity());

                    flightRepository.save(flight);
                    count++;
                }
            }
            //"Transaction status, File processes status" formatında cevap dönüyoruz
            return new TransactionStatusDTO("Success", "File processed successfully. " + count + " flights added.");

        } catch (Exception e) {
            return new TransactionStatusDTO("Failed", "Error processing file: " + e.getMessage());
        }
    }

}

