-- version 1.0.0.17
-- 2019.09.28 还原被删除的sql
ALTER TABLE `user` CHANGE id id bigint(20) UNSIGNED NOT NULL COMMENT '用户id分布式计算';
ALTER TABLE `player` CHANGE uid uid bigint(20) UNSIGNED DEFAULT  NULL COMMENT '用户id分布式计算';
ALTER TABLE `login_token` CHANGE uid uid bigint(20) UNSIGNED DEFAULT  NULL COMMENT '用户id分布式计算';

-- version 1.0.0.24
-- 2020.03.04 用户表添加实名认证字段
ALTER TABLE `user`
ADD COLUMN `idCard` varchar(20) DEFAULT '',
ADD COLUMN `age` int(11) DEFAULT '0',
ADD COLUMN `realName` varchar(20) DEFAULT '';

-- version 1.0.0.25
-- 2020.03.19 用户表添加字游客非游客标识
ALTER TABLE `user`
ADD COLUMN `userStatus` int(2) DEFAULT NULL COMMENT '用户状态0-游客 1-非游客';

-- version 1.0.0.26
-- 2020.06.04 用户表删除多余的字段
ALTER TABLE `user` DROP COLUMN `age`;

-- version 1.0.0.37
-- 2021.04.20 增加实名认证字段
ALTER TABLE `user`
ADD COLUMN `identety_status` int(2) DEFAULT NULL COMMENT '实名认证状态 0:认证成功,1:认证中',
ADD COLUMN `pi` varchar(38) DEFAULT '' COMMENT '实名认证用户唯一标识';

-- version 1.0.0.41
-- 2021.05.28 战力改为长整型
ALTER TABLE player MODIFY COLUMN  battle_power  BIGINT(20) DEFAULT NULL;

-- version 1.0.0.45
-- 2021.08.10 配置表修改value为mediumtext
alter table pf_options modify column `value` mediumtext;

-- version 1.0.0.50
-- 2021.11.15 优化慢sql
ALTER TABLE `player` ADD INDEX player_sid ( `sid` );
ALTER TABLE `advertise` ADD INDEX advertise_trans_id ( `trans_id` );
DELETE  FROM `advertise` where create_time <  UNIX_TIMESTAMP( DATE_SUB(curdate(),INTERVAL 90 DAY)) *1000;
-- version 1.0.0.58
-- 2022.05.03 添加注销标识符
alter table `user` add  column loginOff  bit  default 0 comment '注销状态 0-未注销 1注销';
-- version 1.0.0.61
-- 2022.06.29 player表添加角色关卡 总金额 区服名 创建pid索引
alter table `player` modify column diamond BIGINT(20) DEFAULT NULL;
alter table  `player` add column roleStage varchar(20) DEFAULT NULL comment '角色关卡';
alter table  `player` add column roleRechargeAmount float(0) DEFAULT 0 comment '角色付费总金额';
alter table  `player` add column serverName varchar(20) DEFAULT NULL comment '区服名';