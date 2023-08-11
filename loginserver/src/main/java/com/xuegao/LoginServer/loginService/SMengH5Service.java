package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;

public class SMengH5Service extends AbsLoginService {

    /** queryLoginUrl */
    public static String queryLoginUrl = "http://www.19meng.com/api/v1/verify_session_id";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        Constants.PlatformOption.SMengH5 config = getConfig();
        if (parameters.length == 4) {
            String channel = parameters[0];
            String login_account  = parameters[2];//用戶Id
            String session_id = parameters[3];
            //拼接json
            StringBuffer buff = new StringBuffer();
            buff.append("{\"login_account\":").append("\"").append(login_account).append("\"").append(",")
                    .append("\"session_id\":").append("\"").append(session_id).append("\"}");
            String request = SMengSDKService.post(queryLoginUrl, buff.toString());
            logger.info("手盟H5 post:"+queryLoginUrl+",param="+buff.toString()+",result="+request);
            if(request!=null){
                JSONObject obj= JSON.parseObject(request);
                if(obj.getIntValue("result")==1){
                    return openUserBase(login_account, getPlatformName(),channel);
                }
            }
        }
        return null;
    }
}
