package com.example.flyhas.dto;

public class CheckInRequest {
    private String reservationCode;
    private String lastName;

    public CheckInRequest() {
    }

    public CheckInRequest(String reservationCode, String lastName) {
        this.reservationCode = reservationCode;
        this.lastName = lastName;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
