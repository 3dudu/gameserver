package com.xuegao.PayServer.vo;

public class YSDKNotifyData {
    public String openid;//从手Q登录态或微信登录态中获取的openid的值
    public String openkey;//手Q登陆时传手Q登陆回调里获取的paytoken值，微信登陆时传微信登陆回调里获取的传access_token值。
    public String appid;//offerid即支付结算页面里的应用id，用于支付接口
    public String pf;//登录获取的pf值
    public String pf_key;//登录获取的pfkey值
    public String zoneid;//账户分区ID_角色ID
    public String amt;//扣游戏币的数量(单位/毛)
    public String billno;//订单号，业务自定义，业务需要确保全局的唯一性；相同的订单号不会重复扣款。
    public String platform;//订单平台 1  qq   2 微信

    @Override
    public String toString() {
        return "YSDKNotifyData{" +
                "openid='" + openid + '\'' +
                ", openkey='" + openkey + '\'' +
                ", appid='" + appid + '\'' +
                ", pf='" + pf + '\'' +
                ", pf_key='" + pf_key + '\'' +
                ", zoneid='" + zoneid + '\'' +
                ", amt='" + amt + '\'' +
                ", billno='" + billno + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
