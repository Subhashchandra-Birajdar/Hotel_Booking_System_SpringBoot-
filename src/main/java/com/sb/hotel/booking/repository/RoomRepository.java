package com.sb.hotel.booking.repository;

import com.sb.hotel.booking.models.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Finds available rooms by room type and date range.
     *
     * @param checkInDate  the check-in date
     * @param checkOutDate the check-out date
     * @param roomType     the type of the room
     * @param pageable
     * @return a list of available rooms that match the criteria
     */
//    @Query("SELECT r FROM Room r "
//            + "WHERE r.roomType LIKE %:roomType% "
//            + "AND r.id NOT IN ("
//            + "    SELECT br.room.id FROM BookedRoom br "
//            + "    WHERE (br.checkInDate < :checkOutDate AND br.checkOutDate > :checkInDate)"
//            + ")")
//    Page<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType, Pageable pageable);
        @Query("SELECT r FROM Room r LEFT JOIN r.bookings b " +
                "WHERE r.roomType = :roomType AND (b.checkOutDate <= :checkInDate " +
                "OR b.checkInDate >= :checkOutDate OR b.id IS NULL)")
        Page<Room> findAvailableRoomsByDatesAndType(@Param("checkInDate") LocalDate checkInDate, @Param("checkOutDate") LocalDate checkOutDate, @Param("roomType") String roomType, Pageable pageable);


    @Query("SELECT r FROM Room r "
            + "WHERE r.roomType LIKE %:roomType% "
            + "AND r.id NOT IN ("
            + "    SELECT br.room.id FROM BookedRoom br "
            + "    WHERE (br.checkInDate < :checkOutDate AND br.checkOutDate > :checkInDate)"
            + ")")
    List<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

}
