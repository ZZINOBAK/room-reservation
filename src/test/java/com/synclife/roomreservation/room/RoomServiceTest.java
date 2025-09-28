package com.synclife.roomreservation.room;

import com.synclife.roomreservation.AppRepository;
import com.synclife.roomreservation.DTO.RoomDTO;
import com.synclife.roomreservation.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    AppRepository appRepository;

    @InjectMocks
    RoomService roomService;

    @Test
    void createRoom() {
        // given
        RoomDTO req = new RoomDTO("room1", 1, "location", 1);

        // when
        roomService.createRoom(1, req);

        // then
        verify(appRepository).createRoom(
                "room1",
                1,
                "location",
                1
        );
    }
}
