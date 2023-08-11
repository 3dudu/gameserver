package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;

import com.google.api.client.util.Base64;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;

import java.io.UnsupportedEncodingException;


public class FirebaseLoginService extends  AbsLoginService{
    private static final String PUBLIC_KEY_URL = " https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        Constants.PlatformOption.FirebaseLogin config = getConfig();
        if (parameters.length == 4) {
            String channel = parameters[0];
            String userId = parameters[2];//授权的用户唯一标识
            String identityToken = parameters[3];//授权用户的JWT凭证
            try {
                String webStr = identityToken.split("\\.")[1];
                byte[] decodeBase64 = Base64.decodeBase64(webStr);
                logger.info( "webStr :" + webStr +", decodeBase64:" +decodeBase64);
                String web = new String(decodeBase64 ,"utf-8");
                /*{"name":"신수용","picture":"https://graph.facebook.com/2762536730714693/picture",
                "iss":"https://securetoken.google.com/waryong3l","aud":"waryong3l","auth_time":1648691158,
                "user_id":"DxgoAMj2HGadOEewulCVk5G8Slu2","sub":"DxgoAMj2HGadOEewulCVk5G8Slu2","iat":1648691158,
                "exp":1648694758,"email":"steponamine@naver.com","email_verified":false,"firebase"
                :{"identities":{"facebook.com":["2762536730714693"],"email":["steponamine@naver.com"]},
                "sign_in_provider":"facebook.com"}}*/
                logger.info("firebase web : " + web);
                String  sub=  JSONObject.parseObject(web).getString("sub");
                if (sub.equals(userId)) {
                    return openUserBase(userId, getPlatformName(), channel);
                }
            } catch (Exception ex) {
                logger.info("firebase login err :"+ex.getMessage());
            }
        }
        return null;
    }

}
