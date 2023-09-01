-- phpMyAdmin SQL Dump
-- version 4.9.7
-- https://www.phpmyadmin.net/
--
-- 主机： 192.168.1.221:4306
-- 生成日期： 2023-08-14 09:19:32
-- 服务器版本： 5.7.40-log
-- PHP 版本： 7.3.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- 数据库： `game_loginpay`
--

-- --------------------------------------------------------

--
-- 表的结构 `advertise`
--

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `pay_count`
--

DROP TABLE IF EXISTS `pay_count`;
CREATE TABLE IF NOT EXISTS `pay_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createDate` varchar(20) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `channel` int(11) NOT NULL DEFAULT '0',
  `newcount` bigint(20) NOT NULL DEFAULT '0' COMMENT '今日新增付费总流水',
  `actcount` bigint(20) NOT NULL DEFAULT '0' COMMENT '活跃账户[只要不是今天注册的都算活跃]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `pay_order`
--

DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE IF NOT EXISTS `pay_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(42) NOT NULL COMMENT 'UUID+serverId+playerId',
  `pay_price` float DEFAULT NULL COMMENT '订单金额(该值验证后写入)',
  `units` varchar(10) DEFAULT NULL COMMENT '货比单位',
  `pro_idx` int(2) DEFAULT NULL COMMENT '档位',
  `sid` int(11) DEFAULT NULL COMMENT '服务器ID',
  `pid` int(11) DEFAULT NULL COMMENT '角色ID',
  `status` int(2) DEFAULT NULL COMMENT '状态码',
  `platform` varchar(20) DEFAULT NULL COMMENT '平台<xuegaoSDK, anysdk,mycard...>',
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道  对于anySDK  那么就是渠道编号,\r\n对于非AnySDK 这个位置就null',
  `source` text COMMENT 'AnySdk 使用字段  :\r\n resource : 渠道服务器通知 AnySDK 时请求的参数',
  `third_trade_no` varchar(64) DEFAULT NULL COMMENT '第三方订单号; anysdk 接入是 就是 真实支付方服务器订单Id, 对于非anySDk 就是 支付平台订单号',
  `create_time` bigint(20) DEFAULT NULL COMMENT '订单创建时间',
  `finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',
  `user_id` bigint(20) DEFAULT NULL,
  `channelCode` varchar(20) DEFAULT NULL COMMENT '渠道号',
  `order_type` varchar(20) DEFAULT NULL COMMENT '内部充值沙盒充值标识',
  `bonus` int(11) DEFAULT '0' COMMENT '返利元宝/金币/钻石数量',
  `ext` varchar(20) DEFAULT NULL COMMENT '透传',
  PRIMARY KEY (`id`),
  KEY `唯一索引` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `pay_order_syn`
--

DROP TABLE IF EXISTS `pay_order_syn`;
CREATE TABLE IF NOT EXISTS `pay_order_syn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(32) NOT NULL,
  `sych_status` int(2) DEFAULT NULL COMMENT '同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败',
  `sych_cnt` int(11) DEFAULT NULL COMMENT '同步次数',
  `status_msg` varchar(50) DEFAULT NULL COMMENT '状态描述',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`),
  KEY `唯一索引` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `pay_total`
--

DROP TABLE IF EXISTS `pay_total`;
CREATE TABLE IF NOT EXISTS `pay_total` (
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

-- --------------------------------------------------------

--
-- 表的结构 `pf_options`
--

DROP TABLE IF EXISTS `pf_options`;
CREATE TABLE IF NOT EXISTS `pf_options` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `key` varchar(128) DEFAULT NULL COMMENT '键(各平台接入参数)',
  `value` text COMMENT '值(json配置)',
  `close` int(2) DEFAULT NULL COMMENT '关闭该渠道充值要求客户端不再调起支付,关闭状态下订单直接返回false不允许支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `server_list`
--

DROP TABLE IF EXISTS `server_list`;
CREATE TABLE IF NOT EXISTS `server_list` (
  `server_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '区服ID',
  `name` varchar(20) DEFAULT NULL COMMENT '区服名称',
  `ip` varchar(20) DEFAULT NULL COMMENT 'ip地址',
  `port` varchar(6) DEFAULT NULL COMMENT '端口号',
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道号',
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
COMMIT;
