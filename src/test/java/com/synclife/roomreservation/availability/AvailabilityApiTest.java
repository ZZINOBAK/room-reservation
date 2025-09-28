package com.synclife.roomreservation.availability;

import com.synclife.roomreservation.AppController;
import com.synclife.roomreservation.service.AvailabilityService;
import com.synclife.roomreservation.service.ReservationService;
import com.synclife.roomreservation.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AppController.class)
@AutoConfigureMockMvc(addFilters = false)
class AvailabilityApiTest {

    @Autowired MockMvc mockMvc;

    @MockBean RoomService roomService;
    @MockBean AvailabilityService availabilityService;
    @MockBean ReservationService reservationService;

    // 뷰 순환 방지용 테스트 전용 설정 추가
    @TestConfiguration
    static class TestViewConfig {
        @Bean
        MappingJackson2JsonView mappingJackson2JsonView() {
            return new MappingJackson2JsonView();
        }
    }


    @Test
    void availability_withValidDate() throws Exception {
        String date = "2025-09-29";

        mockMvc.perform(get("/rooms").param("date", date))
                .andExpect(status().isOk());

        verify(availabilityService).getByDate(LocalDate.parse(date));
    }

    @Test
    void availability_withInvalidDate() throws Exception {
        mockMvc.perform(get("/rooms").param("date", "20250929"))
                .andExpect(status().isBadRequest());
    }

}
