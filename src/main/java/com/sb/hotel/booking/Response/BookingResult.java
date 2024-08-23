package com.sb.hotel.booking.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResult {
    private boolean success;
    private String message;
    private String confirmationCode; // For saveBooking

    // Constructors, getters, setters
}

