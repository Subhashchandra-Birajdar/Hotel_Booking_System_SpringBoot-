package com.sb.hotel.booking.service.Impl;

import com.sb.hotel.booking.models.BookedRoom;
import com.sb.hotel.booking.models.Room;
import com.sb.hotel.booking.repository.BookingRepository;
import com.sb.hotel.booking.service.BookingService;
import com.sb.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final RoomService roomService;

    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new RuntimeException("Please provide valid date.");
        }

        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();

        if (!roomIsAvailable(bookingRequest, existingBookings)) {
            throw new RuntimeException("This room is not available for the selected dates.");
        }

        room.addBooking(bookingRequest);
        bookingRepository.save(bookingRequest);

        return bookingRequest.getBookingConfirmationCode();
    }

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    public boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking -> bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                        || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                        || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                        && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                        && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                        && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                        && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                        && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate())));
    }
}
