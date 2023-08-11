package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;

public class MRSDKService extends AbsLoginService {

    private static final String str = "291J4MJHGHAL1";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        if (parameters.length == 5) {
            String channel = parameters[0];
            String mUid = parameters[2];
            String token = parameters[3];
            String openid = parameters[4];

            logger.info("猫耳登录 mUid=" + mUid );
            return openUserBase(mUid, getPlatformName(), channel);

        }
        return null;
    }
}
