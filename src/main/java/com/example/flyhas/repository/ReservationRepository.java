package com.example.flyhas.repository;

import com.example.flyhas.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByReservedBy(String reservedBy);

    Optional<Reservation> findByReservationCodeAndLastName(String reservationCode, String lastName);

    Optional<Reservation> findByReservationCode(String reservationCode);
}
