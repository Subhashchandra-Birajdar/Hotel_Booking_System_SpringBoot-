package com.sb.hotel.booking.service.Impl;

import com.sb.hotel.booking.models.BookedRoom;
import com.sb.hotel.booking.models.Room;
import com.sb.hotel.booking.repository.BookingRepository;
import com.sb.hotel.booking.service.BookingService;
import com.sb.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public boolean cancelBooking(Long bookingId) {
        try {
            if (bookingRepository.existsById(bookingId)) {
                bookingRepository.deleteById(bookingId);
                return true;
            } else {
                logger.warn("Booking with ID {} does not exist.", bookingId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error occurred while canceling booking with ID {}: {}", bookingId, e.getMessage());
            return false;
        }
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        //validateBookingRequest(bookingRequest);
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

    @Override
    public boolean updateBooking(Long bookingId, BookedRoom updatedBooking) {
        // Step 1: Retrieve the existing booking
        Optional<BookedRoom> existingBookingOpt = bookingRepository.findById(bookingId);

        if (existingBookingOpt.isEmpty()) {
            logger.warn("Booking with ID {} not found.", bookingId);
            return false;
        }

        BookedRoom existingBooking = existingBookingOpt.get();
        updateBookingDetails(existingBooking, updatedBooking);

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
            logger.error("Error occurred while updating booking with ID {}: {}", bookingId, e.getMessage());
            // Handle any exceptions that may occur during saving
            return false;
        }
    }

    private void validateBookingRequest(BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
    }

    private void updateBookingDetails(BookedRoom existingBooking, BookedRoom updatedBooking) {
        // Step 2: Update the booking details
        existingBooking.setCheckInDate(updatedBooking.getCheckInDate());
        existingBooking.setCheckOutDate(updatedBooking.getCheckOutDate());
        existingBooking.setGuestFullName(updatedBooking.getGuestFullName());
        existingBooking.setGuestEmail(updatedBooking.getGuestEmail());
        existingBooking.setNumOfAdults(updatedBooking.getNumOfAdults());
        existingBooking.setNumOfChildren(updatedBooking.getNumOfChildren());
        existingBooking.setTotalNumOfGuest(updatedBooking.getTotalNumOfGuest());
    }

    public boolean isRoomAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking -> isOverlap(bookingRequest, existingBooking));
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
