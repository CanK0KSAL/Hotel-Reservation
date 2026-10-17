package com.canbey.reservation.model;

import com.canbey.reservation.model.enums.RoomType;

public class Room {
    private int id;
    private String roomNumber;
    private RoomType type;
    private double pricePerNight;
    private boolean active;

    public Room(int id, String roomNumber, RoomType type, double pricePerNight, boolean active) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", type=" + type +
                ", pricePerNight=" + pricePerNight +
                ", active=" + active +
                '}';
    }
}