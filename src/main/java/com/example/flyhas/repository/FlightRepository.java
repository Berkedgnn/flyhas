package com.example.flyhas.repository;

import com.example.flyhas.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByOriginAndDestinationAndDepartureTimeBetween(
            String origin,
            String destination,
            LocalDateTime start,
            LocalDateTime end);
}
