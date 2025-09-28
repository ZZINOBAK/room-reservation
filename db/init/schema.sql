create DATABASE IF NOT EXISTS room_reservation
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE room_reservation;

create table users(
user_id int not null auto_increment,
user_name varchar(50) not null, 
primary key (user_id),
unique key uk_users_user_name (user_name)
)ENGINE=innoDB;

create table rooms (
room_id int not null auto_increment,
room_name varchar(50) not null,
user_id int not null,
location varchar(255) not null,
capacity int not null,
primary key (room_id),
constraint fk_rooms_user_id foreign key (user_id) references users(user_id)
on update restrict on delete restrict
)engine=innodb;

create TABLE reservations (
reservation_id INT NOT NULL AUTO_INCREMENT,
user_id        INT NOT NULL,
room_id        INT NOT NULL,
start_at       DATETIME NOT NULL,
end_at         DATETIME NOT NULL,
status         ENUM('BOOKED','CANCELLED') NOT NULL,
PRIMARY KEY (reservation_id),
CONSTRAINT fk_resv_user_id FOREIGN KEY (user_id)
 REFERENCES users(user_id)
ON update RESTRICT ON delete RESTRICT,
CONSTRAINT fk_resv_room_id FOREIGN KEY (room_id)
REFERENCES rooms(room_id)
ON update RESTRICT ON delete RESTRICT
) ENGINE=InnoDB;
