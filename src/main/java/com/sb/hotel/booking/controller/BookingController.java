package com.sb.hotel.booking.controller;

import com.sb.hotel.booking.models.BookedRoom;
import com.sb.hotel.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/room/booking")
    public ResponseEntity<String> saveBooking(@RequestParam Long roomId, @RequestBody BookedRoom bookingRequest) {
        String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);

        return ResponseEntity.ok("Room booked successfully, Your booking confirmation code is :" + confirmationCode);
    }

}