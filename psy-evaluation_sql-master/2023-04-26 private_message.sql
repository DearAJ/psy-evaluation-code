-- create tables for private message

DROP TABLE IF EXISTS `user_message_profile`;
DROP TABLE IF EXISTS `private_message`;
DROP TABLE IF EXISTS `private_message_deliver`;

CREATE TABLE `user_message_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `current_private_message_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `private_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` int NOT NULL COMMENT '1,用户单对单发信\n2,预警催办\n3,任务催办\n',
  `target` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '发送的对象，单对单发送是指哪个人',
  `target_title` varchar(200) DEFAULT NULL,
  `ref_id` varchar(200) DEFAULT NULL COMMENT '预警或任务ID',
  `ref_id2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `content` text,
  `sender_username` varchar(50) NOT NULL,
  `sender_title` varchar(200) DEFAULT NULL,
  `create_time` datetime NOT NULL COMMENT '发送时间',
  `deleted` tinyint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站内信.';



CREATE TABLE `private_message_deliver` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `private_message_id` bigint NOT NULL,
  `target_username` varchar(50) NOT NULL COMMENT '接收人账号',
  `status` int NOT NULL COMMENT '0, 未读\n1, 已读',
  `deleted` tinyint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站内信发送状态';
