package com.sb.hotel.booking.controller;

import com.sb.hotel.booking.models.BookedRoom;
import com.sb.hotel.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling booking-related operations.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    /**
     * Books a room based on the provided room ID and booking details.
     * @param roomId the ID of the room to book
     * @param bookingRequest the booking details
     * @return ResponseEntity with the booking confirmation code or error message
     */
    @PostMapping("/room/booking")
    public ResponseEntity<?> saveBooking(@RequestParam Long roomId, @Valid @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            //return ResponseEntity.ok(new BookingResponseMessage("Room booked successfully", confirmationCode));
            return new ResponseEntity<>(confirmationCode,HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Handle specific exceptions if needed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }


    @PutMapping("/update/{bookingId}")
    public ResponseEntity<String> updateBooking(@PathVariable Long bookingId, @RequestBody BookedRoom updatedBooking) {
        boolean isUpdated = bookingService.updateBooking(bookingId, updatedBooking);
        if (isUpdated) {
            return ResponseEntity.ok("Booking updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        boolean isCancelled = bookingService.cancelBooking(bookingId);
        if (isCancelled) {
            return ResponseEntity.ok("Booking canceled successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
        /*
        Delete : http://localhost:8080/bookings/cancel/1
        Response : Booking canceled successfully.
         */
    }

    /**
     * Response object for successful booking.
     */
    private static class BookingResponseMessage {
        private final String message;
        private final String confirmationCode;

        public BookingResponseMessage(String message, String confirmationCode) {
            this.message = message;
            this.confirmationCode = confirmationCode;
        }

        public String getMessage() {
            return message;
        }

        public String getConfirmationCode() {
            return confirmationCode;
        }
    }

    /**
     * Response object for error messages.
     */
    private static class ErrorResponse {
        private final String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

/*
POST : http://localhost:8080/bookings/room/booking?roomId=1
Request :
{
    "checkInDate": "2024-09-10",
    "checkOutDate": "2024-09-15",
    "guestName": "John Doe",
    "guestEmail": "john.doe@example.com",
    "numOfAdults": 2,
    "numOfChildren": 1,
    "totalNumOfGuests": 3,
    "bookingConfirmationCode": "CONF123",
    "room": {
        "id": 1,
        "roomType": "Deluxe",
        "roomPrice": 200.00,
        "isBooked": false,
        "photoBase64": "base64encodedphotodata",
        "bookings": []
    }
}
Response :
    {
    "message": "Room booked successfully",
    "confirmationCode": "4948918860"
    }
 */