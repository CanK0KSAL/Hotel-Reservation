package com.canbey.reservation.repository;

import com.canbey.reservation.model.Room;
import com.canbey.reservation.model.enums.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepository {
    private final List<Room> rooms = new ArrayList<>();

    public RoomRepository() {
        rooms.add(new Room(1, "101", RoomType.SINGLE, 80.0, true));
        rooms.add(new Room(2, "102", RoomType.SINGLE, 85.0, true));
        rooms.add(new Room(3, "201", RoomType.DOUBLE, 120.0, true));
        rooms.add(new Room(4, "202", RoomType.DOUBLE, 130.0, true));
        rooms.add(new Room(5, "301", RoomType.SUITE, 250.0, true));
    }

    public List<Room> findAll() {
        return new ArrayList<>(rooms);
    }

    public Optional<Room> findById(int id) {
        for (Room room : rooms) {
            if (room.getId() == id) {
                return Optional.of(room);
            }
        }
        return Optional.empty();
    }
}