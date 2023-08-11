package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;

public class QuickSDKLoginService extends AbsLoginService{

    private static final  String ABROAD_URL = "http://sdkapi04.quickapi.net/v2/checkUserInfo";

    private static  final String  INTERNAL_URL = " http://checkuser.quickapi.net/v2/checkUserInfo";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //取配置参数
        Constants.PlatformOption.QuickSDK config = getConfig();
        if (parameters.length == 5) {
            String channel_code = parameters[0];
            String pf = parameters[1];
            String uid = parameters[2];
            String token = parameters[3];
            String ext = parameters[4];  //是否为海外 海外传1 非海外传0

            String url = "1".equals(ext) ? ABROAD_URL : INTERNAL_URL;

            String productCode=config.productCode;
            if (productCode==null) {
                logger.info("QuickSDK productCode:未配置"+productCode);
                return null;
            }
            String postData = "token=" + token + "&uid=" + uid + "&product_code=" + productCode;
            String result = RequestUtil.request(url + "?" + postData );
            logger.info("QuickSDK 请求url：" + url +",请求参数：" + postData + ",返回参数为：" + result);
            if ("1".equals(result)) {
                return openUserBase(uid, getPlatformName(), channel_code);
            }
        }
        return null;
    }

}
