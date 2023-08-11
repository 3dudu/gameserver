package com.xuegao.PayServer.vo;

public class BTGift {
    public int  active_id;//	int	活动序号(判断唯一 不做重复发放)
    public int  is_freee;//	int	0 免费 1 付费
    public float money;//	int	价值：默认0 单位：元(用户后期的结算付费需记录)
    public String title;//	string	邮件标题
    public String  content;//	string	邮件内容
    public String  player_id;//string	角色ID (研发上报的角色ID)
    public String  server_id;//	string	区服ID (需要校验角色id与服务器id是否匹配)
    public String item;//	string	发放物品 固定格式 例如： [{"mid":"110","amount":10,"bind":1,"type":4},{"mid":"120","amount":10,"bind":1,"type":4}]
                       // mid :道具物品ID amount:数量 bind: 是否绑定/是否可以交易 type 物品类型 需研发提供道具列表配置
    public String  order_id;//	string	订单ID 付费道具订单号存储使用 非付费为0 返利需要验证订单号可以使用该订单号，研发返利验证订单号可以使用这个来识别道具商城订单。
    public String user_id;//	string	用户ID 用户唯一标识
    public String  cp_gift_group;//	string	不是必填项 研发自定义分组ID 付费 订单才有 不参与签名
    public String  is_test;	//string	测试订单 1 是 0 否 不参与签名
    public String sign;//签名：md5(active_id+is_freee+money+title+content+player_id+server_id+key)

    @Override
    public String toString() {
        return "BTGift{" +
                "active_id=" + active_id +
                ", is_freee=" + is_freee +
                ", money=" + money +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", player_id='" + player_id + '\'' +
                ", server_id='" + server_id + '\'' +
                ", item='" + item + '\'' +
                ", order_id='" + order_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", cp_gift_group='" + cp_gift_group + '\'' +
                ", is_test='" + is_test + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
