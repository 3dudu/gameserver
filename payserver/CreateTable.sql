/*
Navicat MySQL Data Transfer

Source Server         : Master测试114.116.82.63 
Source Server Version : 50558
Source Host           : localhost:3306
Source Database       : GateWay

Target Server Type    : MYSQL
Target Server Version : 50558
File Encoding         : 65001

Date: 2018-08-01 10:45:02
*/

SET FOREIGN_KEY_CHECKS=0;


-- ----------------------------
-- Table structure for pay_count
-- ----------------------------
DROP TABLE IF EXISTS `pay_count`;
CREATE TABLE `pay_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createDate` varchar(20) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `channel` int(11) NOT NULL DEFAULT '0',
  `newcount` bigint(20) NOT NULL DEFAULT '0' COMMENT '今日新增付费总流水',
  `actcount` bigint(20) NOT NULL DEFAULT '0' COMMENT '活跃账户[只要不是今天注册的都算活跃]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for pay_total
-- ----------------------------
DROP TABLE IF EXISTS `pay_total`;
CREATE TABLE `pay_total` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createDate` varchar(20) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `channel` int(11) NOT NULL DEFAULT '0' COMMENT '''渠道''',
  `day_1` bigint(20) NOT NULL DEFAULT '0' COMMENT '今日新增流水',
  `day_2` bigint(20) NOT NULL DEFAULT '0' COMMENT '次日流水',
  `day_3` bigint(20) NOT NULL DEFAULT '0',
  `day_4` bigint(20) NOT NULL DEFAULT '0',
  `day_5` bigint(20) NOT NULL DEFAULT '0',
  `day_6` bigint(20) NOT NULL DEFAULT '0',
  `day_7` bigint(20) NOT NULL DEFAULT '0',
  `day_14` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户第8-14天流水都记录在此',
  `day_21` bigint(20) NOT NULL DEFAULT '0' COMMENT '第15-21天用户流水',
  `day_30` bigint(20) NOT NULL DEFAULT '0' COMMENT '第22天-30天流水记录',
  `day_60` bigint(20) NOT NULL DEFAULT '0' COMMENT '第31-60天流水统计',
  `day_180` bigint(20) NOT NULL DEFAULT '0',
  `day_360` bigint(20) NOT NULL DEFAULT '0' COMMENT '181天-360天数据统计',
  `day_other` bigint(20) NOT NULL DEFAULT '0' COMMENT '361天之后流水统计',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
