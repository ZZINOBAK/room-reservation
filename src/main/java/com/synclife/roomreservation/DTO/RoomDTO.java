package com.synclife.roomreservation.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoomDTO {

    private final String roomName;
    private final int userId;
    private final String location;
    private final int capacity;
}
