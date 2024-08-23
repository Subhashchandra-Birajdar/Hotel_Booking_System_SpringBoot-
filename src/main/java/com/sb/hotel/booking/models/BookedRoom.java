package com.sb.hotel.booking.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString(exclude = "room")
@EqualsAndHashCode(of = "bookingId")
public class BookedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @NotNull(message = "Check-in date cannot be null")
    @Column(name = "check_in", nullable = false)
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date cannot be null")
    @Column(name = "check_out", nullable = false)
    private LocalDate checkOutDate;

    @NotNull(message = "Guest full name cannot be null")
    @Size(min = 1, max = 100, message = "Guest full name must be between 1 and 100 characters")
    @Column(name = "guest_fullName", nullable = false)
    private String guestFullName;

    @NotNull(message = "Guest email cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    @Column(name = "guest_email", nullable = false)
    private String guestEmail;

    @NotNull(message = "Number of adults cannot be null")
    @Min(value = 1, message = "There must be at least one adult")
    @Column(name = "adults", nullable = false)
    private int numOfAdults;

    @Min(value = 0, message = "Number of children cannot be negative")
    @Column(name = "children")
    private int numOfChildren;

    @NotNull(message = "Total number of guests cannot be null")
    @Min(value = 1, message = "There must be at least one guest")
    @Column(name = "total_guest", nullable = false)
    private int totalNumOfGuest;

    @NotNull(message = "Booking confirmation code cannot be null")
    @Size(min = 6, max = 20, message = "Booking confirmation code must be between 6 and 20 characters")
    @Column(name = "confirmation_Code", nullable = false, unique = true)
    private String bookingConfirmationCode;

    @NotNull(message = "Room cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
