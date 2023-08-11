package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.data.Constants.PlatformOption.AFengSDK;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

/**
 * 安锋
 * 为了方便游戏厂商接入，所有的接口沿用同一套签名生成（验证）规则，为了以后的开发或升级方便，建议每个接口的签名字串的拼接不要写死在代码里
 *
 * 1.排序：将【所有请求数据（不包括 sign 本身）】按key升序排列
 * 2.拼接：将第1步骤的结果组合成key1=value&key2=value格式
 * 3.追加：将第2步骤的结果末尾追加&sign_key=SIGNKEY（SIGNKEY由平台分配）
 * 4.加密：将第3步骤的结果进行md5加密(32位16进制)，并将结果转换为小写字母，便得到sign参数
 */
public class AnFengSDKService extends AbsLoginService {
    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        //取配置参数
        AFengSDK config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String app_id  = parameters[2];//应用ID，即APPID
            String open_id = parameters[3];//用户在SDK的唯一标识
            String token = parameters[4];//SDK订单号，在异步支付结果通知时传给游戏厂商的
            if (config.apps.get(app_id)==null) {
                logger.info("安锋平台 参数未配置,app_id="+app_id);
                return null;
            }
            String sign_key=config.apps.get(app_id).sign_key_login;
            if (sign_key==null) {
                logger.info("sign_key_login:未配置"+sign_key);
                return null;
            }
            String login_url=config.apps.get(app_id).login_url;
            if (login_url==null) {
                logger.info("login_url:未配置"+login_url);
                return null;
            }
            String str="app_id="+app_id+"&open_id="+open_id+"&token="+token+"&sign_key="+sign_key;
            String sign = Misc.md5(str).toLowerCase();
            String postData = "app_id="+app_id+"&sign="+sign+"&open_id="+open_id+"&token="+token;
            String request = RequestUtil.requestWithPost(login_url, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("安锋 post:"+login_url+",param="+postData+",result="+request);
            if (request!=null) {
                JSONObject json = JSONObject.parseObject(request);
                logger.info("json:"+json.toString());
                if (json.getIntValue("code")==0) {
                    return openUserBase(open_id, getPlatformName(),channel);
                }
            }
        }
        return null;
    }
}
