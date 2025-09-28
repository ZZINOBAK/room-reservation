package com.synclife.roomreservation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AppRepository {

//    private final AppMapper appMapper;
    private final JdbcTemplate jdbcTemplate;

    public void createRoom(String roomName, long userId, String location, int capacity) {
        String insertRoomSql = "INSERT INTO rooms(room_name, user_id, location, capacity) " +
                "VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertRoomSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, roomName);
            ps.setLong(2, userId);
            ps.setString(3, location);
            ps.setInt(4, capacity);
            return ps;
        }, keyHolder);


    }

    public List<Map<String, Object>> findAllRooms() {
        return jdbcTemplate.queryForList("SELECT room_id, room_name FROM rooms");
    }

    public List<Map<String, Object>> findReservationByDate(LocalDateTime dayStart, LocalDateTime nextDayStart) {
        String sql = """
        SELECT reservation_id, room_id, user_id, start_at, end_at, status
        FROM reservations
        WHERE start_at < ? AND end_at > ?
        ORDER BY room_id, start_at
    """;

        return jdbcTemplate.queryForList(
                sql,
                Timestamp.valueOf(nextDayStart),
                Timestamp.valueOf(dayStart)
        );
    }

    @Transactional
    public void createReservation(long userId, long roomId, LocalDateTime start, LocalDateTime end) {
        String rockSql = "SELECT room_id FROM rooms WHERE room_id=? FOR UPDATE";
        jdbcTemplate.query(
                (Connection con) -> {
                    PreparedStatement ps = con.prepareStatement(rockSql);
                    ps.setLong(1, roomId);
                    return ps;
                },
                (ResultSet rs) -> rs.next()
        );

        String checkSql = """
        SELECT reservation_id FROM reservations
        WHERE room_id=? AND status IN ('BOOKED')
          AND start_at < ? AND end_at > ?
        FOR UPDATE
    """;

        boolean overlap = jdbcTemplate.query(
                (Connection con) -> {
                    PreparedStatement ps = con.prepareStatement(checkSql);
                    ps.setLong(1, roomId);
                    ps.setTimestamp(2, Timestamp.valueOf(end));
                    ps.setTimestamp(3, Timestamp.valueOf(start));
                    return  ps;
                },
                (ResultSet rs) -> rs.next()
        );

        if (overlap) {
            throw new IllegalStateException("겹치는 예약이 있습니다.");
        }

        String insertReservationSql = """
        INSERT INTO reservations(user_id, room_id, start_at, end_at, status)
        VALUES (?,?,?,?, 'BOOKED')
    """;

        GeneratedKeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertReservationSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setLong(2, roomId);
            ps.setTimestamp(3, Timestamp.valueOf(start));
            ps.setTimestamp(4, Timestamp.valueOf(end));
            return ps;
        }, kh);
    }

    public int cancelReservation(int reservationId) {

        String sql = """
            UPDATE reservations
            SET status = 'CANCELED'
            WHERE reservation_id = ?
              AND status = 'BOOKED'
              AND start_at > NOW()
            """;

        return jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, reservationId);
            return ps;
        });
    }

    public Long findRoomOwnerId(int reservationId) {
        String sql = """
        SELECT rm.owner_id
        FROM reservations rv
        JOIN rooms rm ON rv.room_id = rm.room_id
        WHERE rv.reservation_id = ?
        """;
        List<Long> result = jdbcTemplate.query(
                sql,
                ps -> ps.setInt(1, reservationId),
                (rs, rowNum) -> rs.getLong("owner_id")
        );
        return result.isEmpty() ? null : result.get(0);
    }

    public Long findReservationUserId(int reservationId) {
        String sql = "SELECT user_id FROM reservations WHERE reservation_id = ?";
        List<Long> result = jdbcTemplate.query(
                sql,
                ps -> ps.setInt(1, reservationId),
                (rs, rowNum) -> rs.getLong("user_id")
        );
        return result.isEmpty() ? null : result.get(0);
    }


}
