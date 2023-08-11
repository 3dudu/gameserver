package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;


public class YSDKService extends AbsLoginService {

    private static final String url_test_qq = "http://ysdktest.qq.com/auth/qq_check_token";//测试环境qq登录方式
    private static final String url_test_wx = "http://ysdktest.qq.com/auth/wx_check_token";//测试环境微信登录方式
    private static final String url_formal_qq = "http://ysdk.qq.com/auth/qq_check_token";  //正式环境qq登录方式
    private static final String url_formal_wx = "http://ysdk.qq.com/auth/wx_check_token";  //正式环境微信登录方式

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //取配置参数
        Constants.PlatformOption.YSDK config = getConfig();
        if (parameters.length == 6) {
            String channel = parameters[0];
            String appid = parameters[2];
            String openid = parameters[3];//从手Q登录态或微信登录态中获取的openid的值
            String openkey = parameters[4];//从手Q登录态或微信登录态中获取的access_token的值
            String pf = parameters[5];//登录方式 1-qq 2-wx
            if (config.apps.get(appid) == null) {
                logger.info("应用宝平台 参数未配置,appid=" + appid);
                return null;
            }
            if (config.apps.get(appid).LoginAppkey == null) {
                logger.info("应用宝平台 参数未配置,LoginAppkey=" + config.apps.get(appid).LoginAppkey);
                return null;
            }
            long timestamp=System.currentTimeMillis()/1000L;
            String sig= Misc.md5(config.apps.get(appid).LoginAppkey+timestamp);
            String requestData=null;
            switch (Integer.valueOf(pf)) {
                case 1:
                    requestData =config.apps.get(appid).qqLoginUrl+"?"+ "appid=" + appid + "&openid=" + openid + "&openkey=" + openkey + "&sig=" + sig + "&timestamp=" + timestamp;
                    break;
                case 2:
                    requestData =config.apps.get(appid).wxLoginUrl+"?"+ "appid=" + appid + "&openid=" + openid + "&openkey=" + openkey + "&sig=" + sig + "&timestamp=" + timestamp;
                    break;
            }
            logger.info("请求完整url:"+requestData);
            String result = RequestUtil.request(requestData);
            if(result!=null){
                JSONObject obj = JSON.parseObject(result);
                logger.info("应用宝返回:"+obj.toString());
                if(obj.getIntValue("ret")==0){
                    return openUserBase(openid, getPlatformName(),channel);
                }
            }
        }

        return null;
    }
}

