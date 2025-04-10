package com.example.flyhas.controller;

import com.example.flyhas.model.Flight;
import com.example.flyhas.model.Seat;
import com.example.flyhas.repository.FlightRepository;
import com.example.flyhas.repository.SeatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "http://localhost:5173")
public class SeatController {

    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;

    public SeatController(SeatRepository seatRepository, FlightRepository flightRepository) {
        this.seatRepository = seatRepository;
        this.flightRepository = flightRepository;
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Seat>> getSeatsByFlight(@PathVariable Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        if (flight == null)
            return ResponseEntity.notFound().build();

        List<Seat> seats = seatRepository.findByFlight(flight);
        return ResponseEntity.ok(seats);
    }
}
