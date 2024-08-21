package com.sb.hotel.booking.service.Impl;

import com.sb.hotel.booking.models.Room;
import com.sb.hotel.booking.repository.RoomRepository;
import com.sb.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FileStorageServiceImpl fileStorageService;

    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException {
        String fileName = fileStorageService.storeFile(file);
        
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        if (!fileName.isEmpty()) {
            byte[] photoBytes = fileName.getBytes();
              room.setPhoto(photoBytes);
            //room.setPhoto(photoBytes); // Use byte[] instead of Blob
        }

        return roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws IOException {
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isEmpty()) {
            throw new RuntimeException("Sorry, Room not found!");
        }

        byte[] photoBytes = roomOptional.get().getPhoto();

        if (photoBytes != null) {
            return photoBytes;
        }

        return new byte[0]; // Return an empty array instead of null
    }
}
