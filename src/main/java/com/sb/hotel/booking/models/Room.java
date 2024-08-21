package com.sb.hotel.booking.models;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Room type is required")
    private String roomType;

    @NotNull(message = "Room price is required")
    @Positive(message = "Room price must be positive")
    private BigDecimal roomPrice;

    @NotEmpty(message = "Hotel name is required")
    private String hotelName;

    private String details;

    private boolean isBooked = false;

    @Lob
    private byte[] photo; // Changed from Blob to byte[]
    // private Blob photo;
    // Use Blob if you expect to handle very large binary data that might exceed memory constraints or require streaming.
    /*
    Use byte[]. It’s typically recommended for most applications unless you have specific requirements for handling very large binary data.
    It simplifies the code and avoids additional complexity in managing Blob objects.
     */

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookedRoom> bookings = new ArrayList<>();

    public void addBooking(BookedRoom booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        bookings.add(booking);
        booking.setRoom(this);
        isBooked = true;

        String bookingCode = RandomStringUtils.randomNumeric(10);
        booking.setBookingConfirmationCode(bookingCode);
    }

    public void removeBooking(BookedRoom booking) {
        if (booking != null && bookings.remove(booking)) {
            booking.setRoom(null);
            if (bookings.isEmpty()) {
                isBooked = false;
            }
        }
    }
}