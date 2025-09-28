package com.synclife.roomreservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synclife.roomreservation.service.AvailabilityService;
import com.synclife.roomreservation.service.CreateService;
import com.synclife.roomreservation.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AppController에 대한 슬라이스 테스트
 * - Service 들은 MockBean으로 대체
 */
@WebMvcTest(AppController.class)
class ControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    // 컨트롤러가 의존하는 서비스들을 목으로 대체
    @MockBean
    CreateService createService;
    @MockBean
    AvailabilityService availabilityService;
    @MockBean
    ReservationService reservationService;

    // --- 테스트용 DTO 더미 (실제 프로젝트의 DTO 패키지/필드에 맞추세요) ---
    static class RoomDTO {
        public String name;
        public int capacity;
        public RoomDTO() {}
        RoomDTO(String name, int capacity) { this.name = name; this.capacity = capacity; }
    }
    static class ReservationDTO {
        public long roomId;
        public String start; // 예: "2025-10-01T10:00:00"
        public String end;   // 예: "2025-10-01T12:00:00"
        public ReservationDTO() {}
        ReservationDTO(long roomId, String start, String end) {
            this.roomId = roomId; this.start = start; this.end = end;
        }
    }
    // --------------------------------------------------------------------

    @Nested
    @DisplayName("POST /rooms (방 생성)")
    class CreateRoomTests {

        @Test
        @DisplayName("관리자 토큰이 아니면 403과 '관리자 전용' 반환")
        void createRoom_forbidden_when_not_admin() throws Exception {
            RoomDTO req = new RoomDTO("회의실A", 6);

            mockMvc.perform(post("/rooms")
                            .header("Authorization", "user-token-10")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("관리자 전용"));

            then(createService).should(times(0)).createRoom(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        }

        @Test
        @DisplayName("관리자 토큰이면 201과 '방 등록 완료' 반환, 서비스 호출 확인")
        void createRoom_created_when_admin() throws Exception {
            RoomDTO req = new RoomDTO("회의실A", 6);

            mockMvc.perform(post("/rooms")
                            .header("Authorization", "admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("방 등록 완료"));

            then(createService).should(times(1)).createRoom(1L, ArgumentMatchers.any(com.synclife.roomreservation.DTO.RoomDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /romms (가용성 조회)")
    class AvailabilityTests {

        @Test
        @DisplayName("date 파라미터로 전달된 날짜로 서비스 호출")
        void availability_calls_service_with_date() throws Exception {
            String date = "2025-09-28";

            mockMvc.perform(get("/romms").param("date", date))
                    .andExpect(status().isOk()); // 컨트롤러가 void라 기본 200

            then(availabilityService).should(times(1))
                    .getByDate(LocalDate.parse(date));
        }
    }

    @Nested
    @DisplayName("POST /reservations (예약 생성)")
    class CreateReservationTests {

        @Test
        @DisplayName("헤더 token의 userId를 받아 201과 '방 예약 완료' 반환")
        void createReservation_created() throws Exception {
            ReservationDTO req = new ReservationDTO(101L, "2025-10-01T10:00:00", "2025-10-01T12:00:00");

            mockMvc.perform(post("/reservations")
                            .header("token", "10") // 컨트롤러 시그니처가 long userId를 바로 받음
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("방 예약 완료"));

            then(reservationService).should(times(1))
                    .createReservation(10L, ArgumentMatchers.any(com.synclife.roomreservation.DTO.ReservationDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /reservations (예약 취소)")
    class CancelReservationTests {

        // ⚠️ 컨트롤러의 cancelReservation 메서드에 @DeleteMapping("/reservations") 를 추가해야 이 테스트가 동작합니다.

        @Test
        @DisplayName("관리자 토큰이면 204, admin용 서비스 호출")
        void cancel_admin_token() throws Exception {
            mockMvc.perform(delete("/reservations")
                            .header("Authorization", "admin-token")
                            .param("reservationId", "1"))
                    .andExpect(status().isNoContent());

            then(reservationService).should(times(1)).cancelReservationAsAdmin(1);
            // admin 경로에서는 user 취소 서비스는 호출되지 않아야 함
            then(reservationService).should(times(0)).cancelReservationAsUser(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        }

        @Test
        @DisplayName("user-token-{id} 이면 204, user용 서비스 호출")
        void cancel_user_token() throws Exception {
            mockMvc.perform(delete("/reservations")
                            .header("Authorization", "user-token-42")
                            .param("reservationId", "7"))
                    .andExpect(status().isNoContent());

            then(reservationService).should(times(1)).cancelReservationAsUser(7, 42L);
        }

        @Test
        @DisplayName("잘못된 토큰이면 400/403 등으로 처리하도록 기대(현재 컨트롤러 로직 기준 500 가능)")
        void cancel_invalid_token() throws Exception {
            // 현재 컨트롤러는 유효성/예외 처리가 부족해서 NumberFormatException 가능
            // 과제 요구에 맞게 전역 예외 핸들러로 400/403 매핑을 권장합니다.
            mockMvc.perform(delete("/reservations")
                            .header("Authorization", "abcde")
                            .param("reservationId", "7"))
                    .andExpect(status().is4xxClientError());
        }
    }
}