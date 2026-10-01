package com.canbey.reservation.console;

import com.canbey.reservation.model.Reservation;
import com.canbey.reservation.model.Room;
import com.canbey.reservation.service.ReservationService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final ReservationService reservationService;
    private final Scanner scanner;

    public Menu() {
        this.reservationService = new ReservationService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> listRooms();
                case 2 -> makeReservation();
                case 3 -> cancelReservation();
                case 4 -> viewReservations();
                case 5 -> {
                    System.out.println("Exiting system...");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Hotel Reservation System ===");
        System.out.println("1. List Rooms");
        System.out.println("2. Make Reservation");
        System.out.println("3. Cancel Reservation");
        System.out.println("4. View Reservations");
        System.out.println("5. Exit");
    }

    private void listRooms() {
        List<Room> rooms = reservationService.getAllRooms();

        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }

        System.out.println("\n--- Rooms ---");
        for (Room room : rooms) {
            System.out.printf(
                    "ID: %d | Room No: %s | Type: %s | Price: %.2f | Active: %s%n",
                    room.getId(),
                    room.getRoomNumber(),
                    room.getType(),
                    room.getPricePerNight(),
                    room.isActive() ? "Yes" : "No"
            );
        }
    }

    private void makeReservation() {
        try {
            System.out.println("\n--- New Reservation ---");
            System.out.print("Guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Guest phone: ");
            String guestPhone = scanner.nextLine();

            listRooms();
            int roomId = readInt("Room ID: ");
            LocalDate checkInDate = readDate("Check-in date (yyyy-MM-dd): ");
            LocalDate checkOutDate = readDate("Check-out date (yyyy-MM-dd): ");

            Reservation reservation = reservationService.createReservation(
                    guestName,
                    guestPhone,
                    roomId,
                    checkInDate,
                    checkOutDate
            );

            System.out.println("Reservation created successfully.");
            System.out.println(reservation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cancelReservation() {
        try {
            System.out.println("\n--- Cancel Reservation ---");
            int reservationId = readInt("Reservation ID: ");
            reservationService.cancelReservation(reservationId);
            System.out.println("Reservation cancelled successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        System.out.println("\n--- Reservations ---");
        for (Reservation reservation : reservations) {
            System.out.printf(
                    "ID: %d | Guest: %s | Phone: %s | Room: %s | Check-in: %s | Check-out: %s | Status: %s | Total: %.2f%n",
                    reservation.getId(),
                    reservation.getGuestName(),
                    reservation.getGuestPhone(),
                    reservation.getRoom().getRoomNumber(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate(),
                    reservation.getStatus(),
                    reservation.getTotalPrice()
            );
        }
    }

    private int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private LocalDate readDate(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }
}