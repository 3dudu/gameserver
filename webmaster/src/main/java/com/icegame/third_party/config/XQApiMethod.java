package com.icegame.third_party.config;

/**
 * 小七请求方法
 * https://api.x7sy.com/api_helper/x7mall_access
 */
public enum XQApiMethod {
    /**
     * 道具查询 小7Server => 游戏Server
     * 游戏方提供道具编码，通过此接口传递可查询对应道具信息
     */
    PROP_QUERY("x7mall.propQuery"),

    /**
     * 角色查询小7商城 小7Server => 游戏Server
     * 通过此接口传递roleId可查询对应角色信息，也需支持按guid批量查
     */
    ROLE_QUERY_MALL("x7mall.roleQuery"),

    /**
     * 角色查询通用 小7Server => 游戏Server
     * 通过此接口传递roleId可查询对应角色信息，也需支持按guid批量查
     */
    ROLE_QUERY_COMMON("common.roleQuery"),

    /**
     * 道具发放 小7Server => 游戏Server
     * 通过此接口可给指定角色发放道具
     */
    PROP_ISSUE("x7mall.propIssue"),

    /**
     * 商城入口 游戏Server => 小7Server
     * 通过此接口可查询是否需要展示小7商城入口
     */
    MAIL_ENTRY("x7mall.mailEntry"),

    /**
     * 订单通告 小7Server => 游戏Server
     * 玩家下单购买支付成功后会发送订单信息到此接口（此接口非必接）
     */
    ORDER_NOTIFY("x7mall.orderNotify")
    ;

    private final String value;

    XQApiMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
