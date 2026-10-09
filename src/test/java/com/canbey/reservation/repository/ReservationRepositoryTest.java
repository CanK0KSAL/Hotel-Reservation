package com.canbey.reservation.repository;

import com.canbey.reservation.model.Reservation;
import com.canbey.reservation.model.Room;
import com.canbey.reservation.model.enums.ReservationStatus;
import com.canbey.reservation.model.enums.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReservationRepositoryTest {

    private ReservationRepository reservationRepository;
    private Room sampleRoom;

    @BeforeEach
    void setUp() {
        reservationRepository = new ReservationRepository();
        sampleRoom = new Room(1, "101", RoomType.SINGLE, 80.0, true);
    }

    @Test
    void getNextId_incrementsSequentially() {
        assertEquals(1, reservationRepository.getNextId());
        assertEquals(2, reservationRepository.getNextId());
        assertEquals(3, reservationRepository.getNextId());
    }

    @Test
    void save_andFindById_returnsSavedReservation() {
        Reservation reservation = createSampleReservation(1);

        reservationRepository.save(reservation);

        Optional<Reservation> found = reservationRepository.findById(1);
        assertTrue(found.isPresent());
        assertSame(reservation, found.get());
        assertEquals("Dana", found.get().getGuestName());
    }

    @Test
    void findById_returnsEmptyForUnknownId() {
        assertTrue(reservationRepository.findById(42).isEmpty());
    }

    @Test
    void findAll_returnsDefensiveCopyOfStoredReservations() {
        Reservation first = createSampleReservation(1);
        Reservation second = createSampleReservation(2);
        reservationRepository.save(first);
        reservationRepository.save(second);

        List<Reservation> all = reservationRepository.findAll();
        assertEquals(2, all.size());

        all.clear();
        assertEquals(2, reservationRepository.findAll().size());
    }

    private Reservation createSampleReservation(int id) {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        return new Reservation(
                id,
                "Dana",
                "555-0400",
                sampleRoom,
                checkIn,
                checkIn.plusDays(2),
                ReservationStatus.CONFIRMED,
                160.0
        );
    }
}
