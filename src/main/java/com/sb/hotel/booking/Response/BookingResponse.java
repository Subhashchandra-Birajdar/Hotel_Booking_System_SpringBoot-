package com.sb.hotel.booking.response;

import com.sb.hotel.booking.Response.RoomResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long id;

    //@NotNull(message = "Check-in date cannot be null")
    private LocalDate checkInDate;

    //@NotNull(message = "Check-out date cannot be null")
    private LocalDate checkOutDate;

   // @NotNull(message = "Guest name cannot be null")
    @Size(min = 1, max = 100, message = "Guest name must be between 1 and 100 characters")
    private String guestName;

   // @NotNull(message = "Guest email cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    private String guestEmail;

   // @Min(value = 1, message = "There must be at least one adult")
    private int numOfAdults;

   // @Min(value = 0, message = "Number of children cannot be negative")
    private int numOfChildren;

    //@Min(value = 1, message = "Total number of guests must be at least one")
    private int totalNumOfGuests;

   // @NotNull(message = "Booking confirmation code cannot be null")
    @Size(min = 6, max = 20, message = "Booking confirmation code must be between 6 and 20 characters")
    private String bookingConfirmationCode;

    private RoomResponse room;

    public BookingResponse(Long id, LocalDate checkInDate, LocalDate checkOutDate, String bookingConfirmationCode) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}
