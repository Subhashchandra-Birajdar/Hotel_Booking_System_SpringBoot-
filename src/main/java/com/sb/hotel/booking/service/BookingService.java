package com.sb.hotel.booking.service;

import com.sb.hotel.booking.models.BookedRoom;
import java.util.List;

public interface BookingService {

    /**
     * Saves a new booking for a specific room.
     * @param roomId the ID of the room to book
     * @param bookingRequest the booking details
     * @return a confirmation code for the booking
     */
    String saveBooking(Long roomId, BookedRoom bookingRequest);

    /**
     * Retrieves all bookings.
     * @return a list of all bookings
     */
    List<BookedRoom> getAllBookings();

    /**
     * Retrieves all bookings for a specific room.
     * @param roomId the ID of the room
     * @return a list of bookings for the specified room
     */
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    /**
     * Checks if the room is available for the given booking request.
     * @param bookingRequest the booking request details
     * @param existingBookings a list of existing bookings to check against
     * @return true if the room is available, false otherwise
     */
    boolean isRoomAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings);

    /**
     * Updates an existing booking.
     * @param bookingId the ID of the booking to update
     * @param updatedBooking the updated booking details
     * @return true if the update was successful, false otherwise
     */
    boolean updateBooking(Long bookingId, BookedRoom updatedBooking);

    /**
     * Cancels a booking by its ID.
     * @param bookingId the ID of the booking to cancel
     * @return true if the cancellation was successful, false otherwise
     */
    boolean cancelBooking(Long bookingId);
}
