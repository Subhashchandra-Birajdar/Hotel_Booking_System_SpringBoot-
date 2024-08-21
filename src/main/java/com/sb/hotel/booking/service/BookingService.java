package com.sb.hotel.booking.service;

import com.sb.hotel.booking.models.BookedRoom;

import java.util.List;

public interface BookingService {

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    List<BookedRoom> getAllBookings();

    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings);
    
}
