package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;

public class KWSDKService  extends AbsLoginService {

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        try {
            if (parameters.length == 3) {
                String channel = parameters[0];
                String uid = parameters[2];
                return openUserBase(uid, getPlatformName(), channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
