package com.xuegao.LoginServer.loginService;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xuegao.LoginServer.data.Constants.PlatformOption.Facebook;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.vo.FacebookLoginResult;
import com.xuegao.core.util.RequestUtil;

public class FacebookService extends AbsLoginService {

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        String channel = parameters[0];
        if (parameters.length == 5) {
            Map<String, String> param = new HashedMap<>();
            Facebook config = getConfig();
            String fbAppId = parameters[4];
            String checkUrl = config.apps.get(fbAppId).fbCheckUrl;
            String access_token = fbAppId + "|" + config.apps.get(fbAppId).fbAppKey;
            param.put("access_token", access_token);
            param.put("input_token", parameters[3]);
            logger.info("faceBook 配置参数：" + config.apps.get(fbAppId).toString());
            String request = null;
            try {
                request = RequestUtil.request(checkUrl, param);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info("err:" + e );
                e.printStackTrace();
            }
            FacebookLoginResult result = JSON.parseObject(request, new TypeReference<FacebookLoginResult>() { });
            if (result != null && result.getData().isIs_valid() && result.getData().getUser_id() != null) {
                logger.info("success:userId:" + result.getData());
                String fb_uid = result.getData().getUser_id();
                JSONObject jsonObject = JSONObject.parseObject(RequestUtil.request("https://graph.facebook.com/" + fb_uid + "?" + "fields=id,name,email" + "&access_token=" + parameters[3]));
                String email = jsonObject.getString("email");
                return insertHasEmailUserPo(fb_uid, getPlatformName(), channel,email);
            }
        }
        return null;
    }
}
