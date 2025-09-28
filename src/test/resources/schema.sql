CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    user_id INT NOT NULL,
    location VARCHAR(100),
    capacity INT NOT NULL
);