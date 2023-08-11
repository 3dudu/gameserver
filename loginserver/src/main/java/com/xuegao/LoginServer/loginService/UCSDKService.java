package com.xuegao.LoginServer.loginService;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;

public class UCSDKService extends AbsLoginService {

    /**
     * 九游登录域名
     */
    public static final String loginUrl = "http://sdk.9game.cn/cp/account.verifySession";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        //取配置参数
        Constants.PlatformOption.UCSDK config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String id = parameters[2];//
            String gameId = parameters[3];//
            String sid = parameters[4];//
            if (config.apps.get(gameId) == null) {
                logger.info("UC平台 参数未配置,gameId=" + gameId);
                return null;
            }
            String apikey = config.apps.get(gameId).apikey;
            if (apikey == null) {
                logger.info("apikey:未配置" + apikey);
                return null;
            }
            String sign = Misc.md5("sid=" + sid + apikey).toLowerCase();
            //拼接json
            StringBuffer buff = new StringBuffer();
            buff.append("{\"id\":").append(Integer.valueOf(id)).append(",")
                    .append("\"data\":").append("{\"sid\":").append("\"").append(sid).append("\"}").append(",")
                    .append("\"game\":").append("{\"gameId\":").append(Integer.valueOf(gameId)).append("},")
                    .append("\"sign\":").append("\"").append(sign).append("\"}");
            //json请求
            String result = SMengSDKService.post(loginUrl, buff.toString());
            logger.info("UC post:" + loginUrl + ",param=" + buff.toString() + ",result=" + result);
            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                logger.info("UC返回信息:" + json.toString());
                int code = json.getJSONObject("state").getIntValue("code");
                JSONObject data = json.getJSONObject("data");
                if (1==code&&null!=data) {
                    return openUserBase(data.getString("accountId"), getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}

