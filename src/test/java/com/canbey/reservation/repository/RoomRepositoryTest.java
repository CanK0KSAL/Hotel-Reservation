package com.canbey.reservation.repository;

import com.canbey.reservation.model.Room;
import com.canbey.reservation.model.enums.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoomRepositoryTest {

    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        roomRepository = new RoomRepository();
    }

    @Test
    void findAll_returnsSeededRoomsAsDefensiveCopy() {
        List<Room> rooms = roomRepository.findAll();

        assertEquals(5, rooms.size());
        rooms.clear();
        assertEquals(5, roomRepository.findAll().size());
    }

    @Test
    void findAll_containsExpectedSeedData() {
        List<Room> rooms = roomRepository.findAll();

        assertTrue(rooms.stream().anyMatch(r ->
                r.getId() == 1
                        && "101".equals(r.getRoomNumber())
                        && r.getType() == RoomType.SINGLE
                        && r.getPricePerNight() == 80.0
                        && r.isActive()));

        assertTrue(rooms.stream().anyMatch(r ->
                r.getId() == 5
                        && "301".equals(r.getRoomNumber())
                        && r.getType() == RoomType.SUITE
                        && r.getPricePerNight() == 250.0
                        && r.isActive()));
    }

    @Test
    void findById_returnsMatchingRoom() {
        Optional<Room> room = roomRepository.findById(3);

        assertTrue(room.isPresent());
        assertEquals("201", room.get().getRoomNumber());
        assertEquals(RoomType.DOUBLE, room.get().getType());
        assertEquals(120.0, room.get().getPricePerNight(), 0.001);
        assertTrue(room.get().isActive());
    }

    @Test
    void findById_returnsEmptyForUnknownId() {
        assertTrue(roomRepository.findById(999).isEmpty());
    }
}
