package com.xuegao.LoginServer.loginService;

import com.xuegao.LoginServer.po.UserPo;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants.PlatformOption.XPSDK;
import com.xuegao.LoginServer.util.MD5Util;
import com.xuegao.core.util.Misc;

import java.util.Arrays;

public class XPSDKService extends AbsLoginService {


    @Override
    public UserPo loginUser(String[] parameters,JSONObject rs) {
        XPSDK config = getConfig();
        String appServerKey = config.appServerKey;
        if (parameters.length == 5) {
            String channel = parameters[0];
            String openId = parameters[2];
            String timestamp = parameters[3];
            String sign = parameters[4];
            //时间戳校验:时间差精准控制在5~30分钟内,具体视情况而定,实例5分钟
            long diffTime = Math.abs(Long.parseLong(timestamp)-System.currentTimeMillis()/1000);
            if (diffTime>5*60) {
                logger.info("时间差超过5分钟"+"diffTime:"+diffTime);
                return null;
            }
            StringBuffer sb = new StringBuffer();
            sb.append(openId + "&").append(timestamp + "&").append(appServerKey);
            //md5转小写生成sign
            String newSign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("newSign="+newSign+"///sign="+sign+"///appServerKey="+appServerKey);
            //比较sign
            if (newSign.equals(sign)) {
                return openUserBase(openId, getPlatformName(), channel);
            }
        }
        return null;
    }
}
