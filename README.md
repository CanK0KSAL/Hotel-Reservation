# Hotel Reservation System

A Java 17 console application for managing hotel room reservations. Built with object-oriented design, in-memory repositories, and a dedicated service layer for reservation business rules.

## Features

- View a catalog of five preconfigured hotel rooms
- Create reservations with guest details and check-in/check-out dates
- Prevent overlapping reservations for the same room
- Calculate total reservation prices
- List existing reservations
- Cancel eligible reservations
- Validate user input and reservation constraints

## Tech Stack

- Java 17
- Gradle
- JUnit 5
- Java standard library (`LocalDate`, `Scanner`, collections)

## Architecture

The application separates console interaction, business logic, persistence abstractions, and domain models.

```text
src/
  main/java/com/canbey/reservation/
    Main.java
    console/
      Menu.java
    model/
      Room.java
      Reservation.java
      enums/
        RoomType.java
        ReservationStatus.java
    repository/
      RoomRepository.java
      ReservationRepository.java
    service/
      ReservationService.java
  test/java/com/canbey/reservation/
    repository/
      RoomRepositoryTest.java
      ReservationRepositoryTest.java
    service/
      ReservationServiceTest.java
```

**Main / Menu:** Application entry point and interactive console interface.

**ReservationService:** Reservation validation, availability checking, price calculation, and cancellation rules.

**Repositories:** In-memory storage and lookup for rooms and reservations.

**Models:** Room and reservation domain objects, types, and statuses.

## Reservation Business Rules

- Guest name and phone number cannot be empty.
- Check-in cannot be in the past.
- Check-out must be later than check-in.
- Rooms must exist and be active to accept reservations.
- Active reservations for the same room cannot overlap.
- Adjacent date ranges are allowed.
- Total price equals the number of nights multiplied by the room's nightly rate.
- Cancelled reservations no longer block their original dates.
- A reservation cannot be cancelled on or after its check-in date.
- Already cancelled reservations cannot be cancelled again.

## Getting Started

### Requirements

- JDK 17
- Internet access for initial Gradle dependency resolution

### Run the application

Windows PowerShell:

```powershell
.\gradlew.bat run
```

Linux/macOS:

```bash
./gradlew run
```

Follow the interactive menu to list rooms, create reservations, view reservations, or cancel an existing reservation.

## Testing

The project includes 28 JUnit 5 automated tests covering reservation rules, pricing, date validation, overlaps, cancellations, and repository behavior.

Windows:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Linux/macOS:

```bash
./gradlew clean test
./gradlew build
```

The latest local verification passed all 28 tests and completed the Gradle build successfully.

## Current Limitations

- Data is stored in memory and is lost when the application exits.
- The room catalog is predefined.
- There is no database, authentication, or graphical interface.
- The application is designed for single-user console interaction.
- Room availability is checked when creating a reservation rather than through a separate date-range search.

This is an educational Java project focused on object-oriented design and reservation business logic.
