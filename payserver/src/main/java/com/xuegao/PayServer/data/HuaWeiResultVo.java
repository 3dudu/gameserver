package com.xuegao.PayServer.data;

/**
 * 华为响应状态码
 * 操作结果。
 * 0: 表示成功
 * 1: 验签失败
 * 2: 超时
 * 3: 业务信息错误，比如订单不存在
 * 94: 系统错误
 * 95: IO 错误
 * 96: 错误的url
 * 97: 错误的响应
 * 98: 参数错误
 * 99: 其他错误
 */
public class HuaWeiResultVo {
    public int result;

    public HuaWeiResultVo(int result) {
        super();
        this.result = result;
    }
}
