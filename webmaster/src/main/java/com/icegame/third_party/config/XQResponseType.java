package com.icegame.third_party.config;

public enum XQResponseType {

    PROP_QUERY_SUCCESS("SUCCESS","查询成功"),
    ERROR_NO_SLAVE("100001","没有配置Slave服务器，请联系游戏方人员处理。"),
    ERROR_SLAVE_HTTP("100002","网络错误。"),

    PROP_ISSUE_SUCCESS("SUCCESS","道具发放成功"),
    PROP_ISSUE_FAILED("FAILED","道具发放失败"),
    ORDER_ID_EXISTS("100003","订单ID重复。"),
    EXCEPTION_PROP_ISSUE("100004","道具发放异常。"),
    ERROR_ACCESS_XQ_MALL("100005","访问小7商城错误。"),
    EXCEPTION_ACCESS_XQ_MALL("100006","访问小7商城异常。"),
    ERROR_NO_LOGIN("100007","游戏服配置错误，没有配置登录服。"),
    EXCEPTION_ROLE_QUERY("100008","角色查询异常。"),
    ;

    private final String code;
    private final String msg;

    XQResponseType(String key, String msg) {
        this.code = key;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
