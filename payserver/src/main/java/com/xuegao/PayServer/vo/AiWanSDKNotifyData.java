package com.xuegao.PayServer.vo;

public class AiWanSDKNotifyData {
    public String site_uid;//		string	平台uid（爱玩SDK客户端返回account参数值）	是， 202009150930647493
    public String order_no;//		string	爱玩SDK订单号	是	2020091611533299482
    public String  app_id;//		string	应用ID	是	ovdqy3we3ne
    public String channel;//		int	渠道ID	是	431 微信App支付，265 支付宝App支付
    public String  amount;//		float	下单金额（原币）	是	1单位是元
    public String  amount_unit;//		string	货币单位	是	CNY
    public String  amount_rate;//		float	兑换美金的汇率值	是	0.143877
    public String  amount_usd;//		float	美金值	是	0.1439
    public String  amount_change;//		int	下单金额，付款金额是有变动。1有变动，0无变动	是	国内一般是0。国外有部分渠道会变动，是1。
    public String  ext;//		sting	透传参数，如区服、角色、下单ext原样返回	是	{“server_id”:xxx,”role_id”:xxx}
    public String  out_trade_no;//		string	游戏方订单号	是	gmjjgd04c4e99c0015f618c3b
    public String  item_id;//		int	商品ID	是	0
    public String create_time;//		int	创建时间，10位Unix时间戳（秒）	是	1600228412
    public String pay_time;//	int	付款时间，10位Unix时间戳（秒）	是	1600228420
    public String  time;//		int	请求时间，10位Unix时间戳（秒）	是	1600228421
    public String sign;//		string	签名	是	a64179c5a62960942d2540649b2d00a1

    @Override
    public String toString() {
        return "AiWanPayHandler{" +
                "site_uid='" + site_uid + '\'' +
                ", order_no='" + order_no + '\'' +
                ", app_id='" + app_id + '\'' +
                ", channel=" + channel +
                ", amount=" + amount +
                ", amount_unit='" + amount_unit + '\'' +
                ", amount_rate=" + amount_rate +
                ", amount_usd=" + amount_usd +
                ", amount_change=" + amount_change +
                ", ext='" + ext + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", item_id=" + item_id +
                ", create_time=" + create_time +
                ", pay_time=" + pay_time +
                ", time=" + time +
                ", sign='" + sign + '\'' +
                '}';
    }
}
