package com.xuegao.PayServer.vo;

public class SimoSDKNotifyData {
    public String sign;
    public String is_test;
    public String cp_ext;
    public String product_name;
    public String product_id;
    public String role_name;
    public String role_id;
    public String server_name;
    public String server_id;
    public String openid;
    public String paid_time;
    public String paid_state;
    public String paid_amt;
    public String cp_order_no;
    public String m_order_no;
    public String appid;
    public String cchid;
    public String tm;

    @Override
    public String toString() {
        return "SimoSDKNotifyData{" +
                "sign='" + sign + '\'' +
                ", is_test='" + is_test + '\'' +
                ", cp_ext='" + cp_ext + '\'' +
                ", product_name='" + product_name + '\'' +
                ", product_id='" + product_id + '\'' +
                ", role_name='" + role_name + '\'' +
                ", role_id='" + role_id + '\'' +
                ", server_name='" + server_name + '\'' +
                ", server_id='" + server_id + '\'' +
                ", openid='" + openid + '\'' +
                ", paid_time='" + paid_time + '\'' +
                ", paid_state='" + paid_state + '\'' +
                ", paid_amt='" + paid_amt + '\'' +
                ", cp_order_no='" + cp_order_no + '\'' +
                ", m_order_no='" + m_order_no + '\'' +
                ", appid='" + appid + '\'' +
                ", cchid='" + cchid + '\'' +
                ", tm='" + tm + '\'' +
                '}';
    }
}
