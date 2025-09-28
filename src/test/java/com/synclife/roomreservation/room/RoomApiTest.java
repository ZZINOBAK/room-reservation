package com.synclife.roomreservation.room;

import com.synclife.roomreservation.AppController;
import com.synclife.roomreservation.DTO.RoomDTO;
import com.synclife.roomreservation.service.AvailabilityService;
import com.synclife.roomreservation.service.ReservationService;
import com.synclife.roomreservation.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RoomApiTest {

    @Mock
    RoomService roomService;
    @Mock
    AvailabilityService availabilityService;
    @Mock
    ReservationService reservationService;

    AppController appController;

    @BeforeEach
    void setUp() {
        appController = new AppController(roomService, availabilityService, reservationService);
    }

    @Test
    void createRoomByNull() {
        ResponseEntity<?> response = appController.createRoom("", new RoomDTO("room1", 1, "location", 1));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("인증 필요", response.getBody());
    }

    @Test
    void createRoomByUser() {
        ResponseEntity<?> response = appController.createRoom("user-token", new RoomDTO("room1", 1, "location", 1));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("관리자 전용", response.getBody());
    }

    @Test
    void createRoomByAdmin() {
        ResponseEntity<?> response = appController.createRoom("admin-token", new RoomDTO("room1", 1, "location", 1));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("방 등록 완료", response.getBody());
    }
}
