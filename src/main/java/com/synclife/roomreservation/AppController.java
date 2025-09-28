package com.synclife.roomreservation;

import com.synclife.roomreservation.DTO.ReservationDTO;
import com.synclife.roomreservation.DTO.RoomDTO;
import com.synclife.roomreservation.service.AvailabilityService;
import com.synclife.roomreservation.service.RoomService;
import com.synclife.roomreservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

@RequiredArgsConstructor
@Controller
public class AppController {
    private final RoomService createService;
    private final AvailabilityService availabilityService;
    private final ReservationService reservationService;

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestHeader(value = "Authorization", required = false) String token,
                                        @RequestBody RoomDTO req) {

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(401).body("인증 필요");
        }

        if (!"admin-token".equals(token)) {
            return ResponseEntity.status(403).body("관리자 전용");
        }
        createService.createRoom(1, req);

        return ResponseEntity.status(201).body("방 등록 완료");
    }

    @GetMapping("/rooms")
    public void availability(@RequestParam String date) {
        availabilityService.getByDate(LocalDate.parse(date));
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@RequestParam long userId,
                                               @RequestBody ReservationDTO req) {
        reservationService.createReservation(userId, req);
        return ResponseEntity.status(201).body("방 예약 완료");

    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<?> cancelReservation(
            @RequestHeader("Authorization") String token,
            @RequestParam int reservationId) throws AccessDeniedException {

        if ("admin-token".equals(token)) {
            reservationService.cancelReservationAsAdmin(reservationId);
        }

        long requesterId;
        requesterId = Long.parseLong(token.substring("user-token-".length()));


        reservationService.cancelReservationAsUser(reservationId, requesterId);


        return ResponseEntity.noContent().build();
    }
}
