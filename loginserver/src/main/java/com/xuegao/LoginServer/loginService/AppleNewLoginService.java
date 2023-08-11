package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Base64;
import com.xuegao.LoginServer.po.UserPo;

public class AppleNewLoginService extends AbsLoginService {

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {


        if (parameters.length == 7) {
            String channel = parameters[0];
            String userId = parameters[2];//授权的用户唯一标识
            String email = parameters[3];//授权的用户资料
            String fullName = parameters[4];
            String authorizationCode = parameters[5];//授权code
            String identityToken = parameters[6];//授权用户的JWT凭证
            try {
                String webStr = identityToken.split("\\.")[1];
                byte[] decodeBase64 = Base64.decodeBase64(webStr);
                logger.info( "apple webStr :" + webStr +", decodeBase64:" +decodeBase64);
                String web = new String(decodeBase64 ,"utf-8");
                logger.info("apple web : " + web);
                String  sub=  JSONObject.parseObject(web).getString("sub");
                if (sub.equals(userId)) {
                    return openUserBase(userId, getPlatformName(), channel);
                }
            } catch (Exception ex) {
                logger.info("apple login err :"+ex.getMessage());
            }
        }
        return null;
    }
}
