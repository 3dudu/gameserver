package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class VIVOSDKService extends AbsLoginService {

    /**queryLoginUrl*/
    private static final String requestUri = "https://usrsys.vivo.com.cn/sdk/user/auth.do";


    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs){

        Constants.PlatformOption.VIVOSDK config=getConfig();
        if (parameters.length == 4) {
            String channel = parameters[0];
            String authtoken = parameters[2];
            String from = parameters[3];
            String postData ="authtoken="+authtoken +"&from="+from;
            String request = RequestUtil.requestWithPost(requestUri,postData.getBytes(CharsetUtil.UTF_8));
            if(request!=null){
                JSONObject json = JSONObject.parseObject(request);
                logger.info("json:" + json.toString());
                JSONObject data = json.getJSONObject("data");
                if (json.getIntValue("retcode")==0) {
                    return openUserBase(data.getString("openid"), getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}
