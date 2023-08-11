-- 2018.12.28

CREATE TABLE `jactivitygift` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `targets` text,
  `title` varchar(255) DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `useType` int(11) DEFAULT NULL,
  `coin` int(11) NOT NULL,
  `dollar` int(11) NOT NULL,
  `merit` int(11) NOT NULL,
  `number` int(11) NOT NULL,
  `awardStr` varchar(255) DEFAULT NULL,
  `useLimit` bit(1) NOT NULL,
  `selectedTargetsList` varchar(255) DEFAULT NULL,
  `cdKeyList` text,
  `diffType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `jactivitygiftkey` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `targets` text,
  `title` varchar(255) DEFAULT NULL,
  `cdKey` longtext NOT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `startTime` varchar(20) NOT NULL,
  `passTime` varchar(20) NOT NULL,
  `disabled` varchar(20) DEFAULT NULL,
  `timesLimit` int(11) NOT NULL,
  `talkType` int(11) NOT NULL,
  `coin` int(11) NOT NULL,
  `dollar` int(11) NOT NULL,
  `merit` int(11) NOT NULL,
  `awardStr` varchar(255) DEFAULT NULL,
  `selectedTargetsList` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `jkeyuse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cdKey` varchar(255) DEFAULT NULL,
  `playerId` bigint(20) NOT NULL,
  `serverId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

alter table jkeyuse add `useType` int(11) DEFAULT NULL;
alter table jkeyuse add `diffType` int(11) DEFAULT NULL;



-- 2019.03.13
-- version 1.0.0.6
alter table jcsmessage add index sidpid (`serverId`,`playerId`);



-- 2019.03.25
-- version 1.0.0.7
CREATE TABLE `help_sys` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(30) DEFAULT NULL,
  `create_time` varchar(30) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `context` text COMMENT '内容',
  `diff_type` int(5) DEFAULT NULL COMMENT '1：官方  2：攻略',
  `zan` int(10) DEFAULT NULL COMMENT '点赞数',
  `sign_type` int(5) DEFAULT NULL COMMENT '1：热门攻略  2：新手推荐  3：高玩攻略...',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `help_sys_sign` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `sign_name` varchar(10) DEFAULT NULL COMMENT '类型名字：热门攻略、新手推荐、高玩攻略...',
  `sign_type` int(5) DEFAULT NULL COMMENT '1：热门攻略  2：新手推荐  3：高玩攻略...',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `help_sys_sign` VALUES ('1', '热门攻略', 1);
INSERT INTO `help_sys_sign` VALUES ('2', '新手推荐', 2);
INSERT INTO `help_sys_sign` VALUES ('3', '高玩攻略', 3);

CREATE TABLE `help_sys_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `diff_name` varchar(10) DEFAULT NULL COMMENT '类型名字：官方、攻略',
  `diff_type` int(5) DEFAULT NULL COMMENT '1：官方  2：攻略',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `help_sys_type` VALUES ('1', '官方', 1);
INSERT INTO `help_sys_type` VALUES ('2', '攻略', 2);



-- 2019.03.26
-- version 1.0.0.8
CREATE TABLE `main_tain_desc_time` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `expect_start_time` varchar(20) DEFAULT NULL COMMENT '预计维护开始时间',
  `expect_end_time` varchar(20) DEFAULT NULL COMMENT '预计维护结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table all_srv_mail add `status` varchar(5) DEFAULT NULL COMMENT '标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送}';
alter table sel_srv_mail add `status` varchar(5) DEFAULT NULL COMMENT '标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送}';
alter table role_mail add `status` varchar(5) DEFAULT NULL COMMENT '标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送}';



-- 2019.04.23
-- version 1.0.0.9
alter table slave_nodes add (`limit` int(10) default '0' COMMENT '限制在此服务器开的区服数量' not null);



-- 2019.05.17
-- version 1.0.0.13
CREATE TABLE `auto_open_server` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `key` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `status` varchar(1) DEFAULT '1' COMMENT '自动开服开关：0:关闭，1:开启',
  `type` varchar(10) DEFAULT NULL COMMENT '类型，1:代表自动开服',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table server_list add `is_success` int(1) DEFAULT NULL COMMENT '标示开服是否成功';
-- 标记之前已经开过的区服为1，表示都开服成功了
update server_list set `is_success`=1;


-- 2019.05.21
-- version 1.0.0.15
-- ----------------------------
-- Table structure for mconfigure
-- ----------------------------
CREATE TABLE `mconfigure` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `key` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `mconfigure` VALUES ('1', 'vip.qq.status', '0');
INSERT INTO `mconfigure` VALUES ('2', 'vip.qq.money', '1000');
INSERT INTO `mconfigure` VALUES ('3', 'weixin.status', '0');
INSERT INTO `mconfigure` VALUES ('4', 'weixin.pnum', '');


-- ----------------------------
-- Table structure for jvipqq
-- ----------------------------
CREATE TABLE `jvipqq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `qq` bigint(20) DEFAULT NULL,
  `times` int(10) DEFAULT '0' COMMENT '每个qq号分配的次数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_qq` (`qq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for jvipqq_recode
