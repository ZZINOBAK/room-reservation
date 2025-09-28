package com.synclife.roomreservation.room;

import com.synclife.roomreservation.AppRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(AppRepository.class)
class RoomRepositoryTest {

    @Autowired
    AppRepository appRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void createRoom() {
        // given
        String roomName = "room1";
        int userId = 1;
        String location = "location";
        int capacity = 1;

        // when
        appRepository.createRoom(roomName, userId, location, capacity);

        // then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM rooms WHERE room_name = ? AND user_id = ? AND location = ? AND capacity = ?",
                Integer.class,
                roomName, userId, location, capacity
        );

        assertThat(count).isEqualTo(1);
    }
}
