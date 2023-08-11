package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;

public class Quick7751SDKLoginService extends AbsLoginService{

    private static final  String INTERNAL_URL = "http://m.7751games.com/webapi/checkUserInfo";


    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //取配置参数
        Constants.PlatformOption.Quick7751SDK config = getConfig();
        if (parameters.length == 4) {
            String channel_code = parameters[0];
            String pf = parameters[1];
            String uid = parameters[2];
            String token = parameters[3];
            String productCode=config.productCode;
            if (productCode==null) {
                logger.info("Quick7751SDK productCode:未配置"+productCode);
                return null;
            }
            String postData = "token=" + token + "&uid=" + uid;
            String result = RequestUtil.request(INTERNAL_URL + "?" + postData );
            logger.info("Quick7751SDK 请求url：" + INTERNAL_URL +",请求参数：" + postData + ",返回参数为：" + result);
            if (result !=null ) {
                JSONObject json = JSONObject.parseObject(result);
                if ("true".equals(json.getString("status"))) {
                    return openUserBase(uid, getPlatformName(), channel_code);
                }
            }
        }
        return null;
    }
}
