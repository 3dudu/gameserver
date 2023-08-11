package com.xuegao.PayServer.vo;

/**
 * @Author: LiuBin
 * @Date: 2020/2/3 15:29
 */
public class GMNotifyData {
    public String order_id;//发行平台订单
    public String userId;//玩家ID
    public String amount;//金额（元）
    public String role_id;//角色ID
    public String server_id;//服务器ID
    public String channel_code;//渠道ID
    public String extend_info;//扩展参数

    @Override
    public String toString() {
        return "GMNotifyData{" +
                "order_id='" + order_id + '\'' +
                ", userId='" + userId + '\'' +
                ", amount='" + amount + '\'' +
                ", role_id='" + role_id + '\'' +
                ", server_id='" + server_id + '\'' +
                ", channel_code='" + channel_code + '\'' +
                ", extend_info='" + extend_info + '\'' +
                '}';
    }
}

