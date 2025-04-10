package com.example.flyhas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationCode;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String nationalId;

    private String reservedBy;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @PrePersist
    public void generateReservationCode() {
        this.reservationCode = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        System.out.println(" Reservation Code Generated: " + reservationCode);
    }

    // Getter - Setter’lar
    public Long getId() {
        return id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