-- ----------------------------
CREATE TABLE `jvipqq_recode` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `qid` bigint(20) DEFAULT NULL COMMENT '对应jvipqq表的id',
  `sid` bigint(20) DEFAULT NULL,
  `pid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 2019.06.17
-- version 1.0.0.18
alter table server_list add (`is_auto` int(1) default '0' COMMENT '开服类型；0：手动 1：自动' not null);

CREATE TABLE `memail` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `email` varchar(30) DEFAULT NULL,
  `type` int(15) DEFAULT NULL COMMENT '类型：1:自动开服',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `mconfigure` VALUES ('5', 'auto.openserver.tips.status', '0');
INSERT INTO `mconfigure` VALUES ('6', 'auto.openserver.tips.slimit', '15');

alter table jvipqq_recode add index index_sidpid (`sid`,`pid`);


-- 2019.06.24
-- version 1.0.0.19
alter table all_srv_mail add `channel` varchar(50) DEFAULT NULL COMMENT '渠道';

alter table main_tain add `selectedChannel` text DEFAULT NULL COMMENT '渠道';
alter table main_tain add `selectedSlave` text DEFAULT NULL COMMENT 'slave节点';
alter table main_tain add `mainType` int(5) DEFAULT NULL COMMENT '维护方式：0:默认；1:slave节点；2:channel';


-- 2019.07.19
-- version 1.0.0.19-2
alter table server_list add `is_suggest` int(1) DEFAULT '0' COMMENT '是否是推荐服：0:不是；1:是';



-- 2019.07.21
-- version 1.0.0.20
alter table mgroup add `has_channel` text DEFAULT NULL COMMENT '渠道授权';

alter table all_srv_mail modify `channel` varchar(255) DEFAULT NULL COMMENT '渠道';
alter table all_srv_mail add `mailType` int(1) DEFAULT NULL COMMENT '邮件方式：0:channel；1:slave节点';
alter table all_srv_mail add `slave` varchar(255) DEFAULT NULL COMMENT 'slave节点';

-- 2019.08.19
-- version 1.0.0.23
alter table jvipqq modify `qq` VARCHAR(30) NULL DEFAULT NULL ;

CREATE TABLE `jvipqq_wx_config` (
    `id` int(10) NOT NULL AUTO_INCREMENT,
    `qq_status` int(1) DEFAULT NULL COMMENT 'qq开关，0:关闭 1:开启',
    `qq_money` int(10) DEFAULT NULL COMMENT 'qq 累计充值',
    `wx_status` int(1) DEFAULT NULL COMMENT '微信开关，0:关闭 1:开启',
    `wx_pnum` varchar(30) DEFAULT NULL COMMENT '微信公众号名字',
    `channel` varchar(20) DEFAULT NULL COMMENT '渠道',
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_unique_channel` (`channel`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table jvipqq add `channel` varchar(20) DEFAULT NULL COMMENT '渠道';

alter table jvipqq_recode add `channel` varchar(20) DEFAULT NULL COMMENT '渠道';


-- 2019.09.17
-- version 1.0.0.25
ALTER TABLE jvipqq DROP INDEX `unique_qq`;



-- 2019.09.23
-- version 1.0.0.26
alter table server_list add `wudaogroup` int(11) DEFAULT '0' COMMENT '武道会分组';


-- 2019.10.10
-- version 1.0.0.27
alter table jcsmessage_info add index sidpid (`serverId`,`playerId`);
alter table jcsmessage_process add index sidpid (`serverId`,`playerId`);
alter table jcsmessage_status add index sidpid (`serverId`,`playerId`);

-- 2019.10.28
-- version 1.0.0.28
CREATE TABLE `jfacebook` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `yuanbao` bigint(20) DEFAULT NULL COMMENT '元宝数量',
  `likenum` bigint(20) DEFAULT NULL COMMENT '点赞数',
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 2020.02.12
-- version 1.0.0.31
ALTER TABLE pf_options MODIFY COLUMN `value`  mediumtext COMMENT '值(json配置)';

-- 2020.08.26
alter table `notice` add `type` int(1) DEFAULT 0 COMMENT '0：文本，1：图片';
alter table `notice` add `isNew` int(1) DEFAULT 0 COMMENT '0：添加new图标，1：不添加new图标';
alter table `notice` add `isUnfold` int(1) DEFAULT 0 COMMENT '0：不展开，1：展开';
alter table `notice` add `sort` int(6) DEFAULT 0 COMMENT '排序';
alter table `notice` add `titleColor` varchar(50) DEFAULT '#000000' COMMENT '标题颜色';
alter table `notice` add `contextColor` varchar(50) DEFAULT '#000000' COMMENT '公告颜色';
alter table `notice` add `contextReviewColor` varchar(50) DEFAULT '#000000' COMMENT '审核公告颜色';

-- 2021.01.28
-- version 4.0.34.35
CREATE TABLE `excel_mail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `server_id` text COMMENT '区服ID',
  `player_id` text COMMENT '角色ID',
  `content` text,
  `create_time` varchar(20) DEFAULT NULL COMMENT '创角时间大于该时间无法获得奖励',
  `sync_time` varchar(20) DEFAULT NULL COMMENT '邮件发送时间',
  `awardStr` varchar(255) DEFAULT NULL COMMENT '奖励字段',
  `subject` varchar(50) DEFAULT NULL COMMENT '邮件标题',
  `status` varchar(5) DEFAULT NULL COMMENT '标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送,3:重新发送成功}',
  `userId` int(10) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置表邮件';

CREATE TABLE `agent_recharge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `server_id` int(11) DEFAULT NULL COMMENT '区服ID',
  `player_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `pro_idx` int(2) DEFAULT NULL COMMENT '充值档位',
  `pay_price` double DEFAULT NULL COMMENT '订单金额(该值验证后写入)',
  `platform` varchar(20) DEFAULT NULL COMMENT '平台<xuegaoSDK, anysdk,mycard...>',
  `order_type` int(2) DEFAULT NULL COMMENT '订单类型',
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道  对于anySDK 那么就是渠道编号,对于非AnySDK这个位置就null',
  `channelCode` varchar(20) DEFAULT NULL COMMENT '渠道号',
  `ext` varchar(20) DEFAULT NULL COMMENT '透传参数',
  `create_time` bigint(20) DEFAULT NULL COMMENT '订单创建时间',
  `finish_time` bigint(20) DEFAULT NULL COMMENT '支付完成时间',
  `status` int(2) DEFAULT 0 COMMENT '状态码',
  `order_id` varchar(42) DEFAULT NULL COMMENT '订单ID UUID+serverId+playerId',
  `player_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `money_type` varchar(25) DEFAULT NULL COMMENT '货币类型',
  `login_name` varchar(50) DEFAULT NULL COMMENT 'GM登录名',
   PRIMARY KEY (`id`),
   KEY `index_sidpid` (`server_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table `notice` add `startTime` varchar(20) DEFAULT '31507200000' COMMENT '开始时间';
alter table `notice` add `endTime` varchar(20) DEFAULT '4102415999000' COMMENT '结束时间';

CREATE TABLE `local_push` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `channel` varchar(50) NOT NULL COMMENT '渠道号',
  `channel_code` text COMMENT '多个channelCode',
  `push_time` varchar(20) COMMENT '推送时间',
  `context` text COMMENT '内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='本地推送';

-- 2022.10.25
-- version 4.0.50.37
CREATE TABLE `xq_mail` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
   `issue_order_id` varchar(255) DEFAULT NULL COMMENT '发放订单号，对同一订单号发放道具需要进行幂等性校验',
   `role_id` varchar(255) DEFAULT NULL COMMENT '发放角色ID',
   `guid` varchar(255) DEFAULT NULL COMMENT '角色所属小号ID',
   `server_id` varchar(255) DEFAULT NULL COMMENT '角色所属区服ID',
   `server_name` varchar(255) DEFAULT NULL COMMENT '角色所属区服名称',
   `issue_time` varchar(255) DEFAULT NULL COMMENT '发放时间，格式为ISO8601，示例：2022-06-27T09:11:13+0800',
   `mail_title` varchar(255) DEFAULT NULL COMMENT '邮件标题',
   `mail_content` varchar(255) DEFAULT NULL COMMENT '邮件内容',
   `test` tinyint(1) DEFAULT 1 COMMENT '是否为测试发放',
   `issuedProps` text COMMENT '发放的道具信息',
   `status` varchar(5) DEFAULT '2' COMMENT '邮件状态',
   PRIMARY KEY (`id`),
   UNIQUE KEY `unique_order_id` (`issue_order_id`) USING BTREE COMMENT '订单号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='小七邮件';
