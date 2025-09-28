package com.synclife.roomreservation.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationDTO {

    private final long roomId;
    private final String startAt;
    private final String endAt;
}
