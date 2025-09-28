package com.synclife.roomreservation.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoomDTO {

    private final Long roomId;
    private final String roomName;
    private final String userId;
    private final String location;
    private final int capacity;
}
