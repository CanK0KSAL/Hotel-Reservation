package com.canbey.reservation.model;

import com.canbey.reservation.model.enums.ReservationStatus;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private String guestName;
    private String guestPhone;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;
    private double totalPrice;

    public Reservation(int id,
                       String guestName,
                       String guestPhone,
                       Room room,
                       LocalDate checkInDate,
                       LocalDate checkOutDate,
                       ReservationStatus status,
                       double totalPrice) {
        this.id = id;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", guestName='" + guestName + '\'' +
                ", guestPhone='" + guestPhone + '\'' +
                ", room=" + room.getRoomNumber() +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                '}';
    }
}