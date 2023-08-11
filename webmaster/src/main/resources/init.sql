
-- ----------------------------
-- Table structure for all_srv_mail
-- ----------------------------
DROP TABLE IF EXISTS `all_srv_mail`;
CREATE TABLE `all_srv_mail` (
  `id` bigint(20) NOT NULL COMMENT '自增ID',
  `context` text,
  `create_time` varchar(20) DEFAULT NULL,
  `awardStr` varchar(255) DEFAULT NULL COMMENT '奖励字段',
  `subject` varchar(50) DEFAULT NULL COMMENT '邮件标题',
  `channel` varchar(255) DEFAULT NULL COMMENT '渠道',
  `status` varchar(5) DEFAULT NULL COMMENT '标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送}',
  `mailType` int(1) DEFAULT NULL COMMENT '邮件方式：0:channel；1:slave节点',
  `slave` varchar(255) DEFAULT NULL COMMENT 'slave节点',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全服邮件';


-- ----------------------------
-- Table structure for gmlogs
-- ----------------------------
DROP TABLE IF EXISTS `gmlogs`;
CREATE TABLE `gmlogs` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `createTime` varchar(30) DEFAULT NULL,
  `loginName` varchar(30) DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for help_sys
-- ----------------------------
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

-- ----------------------------
-- Table structure for help_sys_sign
-- ----------------------------
CREATE TABLE `help_sys_sign` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `sign_name` varchar(10) DEFAULT NULL COMMENT '类型名字：热门攻略、新手推荐、高玩攻略...',
  `sign_type` int(5) DEFAULT NULL COMMENT '1：热门攻略  2：新手推荐  3：高玩攻略...',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of help_sys_sign
-- ----------------------------
INSERT INTO `help_sys_sign` VALUES ('1', '热门攻略', 1);
INSERT INTO `help_sys_sign` VALUES ('2', '新手推荐', 2);
INSERT INTO `help_sys_sign` VALUES ('3', '高玩攻略', 3);


-- ----------------------------
-- Table structure for help_sys_type
-- ----------------------------
CREATE TABLE `help_sys_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `diff_name` varchar(10) DEFAULT NULL COMMENT '类型名字：官方、攻略',
  `diff_type` int(5) DEFAULT NULL COMMENT '1：官方  2：攻略',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of help_sys_type
-- ----------------------------
INSERT INTO `help_sys_type` VALUES ('1', '官方', 1);
INSERT INTO `help_sys_type` VALUES ('2', '攻略', 2);


-- ----------------------------
-- Table structure for jactivitygift
-- ----------------------------
DROP TABLE IF EXISTS `jactivitygift`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for jactivitygiftkey
-- ----------------------------
DROP TABLE IF EXISTS `jactivitygiftkey`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for jautoreply_message
-- ----------------------------
DROP TABLE IF EXISTS `jautoreply_message`;
CREATE TABLE `jautoreply_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `content` text COMMENT '消息内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='自动回复表';

-- ----------------------------
-- Records of jautoreply_message
-- ----------------------------
INSERT INTO `jautoreply_message` VALUES ('1', '留言成功！客服上班时间 9:00 - 21:00，请在次日关注回馈，谢谢！');

-- ----------------------------
-- Table structure for jcsmessage
-- ----------------------------
DROP TABLE IF EXISTS `jcsmessage`;
CREATE TABLE `jcsmessage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `csId` int(11) DEFAULT NULL COMMENT '客服ID',
  `serverId` int(11) DEFAULT NULL COMMENT '区服ID',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家ID',
  `csName` varchar(50) DEFAULT NULL COMMENT '客服昵称',
  `playerName` varchar(50) DEFAULT NULL COMMENT '玩家姓名',
  `serverName` varchar(50) DEFAULT NULL,
  `createTime` varchar(20) DEFAULT NULL COMMENT '发送消息时间戳',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `flag` int(1) DEFAULT NULL COMMENT '用来此条标识是回复消息还是接受消息，0：玩家发送    1：客服发送',
  `content` text COMMENT '消息内容',
  `isNew` int(1) DEFAULT NULL COMMENT '判断是否是新消息；0：老消息 1：新消息',
  PRIMARY KEY (`id`),
  KEY `sidpid` (`serverId`,`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客服聊天记录表';


-- ----------------------------
-- Table structure for jcsmessage_info
-- ----------------------------
DROP TABLE IF EXISTS `jcsmessage_info`;
CREATE TABLE `jcsmessage_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `serverId` int(11) DEFAULT NULL COMMENT '区服ID',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家ID',
  `banTime` varchar(20) DEFAULT NULL COMMENT '封号天数',
  `chattingBanTime` varchar(20) DEFAULT NULL COMMENT '聊天禁言天数',
  `mailBanTime` varchar(20) DEFAULT NULL COMMENT '禁发邮件天数',
  `vip` int(3) DEFAULT NULL COMMENT 'vip等级',
  `shieldCount` int(11) DEFAULT NULL,
  `usedTitle` int(11) DEFAULT NULL,
  `skinId` int(11) DEFAULT NULL,
  `speakerTypeId` bigint(20) DEFAULT NULL,
  `sendMail` int(1) DEFAULT NULL COMMENT '客服回复玩家之后是否向玩家发送邮件提醒 0：发送邮件   1：不发送',
  PRIMARY KEY (`id`),
  KEY `sidpid` (`serverId`,`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='玩家信息表';

-- ----------------------------
-- Table structure for jcsmessage_process
-- ----------------------------
DROP TABLE IF EXISTS `jcsmessage_process`;
CREATE TABLE `jcsmessage_process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `msgId` bigint(20) DEFAULT NULL,
  `serverId` int(11) DEFAULT NULL COMMENT '区服ID',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家ID',
  `csId` int(11) DEFAULT NULL COMMENT '客服ID',
  `csName` varchar(50) DEFAULT NULL COMMENT '客服姓名',
  PRIMARY KEY (`id`),
  KEY `sidpid` (`serverId`,`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='反馈问题处理表';


-- ----------------------------
-- Table structure for jcsmessage_status
-- ----------------------------
DROP TABLE IF EXISTS `jcsmessage_status`;
CREATE TABLE `jcsmessage_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `msgId` bigint(20) DEFAULT NULL,
  `serverId` int(11) DEFAULT NULL COMMENT '区服ID',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家ID',
  `status` int(1) DEFAULT NULL COMMENT '用来判断问题是否已经解决 0：已解决  1：未解决',
  `createTime` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sidpid` (`serverId`,`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='反馈消息状态判断表';


-- ----------------------------
-- Table structure for jevaluate
-- ----------------------------
DROP TABLE IF EXISTS `jevaluate`;
CREATE TABLE `jevaluate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `npcId` int(11) DEFAULT NULL COMMENT '武将ID',
  `npcName` varchar(50) DEFAULT NULL COMMENT '武将姓名',
  `question1` varchar(100) DEFAULT NULL COMMENT '问题1',
  `answer11` int(11) DEFAULT '0' COMMENT '回答1',
  `answer12` int(11) DEFAULT '0' COMMENT '回答2',
  `answer13` int(11) DEFAULT '0' COMMENT '回答3',
  `answer14` int(11) DEFAULT '0' COMMENT '回答4',
  `question2` varchar(100) DEFAULT NULL COMMENT '问题2',
  `answer21` int(11) DEFAULT '0' COMMENT '回答1',
  `answer22` int(11) DEFAULT '0' COMMENT '回答2',
  `answer23` int(11) DEFAULT '0' COMMENT '回答3',
  `answer24` int(11) DEFAULT '0' COMMENT '回答4',
  `question3` varchar(100) DEFAULT NULL COMMENT '3',
  `answer31` int(11) DEFAULT '0' COMMENT '回答1',
  `answer32` int(11) DEFAULT '0' COMMENT '回答2',
  `answer33` int(11) DEFAULT '0' COMMENT '回答3',
  `answer34` int(11) DEFAULT '0' COMMENT '回答4',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='武将评价信息表';

-- ----------------------------
-- Table structure for jkeyuse
-- ----------------------------
DROP TABLE IF EXISTS `jkeyuse`;
CREATE TABLE `jkeyuse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cdKey` varchar(255) DEFAULT NULL,
  `playerId` bigint(20) NOT NULL,
  `serverId` bigint(20) NOT NULL,
  `useType` int(11) DEFAULT NULL,
  `diffType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for jmailmessage
-- ----------------------------
DROP TABLE IF EXISTS `jmailmessage`;
CREATE TABLE `jmailmessage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `createTime` varchar(20) DEFAULT NULL COMMENT '发送邮件时间戳',
  `senderId` int(11) DEFAULT NULL COMMENT '发送者ID',
  `senderName` varchar(50) DEFAULT NULL COMMENT '发送者姓名',
  `senderServerId` int(11) DEFAULT NULL COMMENT '发送者区服',
  `senderServerName` varchar(80) DEFAULT NULL COMMENT '发送者区服名称',
  `receiverId` int(11) DEFAULT NULL COMMENT '接收者ID',
  `receiverName` varchar(50) DEFAULT NULL COMMENT '接收者姓名',
  `receiverServerId` int(11) DEFAULT NULL COMMENT '接收者区服',
  `receiverServerName` varchar(80) DEFAULT NULL COMMENT '接收者区服名称',
  `content` text COMMENT '邮件内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='跨服邮件表';

-- ----------------------------
-- Records of jmailmessage
-- ----------------------------

-- ----------------------------
-- Table structure for jpreinstall_message
-- ----------------------------
DROP TABLE IF EXISTS `jpreinstall_message`;
CREATE TABLE `jpreinstall_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `content` text COMMENT '消息内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='预设消息表';

-- ----------------------------
-- Records of jpreinstall_message
-- ----------------------------
INSERT INTO `jpreinstall_message` VALUES ('1', '有任何问题可在此提出，客服工作时间09:00-21:00，收到消息后会尽快回复！祝您游戏愉快！！');

-- ----------------------------
-- Table structure for jquestion
-- ----------------------------
DROP TABLE IF EXISTS `jquestion`;
CREATE TABLE `jquestion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `status` varchar(20) DEFAULT NULL COMMENT '当前状态   0：已解决  1：未解决',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家ID',
  `playerName` varchar(50) DEFAULT NULL COMMENT '玩家姓名',
  `serverId` int(11) DEFAULT NULL COMMENT '区服ID',
  `serverName` varchar(100) DEFAULT NULL COMMENT '区服名称',
  `csId` int(11) DEFAULT NULL COMMENT '客服ID,代表当天这个问题指派给谁或者是谁认领的',
  `csName` varchar(100) DEFAULT NULL COMMENT '客服昵称,认领此问题的客服昵称',
  `createTime` varchar(20) DEFAULT NULL COMMENT '发送消息时间戳',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改消息时间戳',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='玩家反馈问题列表';


-- ----------------------------
-- Table structure for jquestionnaire
-- ----------------------------
DROP TABLE IF EXISTS `jquestionnaire`;
CREATE TABLE `jquestionnaire` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家id',
  `playerName` varchar(50) DEFAULT NULL COMMENT '玩家姓名',
  `question1` varchar(50) DEFAULT NULL COMMENT '问题1',
  `question2` varchar(50) DEFAULT NULL COMMENT '问题2',
  `question3` varchar(50) DEFAULT NULL COMMENT '问题3',
  `question4` varchar(50) DEFAULT NULL COMMENT '问题4',
  `question5` varchar(50) DEFAULT NULL COMMENT '问题5',
  `question6` varchar(50) DEFAULT NULL COMMENT '问题6',
  `question7` varchar(50) DEFAULT NULL COMMENT '问题7',
  `question8` varchar(50) DEFAULT NULL COMMENT '问题8',
  `question9` varchar(50) DEFAULT NULL COMMENT '问题9',
  `question10` varchar(100) DEFAULT NULL COMMENT '问题10',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='问卷调查表';


-- ----------------------------
-- Table structure for jreducelog
-- ----------------------------
DROP TABLE IF EXISTS `jreducelog`;
CREATE TABLE `jreducelog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `serverId` int(11) DEFAULT NULL COMMENT '区服ID',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家ID',
  `playerName` varchar(255) DEFAULT NULL COMMENT '玩家姓名',
  `serverName` varchar(100) DEFAULT NULL,
  `createTime` varchar(20) DEFAULT NULL COMMENT '时间戳',
  `items` text COMMENT '返还的具体物品明细',
  `npcId` int(11) DEFAULT NULL COMMENT '武将ID',
  `npcName` varchar(50) DEFAULT NULL COMMENT '武将姓名',
  `potential` int(11) DEFAULT NULL COMMENT '升阶次数',
  `evolveLevel` int(11) DEFAULT NULL COMMENT '晋升次数',
  `level` int(11) DEFAULT NULL COMMENT '专属技能等级',
  `exp` int(11) DEFAULT NULL COMMENT '专属技能经验',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='将领重生返还物品明细';


-- ----------------------------
-- Table structure for jworldmessage
-- ----------------------------
DROP TABLE IF EXISTS `jworldmessage`;
CREATE TABLE `jworldmessage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `serverId` int(11) DEFAULT NULL COMMENT '区服ID',
  `playerId` int(11) DEFAULT NULL COMMENT '玩家ID',
  `playerName` varchar(255) DEFAULT NULL COMMENT '玩家姓名',
  `serverName` varchar(100) DEFAULT NULL,
  `createTime` varchar(20) DEFAULT NULL COMMENT '发送消息时间戳',
  `content` text COMMENT '消息内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='聊天记录表';


-- ----------------------------
-- Table structure for main_tain
-- ----------------------------
DROP TABLE IF EXISTS `main_tain`;
CREATE TABLE `main_tain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `targets` text,
  `title` varchar(255) DEFAULT NULL,
  `closed` int(11) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `selectedTargetsList` varchar(255) DEFAULT NULL,
  `targetList` varchar(255) DEFAULT NULL,
  `selectedChannel` text DEFAULT NULL COMMENT '渠道',
  `selectedSlave` text DEFAULT NULL COMMENT 'slave节点',
  `mainType` int(5) DEFAULT NULL COMMENT '维护方式：0:默认；1:slave节点；2:channel',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of main_tain
-- ----------------------------
INSERT INTO `main_tain` VALUES (1, '', '全服维护', 0, '全服维护', '', '', NULL, NULL, 0);

-- ----------------------------
-- Table structure for main_tain_desc_time
-- ----------------------------
CREATE TABLE `main_tain_desc_time` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `expect_start_time` varchar(20) DEFAULT NULL COMMENT '预计维护开始时间',
  `expect_end_time` varchar(20) DEFAULT NULL COMMENT '预计维护结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mgroup
-- ----------------------------
DROP TABLE IF EXISTS `mgroup`;
CREATE TABLE `mgroup` (
  `groupId` int(10) NOT NULL AUTO_INCREMENT,
  `groupName` varchar(30) DEFAULT NULL,
  `has_channel` text DEFAULT NULL,
  PRIMARY KEY (`groupId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户组表';

-- ----------------------------
-- Records of mgroup
-- ----------------------------
INSERT INTO `mgroup` VALUES ('1', '超级管理员', '');

-- ----------------------------
-- Table structure for mgroupmenu
-- ----------------------------
DROP TABLE IF EXISTS `mgroupmenu`;
CREATE TABLE `mgroupmenu` (
  `groupId` int(10) NOT NULL,
  `menuId` int(10) NOT NULL,
  PRIMARY KEY (`groupId`,`menuId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组菜单映射表';

-- ----------------------------
-- Records of mgroupmenu
-- ----------------------------
INSERT INTO `mgroupmenu` VALUES ('1', '1');
INSERT INTO `mgroupmenu` VALUES ('1', '2');
INSERT INTO `mgroupmenu` VALUES ('1', '3');
INSERT INTO `mgroupmenu` VALUES ('1', '4');
INSERT INTO `mgroupmenu` VALUES ('1', '5');
INSERT INTO `mgroupmenu` VALUES ('1', '6');
INSERT INTO `mgroupmenu` VALUES ('1', '7');
INSERT INTO `mgroupmenu` VALUES ('1', '8');
INSERT INTO `mgroupmenu` VALUES ('1', '9');
INSERT INTO `mgroupmenu` VALUES ('1', '10');
INSERT INTO `mgroupmenu` VALUES ('1', '11');
INSERT INTO `mgroupmenu` VALUES ('1', '12');
INSERT INTO `mgroupmenu` VALUES ('1', '13');
INSERT INTO `mgroupmenu` VALUES ('1', '14');
INSERT INTO `mgroupmenu` VALUES ('1', '15');
INSERT INTO `mgroupmenu` VALUES ('1', '16');
INSERT INTO `mgroupmenu` VALUES ('1', '17');
INSERT INTO `mgroupmenu` VALUES ('1', '18');
INSERT INTO `mgroupmenu` VALUES ('1', '19');
INSERT INTO `mgroupmenu` VALUES ('1', '20');
INSERT INTO `mgroupmenu` VALUES ('1', '21');
INSERT INTO `mgroupmenu` VALUES ('1', '22');
INSERT INTO `mgroupmenu` VALUES ('1', '23');
INSERT INTO `mgroupmenu` VALUES ('1', '24');
INSERT INTO `mgroupmenu` VALUES ('1', '25');
INSERT INTO `mgroupmenu` VALUES ('1', '26');
INSERT INTO `mgroupmenu` VALUES ('1', '27');
INSERT INTO `mgroupmenu` VALUES ('1', '28');
INSERT INTO `mgroupmenu` VALUES ('1', '29');
INSERT INTO `mgroupmenu` VALUES ('1', '30');
INSERT INTO `mgroupmenu` VALUES ('1', '31');
INSERT INTO `mgroupmenu` VALUES ('1', '35');
INSERT INTO `mgroupmenu` VALUES ('1', '36');
INSERT INTO `mgroupmenu` VALUES ('1', '39');
INSERT INTO `mgroupmenu` VALUES ('1', '40');
INSERT INTO `mgroupmenu` VALUES ('1', '41');
INSERT INTO `mgroupmenu` VALUES ('1', '42');
INSERT INTO `mgroupmenu` VALUES ('1', '43');
INSERT INTO `mgroupmenu` VALUES ('1', '44');
INSERT INTO `mgroupmenu` VALUES ('1', '46');
INSERT INTO `mgroupmenu` VALUES ('1', '47');
INSERT INTO `mgroupmenu` VALUES ('1', '54');
INSERT INTO `mgroupmenu` VALUES ('1', '56');
INSERT INTO `mgroupmenu` VALUES ('1', '58');
INSERT INTO `mgroupmenu` VALUES ('1', '59');
INSERT INTO `mgroupmenu` VALUES ('1', '60');
INSERT INTO `mgroupmenu` VALUES ('1', '61');
INSERT INTO `mgroupmenu` VALUES ('1', '62');
INSERT INTO `mgroupmenu` VALUES ('1', '63');
INSERT INTO `mgroupmenu` VALUES ('1', '64');
INSERT INTO `mgroupmenu` VALUES ('1', '65');
##INSERT INTO `mgroupmenu` VALUES ('1', '66');
INSERT INTO `mgroupmenu` VALUES ('1', '67');
INSERT INTO `mgroupmenu` VALUES ('1', '68');
INSERT INTO `mgroupmenu` VALUES ('1', '69');
INSERT INTO `mgroupmenu` VALUES ('1', '71');
INSERT INTO `mgroupmenu` VALUES ('1', '73');
INSERT INTO `mgroupmenu` VALUES ('1', '75');
INSERT INTO `mgroupmenu` VALUES ('1', '76');
INSERT INTO `mgroupmenu` VALUES ('1', '77');
INSERT INTO `mgroupmenu` VALUES ('1', '78');
INSERT INTO `mgroupmenu` VALUES ('1', '79');
INSERT INTO `mgroupmenu` VALUES ('1', '80');
INSERT INTO `mgroupmenu` VALUES ('1', '81');
INSERT INTO `mgroupmenu` VALUES ('1', '82');
INSERT INTO `mgroupmenu` VALUES ('1', '83');
INSERT INTO `mgroupmenu` VALUES ('1', '84');
INSERT INTO `mgroupmenu` VALUES ('1', '85');
INSERT INTO `mgroupmenu` VALUES ('1', '86');
INSERT INTO `mgroupmenu` VALUES ('1', '87');
INSERT INTO `mgroupmenu` VALUES ('1', '88');
INSERT INTO `mgroupmenu` VALUES ('1', '89');

-- ----------------------------
-- Table structure for mlog
-- ----------------------------
DROP TABLE IF EXISTS `mlog`;
CREATE TABLE `mlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `userId` int(10) NOT NULL COMMENT '用户ID',
  `userName` varchar(30) DEFAULT NULL,
  `createTime` varchar(20) NOT NULL COMMENT '时间戳',
  `operation` text COMMENT '操作详情',
  `type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';


-- ----------------------------
-- Table structure for mmenu
-- ----------------------------
DROP TABLE IF EXISTS `mmenu`;
CREATE TABLE `mmenu` (
  `id` int(64) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `parentId` int(64) NOT NULL COMMENT '父级编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `sort` decimal(10,0) NOT NULL COMMENT '排序',
  `href` varchar(2000) DEFAULT NULL COMMENT '链接',
  `target` varchar(20) DEFAULT NULL COMMENT '目标',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `isShow` char(1) NOT NULL COMMENT '是否在菜单中显示',
  `todo` varchar(255) DEFAULT NULL COMMENT '按钮的方法名',
  PRIMARY KEY (`id`),
  KEY `sys_menu_parent_id` (`parentId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8 COMMENT='菜单表';

-- ----------------------------
-- Records of mmenu
-- ----------------------------
INSERT INTO `mmenu` VALUES ('1', '0', '功能菜单', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('2', '1', '用户管理', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('3', '2', '用户列表', '20', '/master/sysmgr/user/gotoUserList', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('4', '2', '用户组', '20', '/master/sysmgr/group/gotoGroupList', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('5', '1', '个人管理', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('6', '5', '个人信息', '20', '/master/sysmgr/user/gotoUserInfo', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('7', '1', '系统设置', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('8', '7', '菜单管理', '20', '/master/sysmgr/menu/gotoMenuList', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('9', '1', '登录支付', '10', '', '', '', '1', '');
INSERT INTO `mmenu` VALUES ('10', '22', '系统日志', '20', '/master/sysmgr/log/gotoLogList', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('11', '20', '节点列表', '20', '/master/sysmgr/slavenodes/gotoSlaveNodes', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('12', '9', '地址配置', '20', '/master/sysmgr/server/gotoServerNodes', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('13', '9', '同步列表', '20', '/master/sysmgr/sync/gotoSyncStatus', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('14', '1', '邮件管理', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('15', '14', '全服邮件', '20', '/master/sysmgr/allsrvmail/gotoAllSrvMail', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('16', '14', '区服邮件', '20', '/master/sysmgr/selsrvmail/gotoSelSrvMail', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('17', '14', '个人邮件', '20', '/master/sysmgr/rolemail/gotoRoleMail', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('18', '1', '接口配置', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('19', '18', '配置列表', '20', '/master/sysmgr/pfoptions/gotoPfOptions', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('20', '1', '节点管理', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('21', '20', '服务列表', '20', '/master/gotoServerList', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('22', '1', '日志管理', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('23', '20', '维护列表', '20', '/master/sysmgr/maintain/gotoMainTain', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('24', '20', '同步列表', '20', '/master/sysmgr/opcl/gotoOpclServerStatus', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('25', '1', 'GM工具', '10', '', '', '', '1', null);
INSERT INTO `mmenu` VALUES ('26', '25', '角色信息', '30', '/master/sysmgr/gm/gotoGM', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('27', '25', '充值', '20', '/master/sysmgr/gm/gotoGMRecharge', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('28', '25', '历史充值查询', '20', '/master/sysmgr/gm/gotoGMRechargeHistory', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('29', '25', 'GM日志', '20', '/master/sysmgr/gmlogs/gotoGmlogsList', '', 'list-alt', '1', null);
INSERT INTO `mmenu` VALUES ('30', '26', '添加', '100', '', '', '', '1', 'mgr.addGoods();');
INSERT INTO `mmenu` VALUES ('31', '26', '删除', '100', '', '', '', '1', 'mgr.delGoods();');
INSERT INTO `mmenu` VALUES ('34', '26', '查询', '100', '', '', '', '1', 'mgr.searchPlayerInfo();');
INSERT INTO `mmenu` VALUES ('35', '27', '充值', '100', '', '', '', '1', 'mgr.recharge();');
INSERT INTO `mmenu` VALUES ('36', '26', 'base', '10000', '', '', '', '0', 'alert(\"此按钮为基础按钮,不可以删除,不可以修改为可见状态。如果有看到此菜单,请联系管理员进行修改\");');
INSERT INTO `mmenu` VALUES ('39', '26', '编辑', '100', '', '', '', '1', 'alert(\"编辑\");');
INSERT INTO `mmenu` VALUES ('40', '26', '踢出在线玩家', '100', '', '', '', '1', 'mgr.kickedOffOnePlayer()');
INSERT INTO `mmenu` VALUES ('41', '26', '踢掉所有在线玩家', '100', '', '', '', '1', 'mgr.kickedOffAll()');
INSERT INTO `mmenu` VALUES ('42', '25', '公告列表', '20', '/master/sysmgr/notice/gotoNotice', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('43', '1', '客服后台', '10', '', '', '', '1', '');
INSERT INTO `mmenu` VALUES ('44', '43', '世界聊天', '20', '/master/sysmgr/cs/gotoWordMessage', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('45', '14', '邮件同步日志', '20', '/master/sysmgr/syncmail/gotoSyncMail', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('46', '43', '留言邮件', '20', '/master/sysmgr/cs/gotoMailMessage', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('47', '43', '聊天管理', '20', '/master/sysmgr/cs/gotoChatting', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('54', '43', '反馈列表', '20', '/master/sysmgr/cs/gotoQuestion', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('56', '43', '预设消息', '20', '/master/sysmgr/cs/gotoPreinstallMessage', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('58', '26', '刷新下一天', '100', '', '', '', '1', 'mgr.nextDay();');
INSERT INTO `mmenu` VALUES ('59', '26', '六道重置', '100', '', '', '', '1', 'mgr.editSixDao();');
INSERT INTO `mmenu` VALUES ('60', '26', '六道到底', '100', '', '', '', '1', 'mgr.sixDaoLast();');
INSERT INTO `mmenu` VALUES ('61', '43', '武将重生', '20', '/master/sysmgr/cs/gotoReduceLog', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('62', '65', '武将评价', '20', '/master/sysmgr/cs/gotoEvaluate', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('63', '65', '有奖问答', '20', '/master/sysmgr/cs/gotoQuestionnaire', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('64', '26', '下阵将领', '100', '', '', '', '1', 'mgr.batchUnEquipNpc();');
INSERT INTO `mmenu` VALUES ('65', '1', '调查问卷', '10', '', '', '', '1', '');
##INSERT INTO `mmenu` VALUES ('66', '20', '更新配置表', '20', '/master/sysmgr/xls/gotoUploadXls', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('67', '43', '自动回复', '20', '/master/sysmgr/cs/gotoAutoreplyMessage', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('68', '2', '修改用户密码', '10', '/master/sysmgr/userpwd/gotoChangeUserPwd', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('69', '1', 'cdKey', '10', '', '', '', '1', '');
INSERT INTO `mmenu` VALUES ('71', '69', '礼包码列表', '20', '/master/sysmgr/giftkey/gotoActivityGiftKey', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('73', '69', '无限使用', '20', '/master/sysmgr/gift/gotoActivityGiftNoLimit', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('75', '69', '指定区服', '20', '/master/sysmgr/gift/gotoActivityGiftLimitServer', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('76', '69', '特定渠道', '20', '/master/sysmgr/gift/gotoActivityGiftLimitChannel', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('77', '1', '支付管理', '10', '', '', '', '1', '');
INSERT INTO `mmenu` VALUES ('78', '77', '订单列表', '20', '/master/sysmgr/paylist/gotoPayList', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('79', '1', '账号管理', '10', '', '', '', '1', '');
INSERT INTO `mmenu` VALUES ('80', '79', '重置密码', '20', '/master/sysmgr/juser/gotoResetJUserPwd', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('81', '69', '指定区服渠道', '20', '/master/sysmgr/gift/gotoActivityGiftLimitServerChannel', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('82', '1', '帮助系统', '10', '', '', '', '1', '');
INSERT INTO `mmenu` VALUES ('83', '82', '答疑投稿', '20', '/master/sysmgr/helpsys/gotoHelpSys', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('84', '82', '类型管理', '20', '/master/sysmgr/helpsystype/gotoHelpSysType', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('85', '20', '维护时长', '20', '/master/sysmgr/maintaintime/gotoMainTainDescTime', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('86', '82', '标签管理', '20', '/master/sysmgr/helpsyssign/gotoHelpSysSign', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('87', '20', '自动开服', '20', '/master/sysmgr/autoopenserver/gotoAutoOpenServer', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('88', '1', '活动配置', '10', '', '', '', '1', '');
INSERT INTO `mmenu` VALUES ('89', '88', '专属客服QQ', '20', '/master/sysmgr/vipqq/gotoVipqq', '', 'list-alt', '1', '');
INSERT INTO `mmenu` VALUES ('90', '88', '微信公众号', '20', '/master/sysmgr/wechat/gotoWechat', '', 'list-alt', '1', '');

-- ----------------------------
-- Table structure for muser
-- ----------------------------
DROP TABLE IF EXISTS `muser`;
CREATE TABLE `muser` (
  `userId` int(10) NOT NULL AUTO_INCREMENT,
  `loginName` varchar(30) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `isAdmin` varchar(5) DEFAULT NULL COMMENT '是否是所属用户组的管理员 0：不是  1是',
  `isOnline` varchar(5) DEFAULT NULL COMMENT '是否在线',
  `typeName` varchar(5) DEFAULT NULL COMMENT '用户类型',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of muser
-- ----------------------------
INSERT INTO `muser` VALUES ('1', 'admin', 'admin', '2db2e5d31db046ef3d091b8686a16a094db79604c7fa33b38a402ccf', 'on', 'off', 'user');

-- ----------------------------
-- Table structure for musergroup
-- ----------------------------
DROP TABLE IF EXISTS `musergroup`;
CREATE TABLE `musergroup` (
  `userId` int(10) NOT NULL,
  `groupId` int(10) NOT NULL,
  PRIMARY KEY (`userId`,`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户用户组映射表';

-- ----------------------------
-- Records of musergroup
-- ----------------------------
INSERT INTO `musergroup` VALUES ('1', '1');


-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `channel` varchar(50) DEFAULT NULL COMMENT '渠道号',
  `context` text COMMENT '公告内容',
  `context_review` text COMMENT '审核公告内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='游戏内公告';

-- ----------------------------
-- Table structure for opcl_server
-- ----------------------------
DROP TABLE IF EXISTS `opcl_server`;
CREATE TABLE `opcl_server` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `createTime` varchar(50) DEFAULT NULL COMMENT '同步时间',
  `hosts` text,
  `status` text COMMENT '同步状态',
  `type` varchar(200) DEFAULT NULL COMMENT '类型：开服或者维护',
  `serverId` text,
  `failed` text,
  `lastOperate` varchar(20) DEFAULT NULL COMMENT '最后一次执行的操作',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='开关服维护记录表';


-- ----------------------------
-- Table structure for pf_options
-- ----------------------------
DROP TABLE IF EXISTS `pf_options`;
CREATE TABLE `pf_options` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `key` varchar(128) DEFAULT NULL COMMENT '键(各平台接入参数)',
  `value` mediumtext COMMENT '值(json配置)',
  `close` int(2) DEFAULT NULL COMMENT '关闭该渠道充值要求客户端不再调起支付,关闭状态下订单直接返回false不允许支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pf_options
-- ----------------------------
INSERT INTO `pf_options` VALUES ('1', 'XGSDK', '{\"gameId\":\"10254\", \"gameKey\":\"uzQAGuEAh1cH7KEBKRJfD0tAiFXqK3Wh\"}', '0');
INSERT INTO `pf_options` VALUES ('2', 'AnySdk', '{\"merchantKey\":\"9F280A4AA020D4432D544819F219E0D8\",\"enhancedMerchantKey\":\"ZmQwZjEyNTEwZjhkY2ZkZDEzN2U\",\"loginCheckUrl\":\"http://sdktw.xuegaogame.com:8866/api/User/LoginOauth/\"}', '0');
INSERT INTO `pf_options` VALUES ('3', 'WxPayParam', '{\"appid\":\"wxce0170087f532fe0\",\"partnerId\":\"1501044131\",\"apiKey\":\"auZbXVCHXo7FDFknMkuzrkWPiocgvS4w\",\"wxpayNotify\":\"http://114.115.200.181:9030/p/wxPaycallbackNotify/cb\",\"isSandBox\":1}', '0');
INSERT INTO `pf_options` VALUES ('4', 'AliPayParam', '{\"alipayAccount\":\"18117156796@139.com\",\"partnerId\":\"2088021578975132\",\"merchantKey\":\"MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCTTQ4w2PMxIrWKVD9W7/n5k+CHURWSHVUHv0qDarlp3kPZpAqFHOdrRU0WFGTxjyaMXrkZXxc7YU6FGUJswjFdbB3U7wNhbDaV8s8sSln6xdXK42OmeM3ws/UJAEAt3oAaNzWnWnI6DoV3RzKQ0DKSWXYX3v23EPStOMZvmYkP/gEPSeYL0/42G3wO5u8L16mU7bdemJOOpr+Ydtj4LEyW6/lQQzZjvlKhsLCspzLnPpqlR/ZStnjYMSUeIbQfeluxsCbFPZDwUjrj4WE8ISBqfRhq073SYzG4vAoLuKkGmP0UFbCbUjyPtMlKwx365PBJoCx3pasH50owcAGRTG4pAgMBAAECggEAfC6jPxU3+X2slSEvgQixsvIzgYEfcV6fN/fSRvaruBA1HYAJim1ywGHdejbZXzjzI7Yb0ML1IA+KFfXUpUIWQg+LGuAh9nk3j7TzagCI2Tz+X5zSfx8rZ7kfZGY8tS72yqMu7Zb34/AoHwX8IFAVwoH/aGKLfVGjJYJO8Pj8r2iJ2KYhEVUDwi/2szqshiabXEfvKsrYwGT9tC9v2cz73+RGgt67G/OreH/OUYilQExXDmkVPql/QBaYinSYacVNxyEg+FgYKz3HovHVpt5zutd68PVseaQWDhVbGLQ1zq/xtCF2m8YGCjmE5DX5NIdPJ8VdCZylotM8ClC3ZJd4sQKBgQDN7hZAzWBKBEEKQRyL/MDF6WJ8CLhjbkJ4DX4KFhhWB/GBaNuzn0FkluvD7dr2IhJHNLxxrV54mP2zbSTI5bbLG+fcXtat58r0e/kQMUK2mTZoh21kghrEm2Hf59JRTICrR6rwH9wiyraDQi9UTL6Wj+8OoKntlR066KjRAlKHrQKBgQC3Hal0pF9bs1NgdvyZqhSnVs+/mN2JANgWEjvumi6piu7H9FqDo9xSAcPoAiYp1XFil42NxlzQwtYpXR4/XKLedOZKmze9V/txty7K3qzJb4PZVP7EZ7PAR12wllt0QogATlBIUF4mx2jH78/5SBB5GQWMGdXkrV2HDR2giq5/7QKBgQC8OHpUfqMc4+iCs+KgvMOB4YBnuBy4nOXaDxL9qxZRQ+9khfARrGU7yInzyiqRP7EPLk9zY6/ucH+qiP7PNM8vs/DnxjNYPn2NmuaZ+zxr43wMbIP7EsPeodsCqUPdlRwh8V53Hde6YSgSruLNnVuyCBrLduP6SImv90mdyA6g/QKBgG2eFBL/UxZSA1d74zsZ1cNW9IZ9vCCHoNOVlsr/AW/8+3Q3kve63+k3pU79+lqQq4+ZqeN1G5rdB9c7JyBHFDiXQkTnJjbytFMelaRcKJNtbkBr4Rcx/Sjdj/hmCdlO+ps0RlsDwVj1hoQpyxYvTa0wjWGRFboPDi4Ka/HvEwcBAoGBAIdJGqW3umikckvPf0qpvYApyMfEd7eP4gyvQc4YE0AbtqZLLFih2bk8lS1u99h9RzC/+F+ho2I2XQaWRrAz1zeOu+uzovu3FRhczd0LYcFBl+K3Q6pLlBa8UcfRbNijBXLykaiX27tMOacOFb3RKKZXiPXvwkA4aHC4B0Fk4SZI\",\"merchantPublicKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk00OMNjzMSK1ilQ/Vu/5+ZPgh1EVkh1VB79Kg2q5ad5D2aQKhRzna0VNFhRk8Y8mjF65GV8XO2FOhRlCbMIxXWwd1O8DYWw2lfLPLEpZ+sXVyuNjpnjN8LP1CQBALd6AGjc1p1pyOg6Fd0cykNAykll2F979txD0rTjGb5mJD/4BD0nmC9P+Nht8DubvC9eplO23XpiTjqa/mHbY+CxMluv5UEM2Y75SobCwrKcy5z6apUf2UrZ42DElHiG0H3pbsbAmxT2Q8FI64+FhPCEgan0YatO90mMxuLwKC7ipBpj9FBWwm1I8j7TJSsMd+uTwSaAsd6WrB+dKMHABkUxuKQIDAQAB\",\"aliPublicKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsfVxhBxVDwLahHuXpCmcYCvrJqmGYzjpovGrZJaOUoD5jpF5kZJ23owW+67HR53JgDEIHLur7k4ZQ40kL93w/DUEobLBfhwty7AYBbCrC4ibjn9q9XrLJItfZT37j9iHQPyx+FbTjYZmOlqPnZfeF+rPNei8a5CW4DQju8/4ApB6NThboVxMri4Yl7O/w7LaZoJ1CRxljTRviuCRDaw3jSks6S7uIvNs88zd1BBkVM84D2JlAEGAdACZXlrq0VgFNTpHxv6VM4vhboL739Ub0tE9pp01HoFE9SIMEOsGDNf0u92kGW7KhQIMR8HBdUeXXrjZTDIRUUhY66fg6+8MfwIDAQAB\",\"alipayNotify\":\"http://114.115.200.181:9030/p/alipayRSA2Notify/cb\",\"return_url\":\"http://xg.p.xuegaogame.com:8080/ipp-work/api/aliPaySuccess\",\"appId\":\"2018111662151863\"}', '0');


-- ----------------------------
-- Table structure for role_mail
-- ----------------------------
DROP TABLE IF EXISTS `role_mail`;
CREATE TABLE `role_mail` (
  `id` bigint(20) NOT NULL COMMENT '自增ID',
  `sid_pid` text COMMENT '服务器ID',
  `context` text,
  `create_time` varchar(20) DEFAULT NULL COMMENT '创角时间大于改时间无法获得奖励',
  `awardStr` varchar(255) DEFAULT NULL COMMENT '奖励字段',
  `subject` varchar(50) DEFAULT NULL COMMENT '邮件标题',
  `status` varchar(5) DEFAULT NULL COMMENT '标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送}',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单人，多人邮件';


-- ----------------------------
-- Table structure for sel_srv_mail
-- ----------------------------
DROP TABLE IF EXISTS `sel_srv_mail`;
CREATE TABLE `sel_srv_mail` (
  `id` bigint(20) NOT NULL COMMENT '自增ID',
  `sid` varchar(255) DEFAULT NULL COMMENT '服务器ID',
  `context` text,
  `create_time` varchar(20) DEFAULT NULL,
  `awardStr` varchar(255) DEFAULT NULL,
  `subject` varchar(50) DEFAULT NULL COMMENT '邮件标题',
  `status` varchar(5) DEFAULT NULL COMMENT '标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送}',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区服邮件';


-- ----------------------------
-- Table structure for server_list
-- ----------------------------
DROP TABLE IF EXISTS `server_list`;
CREATE TABLE `server_list` (
  `server_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '区服ID',
  `name` varchar(20) DEFAULT NULL COMMENT '区服名称',
  `ip` varchar(50) DEFAULT NULL COMMENT 'ip地址',
  `port` varchar(6) DEFAULT NULL COMMENT '端口号',
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道号',
  `group` varchar(100) DEFAULT NULL COMMENT '跨服分组',
  `close` int(11) DEFAULT NULL COMMENT '0为开启，1为关闭',
  `beginTime` varchar(20) DEFAULT NULL COMMENT '开始时间',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '更新时间',
  `passTime` varchar(20) DEFAULT NULL COMMENT '结束时间',
  `slaveId` int(20) DEFAULT NULL COMMENT 'slave节点的id',
  `is_success` int(1) DEFAULT NULL COMMENT '标示开服是否成功,0：失败，1：成功',
  `is_auto` int(1) default '0' not null COMMENT '开服类型；0：手动 1：自动',
  `is_suggest` int(1) DEFAULT '0' COMMENT '是否是推荐服：0:不是；1:是',
  `wudaogroup` int(11) DEFAULT '0' COMMENT '武道会分组',
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for server_nodes
-- ----------------------------
DROP TABLE IF EXISTS `server_nodes`;
CREATE TABLE `server_nodes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '区服ID',
  `name` varchar(20) DEFAULT NULL COMMENT '区服名称',
  `protocol` varchar(20) DEFAULT NULL,
  `ip` varchar(100) DEFAULT NULL COMMENT 'ip地址',
  `port` varchar(10) DEFAULT NULL COMMENT '端口号',
  `interfaceName` varchar(100) DEFAULT NULL,
  `reInterfaceName` varchar(100) DEFAULT NULL,
  `diff` int(10) DEFAULT NULL COMMENT '区别登录服和支付服:1.登录 2.支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of server_nodes
-- ----------------------------
INSERT INTO `server_nodes` VALUES ('1', '开发测试-登录服', 'http://', '114.115.200.181', '8882', '/api/open/login/sycnData', '/api/open/login/reloadData?serviceName=', '1');
INSERT INTO `server_nodes` VALUES ('2', '开发测试-支付服', 'http://', '114.115.200.181', '8888', '/api/open/pay/sycnData', '/api/open/pay/reloadData?serviceName=', '2');

-- ----------------------------
-- Table structure for slave_nodes
-- ----------------------------
DROP TABLE IF EXISTS `slave_nodes`;
CREATE TABLE `slave_nodes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '节点ID',
  `name` varchar(100) DEFAULT NULL COMMENT '节点名称',
  `ip` varchar(50) DEFAULT NULL COMMENT '外网IP',
  `nip` varchar(50) DEFAULT NULL COMMENT '内网IP',
  `port` int(6) DEFAULT NULL COMMENT '同步使用的端口',
  `limit` int(10) NOT NULL DEFAULT '0' COMMENT '限制在此服务器开的区服数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='slave节点表';


-- ----------------------------
-- Table structure for sync_mail
-- ----------------------------
DROP TABLE IF EXISTS `sync_mail`;
CREATE TABLE `sync_mail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `createTime` varchar(50) DEFAULT NULL COMMENT '同步时间',
  `hosts` text,
  `status` text COMMENT '同步状态',
  `type` varchar(200) DEFAULT NULL COMMENT '类型：个人多人邮件或者区服邮件或者全服邮件',
  `serverId` text,
  `failed` text,
  `lastOperate` varchar(20) DEFAULT NULL COMMENT '最后一次执行的操作',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='master邮件同步记录表';

-- ----------------------------
-- Table structure for sync_status
-- ----------------------------
DROP TABLE IF EXISTS `sync_status`;
CREATE TABLE `sync_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `createTime` varchar(50) DEFAULT NULL COMMENT '同步时间',
  `serverName` varchar(20) DEFAULT NULL,
  `serverNodeIp` varchar(100) DEFAULT NULL COMMENT '节点IP',
  `status` text COMMENT '同步状态',
  `type` varchar(200) DEFAULT NULL COMMENT '类型：邮件、服务器列表、配置',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for update_queue
-- ----------------------------
DROP TABLE IF EXISTS `update_queue`;
CREATE TABLE `update_queue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `userId` bigint(20) DEFAULT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `createTime` varchar(20) DEFAULT NULL,
  `uploadTime` varchar(20) DEFAULT NULL,
  `targetServer` varchar(10) DEFAULT NULL COMMENT '目标服务器',
  `targetServerName` varchar(50) DEFAULT NULL,
  `language` varchar(10) DEFAULT NULL COMMENT '语言版本',
  `openMd5` varchar(1) DEFAULT NULL COMMENT '{0:关闭md5验证}{1:开启md5验证}',
  `state` varchar(1) DEFAULT NULL COMMENT '{0:没有开始},{1:正在执行},{2:已经完成}',
  `result` varchar(1) DEFAULT NULL COMMENT '{0:失败}{1:成功}',
  `file` varchar(255) DEFAULT NULL COMMENT '文件路径',
  `detail` varchar(255) DEFAULT NULL COMMENT '结果明细',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置表更新记录';


-- ----------------------------
-- Table structure for auto_open_server
-- ----------------------------
DROP TABLE IF EXISTS `auto_open_server`;
CREATE TABLE `auto_open_server` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `key` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `status` varchar(1) DEFAULT '1' COMMENT '自动开服开关：0:关闭，1:开启',
  `type` varchar(10) DEFAULT NULL COMMENT '类型，1:代表自动开服',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


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
INSERT INTO `mconfigure` VALUES ('5', 'auto.openserver.tips.status', '0');
INSERT INTO `mconfigure` VALUES ('6', 'auto.openserver.tips.slimit', '15');

-- ----------------------------
-- Table structure for jvipqq
-- ----------------------------
CREATE TABLE `jvipqq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `qq` VARCHAR (30) DEFAULT NULL,
  `times` int(10) DEFAULT '0' COMMENT '每个qq号分配的次数',
  `channel` varchar(20) default null comment '渠道',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for jvipqq_recode
-- ----------------------------
CREATE TABLE `jvipqq_recode` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `qid` bigint(20) DEFAULT NULL COMMENT '对应jvipqq表的id',
  `sid` bigint(20) DEFAULT NULL,
  `pid` bigint(20) DEFAULT NULL,
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道',
  PRIMARY KEY (`id`),
  KEY `index_sidpid` (`sid`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for memail
-- ----------------------------
CREATE TABLE `memail` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `email` varchar(30) DEFAULT NULL,
  `type` int(15) DEFAULT NULL COMMENT '类型：1:自动开服',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for jvipqq_wx_config
-- ----------------------------
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

-- ----------------------------
-- Table structure for jfacebook
-- ----------------------------
CREATE TABLE `jfacebook` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `yuanbao` bigint(20) DEFAULT NULL COMMENT '元宝数量',
  `likenum` bigint(20) DEFAULT NULL COMMENT '点赞数',
  `channel` varchar(20) DEFAULT NULL COMMENT '渠道',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 从现在开始，init的sql如果有变动，一律在之后面添加更新sql
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
-- ----------------------------
-- Table structure for excel_mail
-- ----------------------------
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

-- ----------------------------
-- Table structure for agent_recharge
-- ----------------------------
DROP TABLE IF EXISTS `agent_recharge`;
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

-- ----------------------------
-- Table structure for local_push
-- ----------------------------
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
