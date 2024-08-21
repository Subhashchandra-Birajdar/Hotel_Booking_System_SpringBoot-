package com.sb.hotel.booking.service.impl;

import com.sb.hotel.booking.models.BookedRoom;
import com.sb.hotel.booking.models.Room;
import com.sb.hotel.booking.repository.BookingRepository;
import com.sb.hotel.booking.service.BookingService;
import com.sb.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public boolean cancelBooking(Long bookingId) {
        try {
            // Check if the booking exists before trying to delete
            if (bookingRepository.existsById(bookingId)) {
                bookingRepository.deleteById(bookingId);
                return true;
            } else {
                return false; // Booking with given ID does not exist
            }
        } catch (Exception e) {
            // Log the exception if needed
            return false; // An error occurred while trying to delete
        }
    }


    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));
        List<BookedRoom> existingBookings = room.getBookings();

        if (!isRoomAvailable(bookingRequest, existingBookings)) {
            throw new RuntimeException("Room is not available for the selected dates.");
        }

        room.addBooking(bookingRequest);
        bookingRepository.save(bookingRequest);

        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    public boolean isRoomAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking -> isOverlap(bookingRequest, existingBooking));
    }

    @Override
    public boolean updateBooking(Long bookingId, BookedRoom updatedBooking) {
        // Step 1: Retrieve the existing booking
        Optional<BookedRoom> existingBookingOpt = bookingRepository.findById(bookingId);

        if (existingBookingOpt.isEmpty()) {
            // Booking not found
            return false;
        }

        BookedRoom existingBooking = existingBookingOpt.get();

        // Step 2: Update the booking details
        existingBooking.setCheckInDate(updatedBooking.getCheckInDate());
        existingBooking.setCheckOutDate(updatedBooking.getCheckOutDate());
        existingBooking.setGuestFullName(updatedBooking.getGuestFullName());
        existingBooking.setGuestEmail(updatedBooking.getGuestEmail());
        existingBooking.setNumOfAdults(updatedBooking.getNumOfAdults());
        existingBooking.setNumOfChildren(updatedBooking.getNumOfChildren());
        existingBooking.setTotalNumOfGuest(updatedBooking.getTotalNumOfGuest());

        // Optional: If updating the room, make sure to validate availability again
        if (updatedBooking.getRoom() != null) {
            Room room = updatedBooking.getRoom();
            List<BookedRoom> existingBookings = room.getBookings();

            if (!roomIsAvailable(updatedBooking, existingBookings)) {
                throw new RuntimeException("The updated booking conflicts with existing bookings.");
            }

            existingBooking.setRoom(room);
        }

        // Step 3: Save the updated booking
        try {
            bookingRepository.save(existingBooking);
            return true;
        } catch (Exception e) {
            // Handle any exceptions that may occur during saving
            return false;
        }
    }

    public boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        LocalDate requestedCheckInDate = bookingRequest.getCheckInDate();
        LocalDate requestedCheckOutDate = bookingRequest.getCheckOutDate();

        // Check for overlap with existing bookings
        return existingBookings.stream()
                .noneMatch(existingBooking -> isDateOverlap(requestedCheckInDate, requestedCheckOutDate,
                        existingBooking.getCheckInDate(), existingBooking.getCheckOutDate()));
    }

    private boolean isDateOverlap(LocalDate checkInDate1, LocalDate checkOutDate1,
                                  LocalDate checkInDate2, LocalDate checkOutDate2) {
        return !checkInDate1.isAfter(checkOutDate2) && !checkOutDate1.isBefore(checkInDate2);
    }

    private boolean isOverlap(BookedRoom bookingRequest, BookedRoom existingBooking) {
        return bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate())
                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckInDate());
    }

}
