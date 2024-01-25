-- add for hotline function

DROP TABLE IF EXISTS `tel_blacklist`;
DROP TABLE IF EXISTS `tel_cdr_info`;
DROP TABLE IF EXISTS `tel_common_code`;
DROP TABLE IF EXISTS `tel_contact`;
DROP TABLE IF EXISTS `tel_doc`;
DROP TABLE IF EXISTS `tel_group`;
DROP TABLE IF EXISTS `tel_note`;
DROP TABLE IF EXISTS `tel_teacher`;


CREATE TABLE `tel_blacklist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tel_group_id` bigint NOT NULL,
  `phone` varchar(20) NOT NULL,
  `create_time` datetime NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `create_username` varchar(50) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tel_cdr_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tel_group_id` bigint NOT NULL,
  `sdk_app_id` varchar(20) NOT NULL,
  `source` int NOT NULL COMMENT '数据最初来源1,网页；2，JOB',
  `process_status` int NOT NULL COMMENT '处理状态',
  `blocked` tinyint(1) DEFAULT NULL,
  `record_local_path` varchar(100) DEFAULT NULL,
  `session_id` varchar(45) DEFAULT NULL,
  `caller` varchar(45) NOT NULL,
  `callee` varchar(45) NOT NULL,
  `time` datetime DEFAULT NULL,
  `direction` tinyint NOT NULL,
  `duration` int DEFAULT NULL,
  `record_url` varchar(200) DEFAULT NULL,
  `seat_user_id` varchar(100) DEFAULT NULL,
  `end_status` int DEFAULT NULL,
  `caller_location` varchar(100) DEFAULT NULL,
  `ivr_duration` int DEFAULT NULL,
  `ring_timestamp` datetime DEFAULT NULL,
  `accept_timestamp` datetime DEFAULT NULL,
  `ended_timestamp` datetime DEFAULT NULL,
  `ivr_key_pressed` varchar(100) DEFAULT NULL,
  `hung_up_side` varchar(20) DEFAULT NULL,
  `skill_group_id` int DEFAULT NULL,
  `end_status_string` varchar(45) DEFAULT NULL,
  `start_timestamp` datetime DEFAULT NULL,
  `queued_timestamp` datetime DEFAULT NULL,
  `post_ivr_key_pressed` varchar(100) DEFAULT NULL,
  `protected_caller` varchar(45) DEFAULT NULL,
  `protected_callee` varchar(45) DEFAULT NULL,
  `asr_url` varchar(200) DEFAULT NULL,
  `asr_local` varchar(200) DEFAULT NULL,
  `call_in_count` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `tel_common_code` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `page_name` varchar(45) NOT NULL,
  `group_name` varchar(45) NOT NULL,
  `title` varchar(100) NOT NULL,
  `code` varchar(45) DEFAULT NULL,
  `sub_title` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `priority` int NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tel_contact` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tel_group_id` bigint NOT NULL,
  `phone` varchar(20) NOT NULL COMMENT '电话号码',
  `role` varchar(10) DEFAULT NULL COMMENT '身份',
  `name` varchar(45) DEFAULT NULL COMMENT '姓名',
  `id_card` varchar(45) DEFAULT NULL COMMENT '身份证',
  `age` int DEFAULT NULL COMMENT '年龄',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `school_name` varchar(200) DEFAULT NULL COMMENT '学校名',
  `study_period` varchar(20) DEFAULT NULL COMMENT '学段',
  `education` varchar(45) DEFAULT NULL COMMENT '学历',
  `school_grade` varchar(45) DEFAULT NULL COMMENT '年级',
  `school_class` varchar(45) DEFAULT NULL COMMENT '班级',
  `province` varchar(30) DEFAULT NULL,
  `city` varchar(20) DEFAULT NULL,
  `county` varchar(20) DEFAULT NULL,
  `single_child` varchar(10) DEFAULT NULL COMMENT '是否独生子女',
  `birth_order` varchar(10) DEFAULT NULL COMMENT '家中排行',
  `address` varchar(300) DEFAULT NULL,
  `parental_marriage_state` varchar(20) DEFAULT NULL COMMENT '父母婚姻状况',
  `last_question_type` varchar(50) DEFAULT NULL,
  `last_question_level` varchar(50) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `tel_doc` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tel_group_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `create_time` datetime NOT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `priority` int NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `idx_ft_td` (`title`,`content`) /*!50100 WITH PARSER `ngram` */ 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tel_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL COMMENT '类型，对应管理员level',
  `province` varchar(20) NOT NULL,
  `city` varchar(20) DEFAULT NULL,
  `county` varchar(20) DEFAULT NULL,
  `school` varchar(255) DEFAULT NULL,
  `admin` varchar(20) DEFAULT NULL COMMENT '管理员对应账号',
  `username` varchar(20) DEFAULT NULL COMMENT '接线员对应账号',
  `secret_id` varchar(100) DEFAULT NULL,
  `secret_key` varchar(100) DEFAULT NULL,
  `sdk_app_id` varchar(20) DEFAULT NULL,
  `seat_account` varchar(45) DEFAULT NULL COMMENT '第三方系统坐席账号',
  `cdr_last_fetch_timestamp` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tel_note` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tel_group_id` bigint NOT NULL,
  `tel_cdr_id` bigint NOT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `teacher_id` bigint DEFAULT NULL,
  `teacher_name` varchar(50) DEFAULT NULL,
  `question_type` varchar(50) DEFAULT NULL,
  `question_level` varchar(50) DEFAULT NULL,
  `question` text,
  `answer` text,
  `remark` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tel_teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tel_group_id` bigint NOT NULL,
  `real_name` varchar(20) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `create_by` varchar(50) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理热线值班老师表';
