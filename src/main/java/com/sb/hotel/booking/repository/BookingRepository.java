package com.sb.hotel.booking.repository;

import com.sb.hotel.booking.models.BookedRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {

    /**
     * Finds all booked rooms by room ID.
     * @param roomId the ID of the room
     * @return a list of booked rooms
     */
    List<BookedRoom> findByRoomId(Long roomId);

    /**
     * Finds a booked room by its confirmation code.
     * @param confirmationCode the booking confirmation code
     * @return an optional booked room
     */
    Optional<BookedRoom> findByBookingConfirmationCode(String confirmationCode);

    /**
     * Finds booked rooms by guest email.
     * @param guestEmail the guest's email
     * @param pageable pagination information
     * @return a page of booked rooms
     */
    Page<BookedRoom> findByGuestEmail(String guestEmail, Pageable pageable);

    /**
     * Finds all booked rooms with a check-in date within a specified range.
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @param pageable pagination information
     * @return a page of booked rooms
     */
    Page<BookedRoom> findByCheckInDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);



}
