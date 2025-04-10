package com.example.flyhas.util;

import com.example.flyhas.model.Flight;
import com.example.flyhas.model.Seat;

import java.util.ArrayList;
import java.util.List;

public class SeatUtils {

    public static List<Seat> generateSeatsForFlight(Flight flight) {
        List<Seat> seats = new ArrayList<>();

        String[] seatLetters = { "A", "B", "C", "D", "E", "F" };

        for (int row = 1; row <= 6; row++) {
            for (String letter : seatLetters) {
                Seat seat = new Seat();
                seat.setFlight(flight);
                seat.setSeatNumber(row + letter);
                seat.setReserved(false);
                seats.add(seat);
            }
        }

        return seats;
    }
}
