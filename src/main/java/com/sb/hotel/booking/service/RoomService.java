package com.sb.hotel.booking.service;

import com.sb.hotel.booking.models.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws SQLException, IOException;

    List<Room> getAllRooms();

    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    Optional<Room> getRoomById(Long roomId);

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;
    
}
