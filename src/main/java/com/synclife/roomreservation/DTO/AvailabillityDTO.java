package com.synclife.roomreservation.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AvailabillityDTO {
    private final Long roomId;
    private final String roomName;
    private final List<TimeSlot> reservationList;
    private final List<TimeSlot> free;
}
