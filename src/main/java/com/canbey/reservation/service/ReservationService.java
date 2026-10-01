package com.canbey.reservation.service;

import com.canbey.reservation.model.Reservation;
import com.canbey.reservation.model.Room;
import com.canbey.reservation.model.enums.ReservationStatus;
import com.canbey.reservation.repository.ReservationRepository;
import com.canbey.reservation.repository.RoomRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService() {
        this.roomRepository = new RoomRepository();
        this.reservationRepository = new ReservationRepository();
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(String guestName,
                                         String guestPhone,
                                         int roomId,
                                         LocalDate checkInDate,
                                         LocalDate checkOutDate) {

        validateGuestInfo(guestName, guestPhone);
        validateDates(checkInDate, checkOutDate);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));

        if (!room.isActive()) {
            throw new IllegalStateException("Room is not active.");
        }

        if (!isRoomAvailable(room, checkInDate, checkOutDate)) {
            throw new IllegalStateException("Room is not available for the selected dates.");
        }

        long nightCount = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double totalPrice = nightCount * room.getPricePerNight();

        Reservation reservation = new Reservation(
                reservationRepository.getNextId(),
                guestName.trim(),
                guestPhone.trim(),
                room,
                checkInDate,
                checkOutDate,
                ReservationStatus.CONFIRMED,
                totalPrice
        );

        reservationRepository.save(reservation);
        return reservation;
    }

    public void cancelReservation(int reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled.");
        }

        if (!LocalDate.now().isBefore(reservation.getCheckInDate())) {
            throw new IllegalStateException("Past or active reservations cannot be cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    private void validateGuestInfo(String guestName, String guestPhone) {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name cannot be empty.");
        }

        if (guestPhone == null || guestPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest phone cannot be empty.");
        }
    }

    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = LocalDate.now();

        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be empty.");
        }

        if (checkInDate.isBefore(today)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
    }

    private boolean isRoomAvailable(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation existingReservation : reservations) {
            if (existingReservation.getStatus() == ReservationStatus.CANCELLED) {
                continue;
            }

            if (existingReservation.getRoom().getId() != room.getId()) {
                continue;
            }

            boolean isOverlapping =
                    checkInDate.isBefore(existingReservation.getCheckOutDate()) &&
                            checkOutDate.isAfter(existingReservation.getCheckInDate());

            if (isOverlapping) {
                return false;
            }
        }

        return true;
    }
}