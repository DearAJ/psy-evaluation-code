ALTER TABLE `school_classes` 
ADD COLUMN `school_year` VARCHAR(10) NULL COMMENT '学年' AFTER `entrance_year`,
ADD COLUMN `archived` TINYINT(1) NULL AFTER `school_year`;

ALTER TABLE `students` 
ADD COLUMN `school_year` VARCHAR(10) NULL COMMENT '学年' AFTER `entrance_year`,
ADD COLUMN `archived` TINYINT(1) NULL AFTER `school_year`;

ALTER TABLE `task_user` 
ADD COLUMN `school_year` VARCHAR(10) NULL COMMENT '学年' AFTER `user_id`,
ADD COLUMN `archived` TINYINT(1) NULL AFTER `school_year`;

ALTER TABLE `task` 
ADD COLUMN `school_year` VARCHAR(10) NULL COMMENT '学年' AFTER `id`,
ADD COLUMN `archived` TINYINT(1) NULL AFTER `school_year`;

ALTER TABLE `task_school` 
ADD COLUMN `school_year` VARCHAR(10) NULL COMMENT '学年' AFTER `task_id`,
ADD COLUMN `archived` TINYINT(1) NULL AFTER `school_year`;


update school_classes set school_year='2022', archived=0 where id > 0;
update students set school_year='2022', archived=0 where id > 0;
update task_user set school_year='2022', archived=0 where id > 0;
update task set school_year='2022', archived=0 where id > 0;
update task_school set school_year='2022', archived=0 where id > 0;

ALTER TABLE `school_classes` 
CHANGE COLUMN `school_year` `school_year` VARCHAR(10) NOT NULL COMMENT '学年',
CHANGE COLUMN `archived` `archived` TINYINT(1) NOT NULL COMMENT '已存档' ;

ALTER TABLE `students` 
CHANGE COLUMN `school_year` `school_year` VARCHAR(10) NOT NULL COMMENT '学年' ,
CHANGE COLUMN `archived` `archived` TINYINT(1) NOT NULL COMMENT '已存档' ;

ALTER TABLE `task_user` 
CHANGE COLUMN `school_year` `school_year` VARCHAR(10) NOT NULL COMMENT '学年' ,
CHANGE COLUMN `archived` `archived` TINYINT(1) NOT NULL COMMENT '已存档' ;

ALTER TABLE `task` 
CHANGE COLUMN `school_year` `school_year` VARCHAR(10) NOT NULL COMMENT '学年' ,
CHANGE COLUMN `archived` `archived` TINYINT(1) NOT NULL COMMENT '已存档' ;

ALTER TABLE `task_school` 
CHANGE COLUMN `school_year` `school_year` VARCHAR(10) NOT NULL COMMENT '学年' ,
CHANGE COLUMN `archived` `archived` TINYINT(1) NOT NULL COMMENT '已存档' ;


