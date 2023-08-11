-- MySQL dump 10.13  Distrib 5.6.19, for Linux (x86_64)
--
-- Host: localhost    Database: loginpay
-- ------------------------------------------------------
-- Server version	5.6.19-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `pay_order`
--

DROP TABLE IF EXISTS `pay_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_order` (
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
  PRIMARY KEY (`id`),
  KEY `唯一索引` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3896464 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pay_order_syn`
--

DROP TABLE IF EXISTS `pay_order_syn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_order_syn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(32) NOT NULL,
  `sych_status` int(2) DEFAULT NULL COMMENT '同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败',
  `sych_cnt` int(11) DEFAULT NULL COMMENT '同步次数',
  `status_msg` varchar(50) DEFAULT NULL COMMENT '状态描述',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`),
  KEY `唯一索引` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pf_options`
--

DROP TABLE IF EXISTS `pf_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pf_options` (
  `id` bigint(20) NOT NULL COMMENT '自增ID',
  `key` varchar(128) DEFAULT NULL COMMENT '键()',
  `value` text COMMENT '值(json配置)',
  `close` int(2) DEFAULT NULL COMMENT '关闭该渠道充值要求客户端不再调起支付,关闭状态下订单直接返回false不允许支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `server_list`
--

DROP TABLE IF EXISTS `server_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_list` (
  `server_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '区服ID',
  `name` varchar(20) DEFAULT NULL COMMENT '区服名称',
  `ip` varchar(20) DEFAULT NULL COMMENT 'ip地址',
  `port` varchar(6) DEFAULT NULL COMMENT '端口号',
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道号',
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-10-25 11:12:48

-- 订单表添加用户id字段
alter table pay_order add user_id bigint;

-- 订单表添加渠道号
ALTER TABLE `pay_order`
ADD COLUMN `channelCode` varchar(20)  COMMENT '渠道号';

-- 添加广告交易记录表
DROP TABLE IF EXISTS `advertise`;
CREATE TABLE `advertise` (
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
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8;

-- version 1.0.0.39
-- 2020.07.24 增加内部充值沙盒充值标识
ALTER TABLE `pay_order`
ADD COLUMN `order_type` varchar(20)  COMMENT '内部充值沙盒充值标识';
-- version 1.0.0.70
-- 2022.05.06 添加返利字段
alter table `pay_order` add  column bonus  int(11)  default 0 COMMENT '返利元宝/金币/钻石数量';
alter table `pay_order` add  column ext  varchar(20)  COMMENT '透传';