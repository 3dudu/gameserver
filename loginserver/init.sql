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
-- Table structure for login_log
-- ----------------------------
-- 添加广告交易记录表
DROP TABLE IF EXISTS `advertise`;
CREATE TABLE IF NOT EXISTS `advertise` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(64) DEFAULT NULL,
  `sid` int(11) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `platform` varchar(20) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `trans_id` varchar(100) DEFAULT NULL,
  `reward_amount` bigint(20) DEFAULT NULL,
  `reward_name` varchar(100) DEFAULT NULL,
  `statu` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `advertise_trans_id` (`trans_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `login_log`;
CREATE TABLE IF NOT EXISTS `login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `sid` int(11) DEFAULT NULL COMMENT '区服ID',
  `pid` int(11) DEFAULT NULL COMMENT '角色ID',
  `channel` varchar(30) DEFAULT NULL COMMENT '渠道',
  `platform` varchar(30) DEFAULT NULL COMMENT '平台',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `reg` tinyint(4) DEFAULT NULL COMMENT '注册(0为登陆 1为注册',
  `create_date` varchar(255) DEFAULT NULL COMMENT '创建日期',
  `uid` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for login_token
-- ----------------------------
DROP TABLE IF EXISTS `login_token`;
CREATE TABLE IF NOT EXISTS `login_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `token` varchar(32) DEFAULT NULL COMMENT '32位令牌',
  `uid` bigint(20) UNSIGNED DEFAULT NULL COMMENT '用户id分布式计算',
  `sid` int(11) DEFAULT NULL COMMENT '区服ID',
  `pid` int(11) DEFAULT NULL COMMENT '角色ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间戳',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- ----------------------------
-- Table structure for pf_options
-- ----------------------------
DROP TABLE IF EXISTS `pf_options`;
CREATE TABLE `pf_options` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `key` varchar(128) DEFAULT NULL COMMENT '键(各平台接入参数)',
  `value` text COMMENT '值(json配置)',
  `close` int(2) DEFAULT NULL COMMENT '关闭该渠道充值要求客户端不再调起支付,关闭状态下订单直接返回false不允许支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE IF NOT EXISTS `player` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增',
  `uid` bigint(20) UNSIGNED DEFAULT NULL COMMENT '用户id分布式计算',
  `sid` int(11) DEFAULT NULL COMMENT 'server表id',
  `pid` bigint(20) DEFAULT NULL COMMENT 'player表id',
  `level` int(11) DEFAULT NULL COMMENT '角色等级',
  `area` int(11) DEFAULT NULL COMMENT '竞技场排名',
  `vip` int(11) DEFAULT NULL COMMENT 'vip等级',
  `money` bigint(20) DEFAULT NULL COMMENT '游戏币数量',
  `diamond` bigint(20) DEFAULT NULL,
  `battle_power` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `date_create` timestamp NULL DEFAULT NULL,
  `roleStage` varchar(20) DEFAULT NULL COMMENT '角色关卡',
  `roleRechargeAmount` float DEFAULT '0' COMMENT '角色付费总金额',
  `serverName` varchar(20) DEFAULT NULL COMMENT '区服名',
  PRIMARY KEY (`id`),
  KEY `index_uid_sid` (`uid`,`sid`),
  KEY `index_uid` (`uid`),
  KEY `player_sid` (`sid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id分布式计算',
  `name` varchar(100) DEFAULT NULL COMMENT '账号',
  `passwd` varchar(32) DEFAULT NULL COMMENT 'MD5(passwd)',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '注册时间',
  `pf_user` varchar(60) DEFAULT NULL COMMENT '平台账号(平台创建账号时需要随机创建一个 name和passwd)',
  `pf` varchar(30) DEFAULT NULL COMMENT '所属平台',
  `mail_user` varchar(50) DEFAULT NULL COMMENT '邮箱账号',
  `bind_time` timestamp NULL DEFAULT NULL COMMENT '绑定邮箱的时间',
  `channelCode` varchar(100) NOT NULL DEFAULT '0',
  `lastLoginDate` varchar(50) DEFAULT NULL,
  `idCard` varchar(20) DEFAULT '',
  `realName` varchar(20) DEFAULT '',
  `userStatus` int(2) DEFAULT NULL COMMENT '用户状态0-游客 1-非游客',
  `identety_status` int(2) DEFAULT NULL COMMENT '实名认证状态 0:认证成功,1:认证中',
  `pi` varchar(38) DEFAULT '' COMMENT '实名认证用户唯一标识',
  `loginOff` bit(1) DEFAULT b'0' COMMENT '注销状态 0-未注销 1注销',
  PRIMARY KEY (`id`),
  KEY `index_name` (`name`) USING BTREE,
  KEY `index_pf` (`pf_user`,`pf`) USING BTREE,
  KEY `index_pf_user` (`pf_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


/*
CREATE TABLE `login_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `createDate` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `platform` varchar(100) NOT NULL COMMENT '平台',
  `channelCode` varchar(100) NOT NULL DEFAULT '0' COMMENT '渠道',
  `newcount` int(11) NOT NULL DEFAULT '0' COMMENT '新增',
  `actcount` int(11) NOT NULL DEFAULT '0' COMMENT '活跃',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_1` (`createDate`,`platform`,`channelCode`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `login_total` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `createDate` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `platform` varchar(100) NOT NULL COMMENT '平台',
  `channelCode` varchar(100) NOT NULL DEFAULT '0' COMMENT '渠道',
  `day_1` int(11) NOT NULL DEFAULT '0' COMMENT '1日,其实是新增',
  `day_2` int(11) NOT NULL DEFAULT '0' COMMENT '次日,次日以后都算留存',
  `day_3` int(11) NOT NULL DEFAULT '0' COMMENT '3日',
  `day_4` int(11) NOT NULL DEFAULT '0' COMMENT '4日',
  `day_5` int(11) NOT NULL DEFAULT '0' COMMENT '5日',
  `day_6` int(11) NOT NULL DEFAULT '0' COMMENT '6日',
  `day_7` int(11) NOT NULL DEFAULT '0' COMMENT '7日',
  `day_14` int(11) NOT NULL DEFAULT '0' COMMENT '8-14天',
  `day_21` int(11) NOT NULL DEFAULT '0' COMMENT '15-21天',
  `day_30` int(11) NOT NULL DEFAULT '0' COMMENT '22-30天',
  `day_60` int(11) NOT NULL DEFAULT '0' COMMENT '31-60天',
  `day_180` int(11) NOT NULL DEFAULT '0' COMMENT '61-180天',
  `day_360` int(11) NOT NULL DEFAULT '0' COMMENT '181-360天',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_1` (`createDate`,`platform`,`channelCode`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
*/



