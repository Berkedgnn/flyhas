package com.example.flyhas.controller;

import com.example.flyhas.dto.CheckInRequest;
import com.example.flyhas.dto.ReservationRequest;
import com.example.flyhas.model.Reservation;
import com.example.flyhas.model.Seat;
import com.example.flyhas.repository.ReservationRepository;
import com.example.flyhas.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @PostMapping
    public ResponseEntity<?> makeReservations(@RequestBody List<ReservationRequest> requests) {
        List<Reservation> savedReservations = requests.stream().map(request -> {
            Seat seat = seatRepository.findById(request.getSeatId())
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            if (seat.isReserved()) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is already reserved");
            }

            seat.setReserved(true);
            seatRepository.save(seat);

            Reservation reservation = new Reservation();
            reservation.setFirstName(request.getFirstName());
            reservation.setLastName(request.getLastName());
            reservation.setEmail(request.getEmail());
            reservation.setBirthDate(request.getBirthDate());
            reservation.setNationalId(request.getNationalId());
            reservation.setReservedBy(request.getReservedBy());
            reservation.setSeat(seat);
            reservation.setCheckedIn(false);

            return reservationRepository.save(reservation);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(savedReservations);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Reservation>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<Reservation> reservations = reservationRepository.findByReservedBy(email);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/checkin/validate")
    public ResponseEntity<Reservation> validateCheckIn(@RequestBody CheckInRequest req) {
        return reservationRepository
                .findByReservationCodeAndLastName(req.getReservationCode(), req.getLastName())
                .map(res -> ResponseEntity.ok(res))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/checkin/{id}")
    public ResponseEntity<Reservation> performCheckIn(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(res -> {
                    res.setCheckedIn(true);
                    reservationRepository.save(res);
                    return ResponseEntity.ok(res);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}