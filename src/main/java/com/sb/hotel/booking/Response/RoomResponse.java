package com.sb.hotel.booking.Response;

import com.sb.hotel.booking.response.BookingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked;
    private String photoBase64;
    private List<com.sb.hotel.booking.response.BookingResponse> bookings; // List of booking responses

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, boolean isBooked, List<BookingResponse> bookings) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.bookings = bookings;
    }

    // Getters and setters
}
