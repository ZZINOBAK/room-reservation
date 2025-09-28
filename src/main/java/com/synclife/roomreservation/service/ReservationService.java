package com.synclife.roomreservation.service;

import com.synclife.roomreservation.AppRepository;
import com.synclife.roomreservation.DTO.ReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final AppRepository appRepository;

    @Transactional
    public void createReservation(long userId, ReservationDTO req) {
        LocalDateTime start = LocalDateTime.parse(req.getStartAt());
        LocalDateTime end = LocalDateTime.parse(req.getEndAt());

        if (!end.isAfter(start)) {
            // 시간 오류 예외
        }

        appRepository.createReservation(userId, req.getRoomId(), start, end);
    }

    public void cancelReservationAsAdmin(int reservationId) {
        int updated = appRepository.cancelReservation(reservationId);
        if (updated == 0) {
            throw new IllegalStateException("cannot cancel (not found or not cancelable)");
        }
    }

    public void cancelReservationAsUser(int reservationId, long requesterId) throws AccessDeniedException {
        // 예약 존재/소유자 확인
        Long roomOwnerId = appRepository.findRoomOwnerId(reservationId);
        Long reservationUserId = appRepository.findReservationUserId(reservationId);

        boolean allowed = (reservationUserId != null && reservationUserId == requesterId)
                || (roomOwnerId != null && roomOwnerId == requesterId);
        if (!allowed) {
            throw new AccessDeniedException("no permission");
        }

      appRepository.cancelReservation(reservationId);

    }
}
