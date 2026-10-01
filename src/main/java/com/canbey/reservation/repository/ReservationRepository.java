package com.canbey.reservation.repository;

import com.canbey.reservation.model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepository{
    private final List<Reservation> reservations = new ArrayList<>();
    private int nextId= 1;

    public void save(Reservation reservation){
        reservations.add(reservation);
    }
    public List<Reservation> findAll(){
        return new ArrayList<>(reservations);
    }
    public Optional<Reservation> findById(int id) {
        for (Reservation reservation : reservations) {
            if (reservation.getId() == id) {
                return Optional.of(reservation);
            }
        }
        return Optional.empty();
    }

    public int getNextId() {
        return nextId++;
    }
}