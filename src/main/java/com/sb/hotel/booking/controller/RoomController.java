package com.sb.hotel.booking.controller;


import com.sb.hotel.booking.Response.BookingResponse;
import com.sb.hotel.booking.Response.RoomResponse;
import com.sb.hotel.booking.models.BookedRoom;
import com.sb.hotel.booking.models.Room;
import com.sb.hotel.booking.service.BookingService;
import com.sb.hotel.booking.service.Impl.FileStorageServiceImpl;
import com.sb.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling room-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final FileStorageServiceImpl fileStorageService;

    /**
     * Adds a new room to the system.
     * @param photo the photo of the room
     * @param roomType the type of the room
     * @param roomPrice the price of the room
     * @return ResponseEntity with the added room details
     */
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
                                                   @RequestParam("roomType") String roomType,
                                                   @RequestParam("roomPrice") BigDecimal roomPrice) {
        try {
            Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
            String photoBase64 = savedRoom.getPhoto() != null ? Base64.getEncoder().encodeToString(savedRoom.getPhoto()) : null;
            RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice(), savedRoom.isBooked(), photoBase64, new ArrayList<>());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // Handle exceptions appropriately
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Retrieves available rooms based on date range and room type.
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @param roomType the type of the room
     * @return ResponseEntity with the list of available rooms
     */
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) {
        try {
            List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
            List<RoomResponse> roomResponses = new ArrayList<>();

            for (Room room : availableRooms) {
                byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
                RoomResponse roomResponse = getRoomResponse(room, photoBytes);
                roomResponses.add(roomResponse);
            }

            if (roomResponses.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(roomResponses);
        } catch (IOException e) {
            // Handle exceptions appropriately
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Creates a RoomResponse object from a Room entity and its photo.
     * @param room the room entity
     * @param photoBytes the photo bytes of the room
     * @return RoomResponse with the room details and bookings
     */
    public RoomResponse getRoomResponse(Room room, byte[] photoBytes) {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings.stream().map(booking -> new BookingResponse(booking.getBookingId(),
                booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList();

        String photoBase64 = photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;

        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBase64, bookingInfo);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

}
