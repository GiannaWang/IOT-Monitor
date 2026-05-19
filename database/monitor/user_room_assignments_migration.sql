CREATE TABLE IF NOT EXISTS `user_room_assignments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `location_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_location` (`user_id`, `location_id`),
  KEY `idx_user_room_user_id` (`user_id`),
  KEY `idx_user_room_location_id` (`location_id`),
  CONSTRAINT `fk_user_room_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_room_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
);

INSERT IGNORE INTO user_room_assignments (user_id, location_id)
SELECT u.user_id, l.id
FROM users u
JOIN locations l
  ON CAST(u.room AS UNSIGNED) = l.roomnumber
WHERE u.room IS NOT NULL
  AND TRIM(u.room) <> ''
  AND u.role <> 'admin';
