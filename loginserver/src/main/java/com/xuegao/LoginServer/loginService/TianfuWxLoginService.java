package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import org.apache.log4j.Logger;

public class TianfuWxLoginService extends AbsLoginService{

    private static org.apache.log4j.Logger logger = Logger.getLogger(TianfuWxLoginService.class);

    private static final String  GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

    private static final String  REFRESH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

    private static final String  CHECK_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/auth";

    private static final String  GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        Constants.PlatformOption.TianfuWxSDK config = getConfig();
        logger.info("天苻微信 配置参数为:" + config.toString());
        if (parameters.length == 4) {
            String channel = parameters[0];
            String pf = parameters[1]; //TianfuWxSDK
            String appId = parameters[2];
            String code = parameters[3];

            //获取应用对应微信的SECRET
            String secret = config.apps.get(appId).secret;
            if (secret == null) {
                logger.info("微信登录appid:"+ appId +"参数未配置");
                return null;
            }
            //根据code 获取access token
            String request = RequestUtil.request(GET_ACCESS_TOKEN + "?appid="+appId+"&secret="+secret+"&code="+ code
                    +"&grant_type=authorization_code");
            logger.info("获取access token接口返回内容：" + request);
            if (request != null) {
                JSONObject json = JSONObject.parseObject(request);
                if (!json.containsKey("access_token")) {
                    logger.info("获取 access token 错误！");
                    return null;
                }
                String access_token = json.getString("access_token");
                String open_id = json.getString("openid");
                String refresh_token = json.getString("refresh_token");
                //判断access_token 是否需要刷新
                if (judgeToken(access_token,open_id)) {
                    access_token =  getNewToken(appId,refresh_token,access_token);
                }
                //获取用户信息
                String userInfoStr = RequestUtil.request(GET_USER_INFO + "?access_token="+ access_token+"&openid=" + open_id);
                logger.info("获取用户信息接口返回内容为：" + userInfoStr);
                if (userInfoStr != null) {
                    JSONObject userInfo = JSONObject.parseObject(userInfoStr);
                    if (userInfo.containsKey("unionid")) {
                        return openUserBase(userInfo.getString("unionid"), getPlatformName(), channel);
                    }
                }
            }
        }
        return null;
    }
    //是否需要刷新token
    public  boolean judgeToken(String access_token ,String openId) {
        String judge = RequestUtil.request(CHECK_ACCESS_TOKEN + "?access_token="+ access_token+"&openid=" + openId);
        logger.info("判断access token 时效性返回内容为：" + judge);
        if (judge != null) {
            JSONObject judgeInfo = JSONObject.parseObject(judge);
            if (judgeInfo.getIntValue("errcode") == 0) {
                return false;
            }
        }
        return true;
    }
    //刷新token
    public String getNewToken(String appId , String refresh_token ,String access_token){
        String accessInfoStr = RequestUtil.request(REFRESH_ACCESS_TOKEN + "?appid="+appId+"&grant_type=refresh_token&refresh_token=" +refresh_token);
        logger.info("获取新access token接口返回：" + accessInfoStr);
        if (accessInfoStr != null) {
            JSONObject accessInfo = JSONObject.parseObject(accessInfoStr);
            return accessInfo.getString("access_token");
        }
        return access_token;
    }

}
