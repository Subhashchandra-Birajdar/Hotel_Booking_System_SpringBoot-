package com.sb.hotel.booking.service;

import com.sb.hotel.booking.models.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    /**
     * Adds a new room with the specified details and photo.
     * @param file the photo of the room
     * @param roomType the type of the room
     * @param roomPrice the price of the room
     * @return the added Room object
     * @throws IOException if there is an error processing the photo
     */
    Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException;

    /**
     * Retrieves all rooms.
     * @return a list of all rooms
     */
    List<Room> getAllRooms();

    /**
     * Retrieves available rooms based on the specified date range and room type.
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @param roomType the type of the room
     * @return a list of available rooms
     */
    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    /**
     * Retrieves a room by its ID.
     * @param roomId the ID of the room
     * @return an Optional containing the Room object if found, or an empty Optional if not found
     */
    Optional<Room> getRoomById(Long roomId);

    /**
     * Retrieves the photo of a room by its ID.
     * @param roomId the ID of the room
     * @return a byte array representing the photo of the room
     * @throws IOException if there is an error processing the photo
     */
    byte[] getRoomPhotoByRoomId(Long roomId) throws IOException;
}
