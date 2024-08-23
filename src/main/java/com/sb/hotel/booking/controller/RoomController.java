package com.sb.hotel.booking.controller;

import com.sb.hotel.booking.Response.RoomResponse;
import com.sb.hotel.booking.models.BookedRoom;
import com.sb.hotel.booking.models.Room;
import com.sb.hotel.booking.service.BookingService;
import com.sb.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;

    /**
     * Adds a new room to the system.
     * @param photo the photo of the room
     * @param roomType the type of the room
     * @param roomPrice the price of the room
     * @return ResponseEntity with the added room details
     */
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
                                                   @RequestParam("hotelName") String hotelName,
                                                   @RequestParam("roomType") String roomType,
                                                   @RequestParam("roomPrice") BigDecimal roomPrice) {
        try {
            Room savedRoom = roomService.addNewRoom(photo,hotelName, roomType, roomPrice);
            String photoBase64 = savedRoom.getPhoto() != null ? Base64.getEncoder().encodeToString(savedRoom.getPhoto()) : null;
            RoomResponse response = new RoomResponse(savedRoom.getId(),
                    savedRoom.getRoomType(), savedRoom.getRoomPrice(), savedRoom.isBooked(), photoBase64, new ArrayList<>());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // Handle exceptions appropriately
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Retrieves all rooms with pagination.
     * @param pageable pagination information
     * @return ResponseEntity with paginated list of rooms
     */
    @GetMapping("/rooms")
    public ResponseEntity<Page<RoomResponse>> getAllRooms(Pageable pageable) {
        try {
            Page<Room> rooms = roomService.getAllRooms(pageable);
            Page<RoomResponse> roomResponses = rooms.map(room -> {
                byte[] photoBytes = room.getPhoto();
                String photoBase64 = photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;
                return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), new ArrayList<>());
            });

            return ResponseEntity.ok(roomResponses);
        } catch (Exception e) {
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
            @RequestParam("checkInDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
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
     * Retrieves a room photo by room ID.
     * @param roomId the ID of the room
     * @return ResponseEntity with the room photo
     */
    @GetMapping("/room-photo/{roomId}")
    public ResponseEntity<String> getRoomPhotoByRoomId(@PathVariable Long roomId) {
        try {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(roomId);
            String photoBase64 = photoBytes.length > 0 ? Base64.getEncoder().encodeToString(photoBytes) : null;

            if (photoBase64 != null) {
                return ResponseEntity.ok(photoBase64);
            } else {
                return ResponseEntity.noContent().build(); // No photo available
            }
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
    private RoomResponse getRoomResponse(Room room, byte[] photoBytes) {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<com.sb.hotel.booking.response.BookingResponse> bookingInfo = bookings
                        .stream().map(booking -> new com.sb.hotel.booking.response.BookingResponse(booking.getBookingId(),
                booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList();

        String photoBase64 = photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;

        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBase64, bookingInfo);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }
}
        /*
        Request Parameters:

photo (Type: File) – the image file of the room.
hotelName (Type: Text) – the name of the hotel.
roomType (Type: Text) – the type of room.
roomPrice (Type: Text) – the price of the room.
Postman Setup:

Choose POST method.
Set the URL: http://localhost:8080/api/add/new-room.
In the Body tab, select form-data.
Add fields:
photo (File)
hotelName (Text)
roomType (Text)
roomPrice (Text)

 Retrieve All Rooms:
Open Postman, select GET method.
Enter the URL: http://localhost:8080/api/rooms

check room availability
http://localhost:8080/api/available-rooms?checkInDate=2024-09-01&checkOutDate=2024-09-10&roomType=Deluxe

Retrive photo by room id
http://localhost:8080/api/room-photo/1

         */