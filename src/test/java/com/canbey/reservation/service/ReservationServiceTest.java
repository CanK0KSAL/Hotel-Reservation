package com.canbey.reservation.service;

import com.canbey.reservation.model.Reservation;
import com.canbey.reservation.model.Room;
import com.canbey.reservation.model.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService();
        today = LocalDate.now();
    }

    @Test
    void createReservation_withValidData_succeeds() {
        Reservation reservation = reservationService.createReservation(
                "Alice Smith",
                "555-0100",
                1,
                today.plusDays(1),
                today.plusDays(3)
        );

        assertNotNull(reservation);
        assertEquals(1, reservation.getId());
        assertEquals("Alice Smith", reservation.getGuestName());
        assertEquals("555-0100", reservation.getGuestPhone());
        assertEquals(1, reservation.getRoom().getId());
        assertEquals(today.plusDays(1), reservation.getCheckInDate());
        assertEquals(today.plusDays(3), reservation.getCheckOutDate());
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        assertEquals(1, reservationService.getAllReservations().size());
    }

    @Test
    void createReservation_calculatesTotalPriceFromNightsAndRoomRate() {
        Room room = reservationService.getAllRooms().stream()
                .filter(r -> r.getId() == 3)
                .findFirst()
                .orElseThrow();

        LocalDate checkIn = today.plusDays(2);
        LocalDate checkOut = today.plusDays(5); // 3 nights
        double expectedTotal = 3 * room.getPricePerNight();

        Reservation reservation = reservationService.createReservation(
                "Bob Jones",
                "555-0200",
                3,
                checkIn,
                checkOut
        );

        assertEquals(expectedTotal, reservation.getTotalPrice(), 0.001);
    }

    @Test
    void createReservation_trimsGuestNameAndPhone() {
        Reservation reservation = reservationService.createReservation(
                "  Carol  ",
                "  555-0300  ",
                1,
                today.plusDays(1),
                today.plusDays(2)
        );

        assertEquals("Carol", reservation.getGuestName());
        assertEquals("555-0300", reservation.getGuestPhone());
    }

    @Test
    void createReservation_rejectsEmptyGuestName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        "   ",
                        "555-0100",
                        1,
                        today.plusDays(1),
                        today.plusDays(2)
                )
        );
        assertEquals("Guest name cannot be empty.", exception.getMessage());
    }

    @Test
    void createReservation_rejectsNullGuestName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        null,
                        "555-0100",
                        1,
                        today.plusDays(1),
                        today.plusDays(2)
                )
        );
        assertEquals("Guest name cannot be empty.", exception.getMessage());
    }

    @Test
    void createReservation_rejectsEmptyGuestPhone() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        "Alice",
                        "",
                        1,
                        today.plusDays(1),
                        today.plusDays(2)
                )
        );
        assertEquals("Guest phone cannot be empty.", exception.getMessage());
    }

    @Test
    void createReservation_rejectsPastCheckInDate() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        "Alice",
                        "555-0100",
                        1,
                        today.minusDays(1),
                        today.plusDays(1)
                )
        );
        assertEquals("Check-in date cannot be in the past.", exception.getMessage());
    }

    @Test
    void createReservation_rejectsCheckOutOnOrBeforeCheckIn() {
        IllegalArgumentException sameDay = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        "Alice",
                        "555-0100",
                        1,
                        today.plusDays(1),
                        today.plusDays(1)
                )
        );
        assertEquals("Check-out date must be after check-in date.", sameDay.getMessage());

        IllegalArgumentException beforeCheckIn = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        "Alice",
                        "555-0100",
                        1,
                        today.plusDays(3),
                        today.plusDays(2)
                )
        );
        assertEquals("Check-out date must be after check-in date.", beforeCheckIn.getMessage());
    }

    @Test
    void createReservation_rejectsNullDates() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        "Alice",
                        "555-0100",
                        1,
                        null,
                        today.plusDays(2)
                )
        );
        assertEquals("Dates cannot be empty.", exception.getMessage());
    }

    @Test
    void createReservation_rejectsNonExistingRoom() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        "Alice",
                        "555-0100",
                        999,
                        today.plusDays(1),
                        today.plusDays(2)
                )
        );
        assertEquals("Room not found.", exception.getMessage());
    }

    @Test
    void createReservation_rejectsInactiveRoom() throws Exception {
        Room room = reservationService.getAllRooms().stream()
                .filter(r -> r.getId() == 2)
                .findFirst()
                .orElseThrow();
        setRoomActive(room, false);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> reservationService.createReservation(
                        "Alice",
                        "555-0100",
                        2,
                        today.plusDays(1),
                        today.plusDays(2)
                )
        );
        assertEquals("Room is not active.", exception.getMessage());
    }

    @Test
    void createReservation_rejectsOverlappingActiveReservation() {
        reservationService.createReservation(
                "First Guest",
                "555-0001",
                1,
                today.plusDays(5),
                today.plusDays(10)
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> reservationService.createReservation(
                        "Second Guest",
                        "555-0002",
                        1,
                        today.plusDays(8),
                        today.plusDays(12)
                )
        );
        assertEquals("Room is not available for the selected dates.", exception.getMessage());
    }

    @Test
    void createReservation_allowsAdjacentNonOverlappingDateRanges() {
        Reservation first = reservationService.createReservation(
                "First Guest",
                "555-0001",
                1,
                today.plusDays(5),
                today.plusDays(10)
        );

        Reservation second = reservationService.createReservation(
                "Second Guest",
                "555-0002",
                1,
                today.plusDays(10),
                today.plusDays(14)
        );

        assertEquals(ReservationStatus.CONFIRMED, first.getStatus());
        assertEquals(ReservationStatus.CONFIRMED, second.getStatus());
        assertEquals(2, reservationService.getAllReservations().size());
    }

    @Test
    void createReservation_allowsSameDatesOnDifferentRooms() {
        assertDoesNotThrow(() -> reservationService.createReservation(
                "Guest A",
                "555-0001",
                1,
                today.plusDays(3),
                today.plusDays(6)
        ));
        assertDoesNotThrow(() -> reservationService.createReservation(
                "Guest B",
                "555-0002",
                2,
                today.plusDays(3),
                today.plusDays(6)
        ));
        assertEquals(2, reservationService.getAllReservations().size());
    }

    @Test
    void cancelReservation_succeedsForFutureConfirmedReservation() {
        Reservation reservation = reservationService.createReservation(
                "Alice",
                "555-0100",
                1,
                today.plusDays(4),
                today.plusDays(6)
        );

        reservationService.cancelReservation(reservation.getId());

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void cancelReservation_rejectsAlreadyCancelledReservation() {
        Reservation reservation = reservationService.createReservation(
                "Alice",
                "555-0100",
                1,
                today.plusDays(4),
                today.plusDays(6)
        );
        reservationService.cancelReservation(reservation.getId());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> reservationService.cancelReservation(reservation.getId())
        );
        assertEquals("Reservation is already cancelled.", exception.getMessage());
    }

    @Test
    void cancelReservation_rejectsUnknownReservationId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.cancelReservation(999)
        );
        assertEquals("Reservation not found.", exception.getMessage());
    }

    @Test
    void cancelReservation_rejectsWhenCheckInIsToday() {
        Reservation reservation = reservationService.createReservation(
                "Alice",
                "555-0100",
                1,
                today,
                today.plusDays(2)
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> reservationService.cancelReservation(reservation.getId())
        );
        assertEquals("Past or active reservations cannot be cancelled.", exception.getMessage());
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    void createReservation_allowsDatesAfterCancellationOfOverlappingStay() {
        Reservation original = reservationService.createReservation(
                "Alice",
                "555-0100",
                1,
                today.plusDays(5),
                today.plusDays(10)
        );
        reservationService.cancelReservation(original.getId());

        Reservation replacement = reservationService.createReservation(
                "Bob",
                "555-0200",
                1,
                today.plusDays(7),
                today.plusDays(9)
        );

        assertEquals(ReservationStatus.CANCELLED, original.getStatus());
        assertEquals(ReservationStatus.CONFIRMED, replacement.getStatus());
        assertEquals(2, reservationService.getAllReservations().size());
    }

    @Test
    void getAllRooms_returnsSeededCatalog() {
        assertEquals(5, reservationService.getAllRooms().size());
    }

    private static void setRoomActive(Room room, boolean active) throws Exception {
        Field activeField = Room.class.getDeclaredField("active");
        activeField.setAccessible(true);
        activeField.setBoolean(room, active);
    }
}
