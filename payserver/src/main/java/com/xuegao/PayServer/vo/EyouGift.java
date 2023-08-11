package com.xuegao.PayServer.vo;

public class EyouGift {
    public int gameid;
    public int uid;
    public int s_id;
    public int role_id;
    public int is_all; //是否给全服玩家发送礼包
    public int type; //礼包类型，1=普通礼包（默认），2=广告奖励。注：1）当发送的是广告奖励时，title、content字段不会传
    public String pid;// 礼包ID
    public String title; //	礼包邮件标题
    public String content; //礼包邮件内容正文
    public int mode; //签名算法，固定传1
    public String singleno;// 	请求编号（唯一）
    public long time;// 	请求编号（唯一）
    public String sign;// 	签名

}
