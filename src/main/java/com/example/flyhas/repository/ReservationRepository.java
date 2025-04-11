package com.example.flyhas.repository;

import com.example.flyhas.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Kullanıcının emailine göre rezervasyonları getirir
    List<Reservation> findByReservedBy(String reservedBy);
}
