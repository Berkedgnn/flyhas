package com.example.flyhas.controller;

import com.example.flyhas.model.Flight;
import com.example.flyhas.model.Seat;
import com.example.flyhas.repository.FlightRepository;
import com.example.flyhas.repository.SeatRepository;
import com.example.flyhas.util.SeatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "http://localhost:5173")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatRepository seatRepository;

    @GetMapping
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @PostMapping
    public Flight createFlight(@RequestBody Flight flight) {
        Flight savedFlight = flightRepository.save(flight);

        List<Seat> seats = SeatUtils.generateSeatsForFlight(savedFlight);
        seatRepository.saveAll(seats);

        savedFlight.setSeats(seats);
        return savedFlight;
    }

    @DeleteMapping("/{id}")
    public void deleteFlight(@PathVariable Long id) {
        flightRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public Flight getFlightById(@PathVariable Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
    }

    @GetMapping("/search")
    public List<Flight> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return flightRepository.findByOriginAndDestinationAndDepartureTimeBetween(
                origin, destination, startOfDay, endOfDay);
    }
}