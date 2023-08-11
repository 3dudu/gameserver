-- version 1.0.0.39
-- 2020.07.24 增加内部充值沙盒充值标识
ALTER TABLE `pay_order`
ADD COLUMN `order_type` varchar(20)  COMMENT '内部充值沙盒充值标识';
-- version 1.0.0.70
-- 2022.05.06 添加返利字段
alter table `pay_order` add  column bonus  int(11)  default 0 COMMENT '返利元宝/金币/钻石数量';
alter table `pay_order` add  column ext  varchar(20)  COMMENT '透传';