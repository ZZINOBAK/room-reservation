package com.synclife.roomreservation.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class TimeSlot {

    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

}
