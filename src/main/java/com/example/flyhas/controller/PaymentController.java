package com.example.flyhas.controller;

import com.example.flyhas.dto.PaymentRequest;
import com.example.flyhas.model.Payment;
import com.example.flyhas.model.Reservation;
import com.example.flyhas.repository.PaymentRepository;
import com.example.flyhas.repository.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentController(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> makePayment(@RequestBody PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Payment payment = new Payment();
        payment.setCardNumber(request.getCardNumber());
        payment.setExpiryDate(request.getExpiryDate());
        payment.setCvv(request.getCvv());
        payment.setReservation(reservation);

        paymentRepository.save(payment);

        return ResponseEntity.ok("Payment completed successfully.");
    }
}
