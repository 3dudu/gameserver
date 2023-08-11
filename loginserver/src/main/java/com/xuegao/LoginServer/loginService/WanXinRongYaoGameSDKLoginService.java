package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.MD5Util;
import com.xuegao.LoginServer.util.YueWanUtils;
import com.xuegao.core.util.RequestUtil;

import java.util.HashMap;
import java.util.Map;

public class WanXinRongYaoGameSDKLoginService extends AbsLoginService   {

    private static final String requestUri = "https://fxapi.botaoo.com/oauth/token";


    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //取配置参数
        Constants.PlatformOption.WanXinRongYao config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0]; //咱们的channel_code
            String pf = parameters[1];  //WanXinRongYao
            String authorize_code = parameters[2];
            String channel_code = parameters[3]; //SDK客户段登录接口返回
            String ext_info = parameters[4];
            //计算签名
            String xx_game_secret = config.apps.get(channel).xx_game_secret;
            if (xx_game_secret == null) {
                logger.info("玩心-荣耀 xx_game_secret参数未配置");
                return null;
            }
            String xx_game_id = config.apps.get(channel).xx_game_id;
            if (xx_game_id == null) {
                logger.info("玩心-荣耀 xx_game_id参数未配置");
                return null;
            }
            Map<String ,String > data = new HashMap<>();
            data.put("authorize_code",authorize_code);
            data.put("channel",channel_code);
            data.put("ext_info",ext_info);
            data.put("time",  String.valueOf( System.currentTimeMillis()/1000));
            data.put("xx_game_id",xx_game_id);
            data.put("xx_game_secret",xx_game_secret);

            String signStr = YueWanUtils.getSortQueryString(data);
            // String signStr = "authorize_code="+authorize_code+"&channel="+channel_code+"&ext_info="+ext_info +"&time="+time+"&xx_game_id="+xx_game_id+"&xx_game_secret="+xx_game_secret;
            //Sign,进行编码后传递
            String sign= MD5Util.md5(signStr).toLowerCase();
            logger.info("玩心-荣耀 签名字符拼接为：" + signStr +",md5签名为：" + sign);
            //String postData = "authorize_code="+authorize_code+"&channel="+channel_code+"&time="+time+"&xx_game_id="+xx_game_id+"&sign="+sign + "&ext_info="+ext_info;
            data.remove("xx_game_secret");
            data.put("sign",sign);
            String request = null;
            try {
                request = RequestUtil.requestWithPost(requestUri,new HashMap() ,JSONObject.toJSONString(data));
                logger.info("玩心-荣耀 post:" + requestUri + ",param=" + JSONObject.toJSONString(data) + ",result=" + request);
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    logger.info("json:" + json.toString());
                    if (json.getIntValue("ret") == 1) {
                        JSONObject content = json.getJSONObject("content");
                        String user_id = content.getString("user_id");
                        UserPo userPo =  openUserBase(user_id, getPlatformName(), channel);
                        userPo.setExt(content.toString());
                        return userPo;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
