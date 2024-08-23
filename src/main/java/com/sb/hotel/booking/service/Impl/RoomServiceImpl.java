package com.sb.hotel.booking.service.Impl;

import com.sb.hotel.booking.models.Room;
import com.sb.hotel.booking.repository.RoomRepository;
import com.sb.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile file, String hotelName,String roomType, BigDecimal roomPrice) throws IOException {
        log.info("Adding new room with type: {} and price: {}",hotelName, roomType, roomPrice);

        Room room = new Room();
        room.setHotelName(hotelName);
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        if (!file.isEmpty()) {
            byte[] photoBytes = file.getBytes();
            room.setPhoto(photoBytes);
            log.info("Photo successfully added for room type: {}", roomType);
        } else {
            log.warn("No photo provided for room type: {}", roomType);
        }

        Room savedRoom = roomRepository.save(room);
        log.info("Room added successfully with ID: {}", savedRoom.getId());

        return savedRoom;
    }

    @Override
    public Page<Room> getAllRooms(Pageable pageable) {
        log.info("Retrieving all rooms with pagination: {}", pageable);
        Page<Room> rooms = roomRepository.findAll(pageable);
        log.info("Retrieved {} rooms", rooms.getTotalElements());
        return rooms;
    }

    @Override
    public Page<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType, Pageable pageable) {
        log.info("Retrieving available rooms of type: {} between dates: {} and {}", roomType, checkInDate, checkOutDate);
        Page<Room> rooms = roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType, pageable);
        //log.info("Retrieved {} available rooms", rooms.getTotalElements());
        return rooms;
    }

    @Override
    public List<Room> getAllRooms() {
        log.info("Retrieving all rooms without pagination");
        List<Room> rooms = roomRepository.findAll();
        log.info("Retrieved {} rooms", rooms.size());
        return rooms;
    }

    @Transactional
    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        log.info("Retrieving available rooms of type: {} between dates: {} and {}", roomType, checkInDate, checkOutDate);
        List<Room> rooms = roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
        log.info("Retrieved {} available rooms", rooms.size());
        return rooms;
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        log.info("Retrieving room by ID: {}", roomId);
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isPresent()) {
            log.info("Room found with ID: {}", roomId);
        } else {
            log.warn("Room not found with ID: {}", roomId);
        }
        return roomOptional;
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws IOException {
        log.info("Retrieving photo for room ID: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.error("Room not found with ID: {}", roomId);
                    return new RuntimeException("Room not found with ID: " + roomId);
                });

        byte[] photoBytes = room.getPhoto();
        if (photoBytes != null) {
            log.info("Photo retrieved for room ID: {}", roomId);
        } else {
            log.warn("No photo found for room ID: {}", roomId);
        }

        return photoBytes != null ? photoBytes : new byte[0];
    }
}
