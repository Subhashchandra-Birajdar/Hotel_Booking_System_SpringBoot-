package com.sb.hotel.booking.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "bookingId")
public class BookedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @NotNull()
    @Column(name = "check_in")
    private LocalDate checkInDate;

    @NotNull()
    @Column(name = "check_out")
    private LocalDate checkOutDate;

    @NotEmpty(message = "Guest full name is required")
    @Column(name = "guest_full_name")
    private String guestFullName;

    @NotEmpty(message = "Guest email is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$",
            message = "Invalid email format")
    @Column(name = "guest_email")
    private String guestEmail;

    @Positive(message = "Number of adults must be positive")
    @Column(name = "adults")
    private int numOfAdults;

    @Positive(message = "Number of children must be positive")
    @Column(name = "children")
    private int numOfChildren;

    @Positive(message = "Total number of guests must be positive")
    @Column(name = "total_guest")
    private int totalNumOfGuest;

    @NotEmpty(message = "Booking confirmation code is required")
    @Column(name = "confirmation_code")
    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}
