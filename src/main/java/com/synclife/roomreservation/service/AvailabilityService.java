package com.synclife.roomreservation.service;

import com.synclife.roomreservation.AppRepository;
import com.synclife.roomreservation.DTO.AvailabillityDTO;
import com.synclife.roomreservation.DTO.TimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AvailabilityService {
    private final AppRepository appRepository;

    public void getByDate(LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime nextDayStart = date.plusDays(1).atStartOfDay();

        List<Map<String, Object>> rooms = appRepository.findAllRooms();
        List<Map<String, Object>> reservations = appRepository.findReservationByDate(dayStart, nextDayStart);

//        new LinkedHashMap<>();
        Map<Long, List<TimeSlot>> byRoom = new HashMap<>();

        for (Map<String, Object> r : reservations) {
            long rid = ((Number) r.get("room_id")).longValue();
            LocalDateTime s = ((Timestamp) r.get("start_at")).toLocalDateTime();
            LocalDateTime e = ((Timestamp) r.get("end_at")).toLocalDateTime();

            List<TimeSlot> slotList = byRoom.computeIfAbsent(rid, k -> new ArrayList<>());
            slotList.add(new TimeSlot(s, e));
        }

        List<AvailabillityDTO> result = new ArrayList<>();
        for (Map<String, Object> room : rooms) {
            long roomId = ((Number) room.get("room_id")).longValue();
            String roomName = (String) room.get("room_name");

            List<TimeSlot> reservationList = byRoom.getOrDefault(roomId, List.of())
                    .stream()
                    .sorted(Comparator.comparing(TimeSlot::getStartAt))
                    .toList();

            List<TimeSlot> free = toFree(reservationList, dayStart, nextDayStart);

            result.add(new AvailabillityDTO(roomId, roomName, reservationList, free));
        }

    }

    private List<TimeSlot> toFree(List<TimeSlot> reservationList, LocalDateTime start, LocalDateTime end) {
        List<TimeSlot> free = new ArrayList<>();
        LocalDateTime cur = start;

        for (TimeSlot reservation : reservationList) {
            if (cur.isBefore(reservation.getStartAt())) {
                free.add(new TimeSlot(cur, reservation.getStartAt()));
            }
            cur = reservation.getEndAt().isAfter(cur) ? reservation.getEndAt() : cur;
        }

        if (cur.isBefore(end)) {
            free.add(new TimeSlot(cur, end));
        }

        return free;
    }
}
