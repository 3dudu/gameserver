package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants.PlatformOption.YWan;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;

import io.netty.util.CharsetUtil;

/**
 * 悦玩
 * @author Administrator
 *
 */
public class YWanService  extends AbsLoginService{

    /** queryLoginUrl */
    public static String queryLoginUrl = "https://japi.game-props.com/oauth/token";

    @Override
    public UserPo loginUser(String[] parameters,JSONObject rs) {
        YWan config=getConfig();
        if (parameters.length == 4) {
            String channel=parameters[0];
            String authorize_code=parameters[2];
            String j_game_id=parameters[3];
            if(config.apps.get(j_game_id) == null){
                logger.info("YWan平台 参数未配置,game_id="+j_game_id);
                return null;
            }
            String j_request_uri = config.apps.get(j_game_id).j_request_uri;
            String time=""+System.currentTimeMillis()/1000;
            String signStr="authorize_code="+authorize_code+"&j_game_id="+j_game_id+"&j_game_secret="+config.apps.get(j_game_id).j_game_secret+"&time="+time;
            String sign=Misc.md5(signStr);
            String postData="j_game_id="+j_game_id+"&authorize_code="+authorize_code+"&sign="+sign+"&time="+time;
            String request = RequestUtil.requestWithPost(j_request_uri, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("YWan平台 post:"+j_request_uri+",param="+postData+",result="+request);
            if(request!=null){
                JSONObject obj=JSON.parseObject(request);
                //{ "content": { "access_token": "296ebf9ca90afcde08aa8d743ec5d01e", "user_id": 88027, "user_name": "Lizhiwei5"},"msg": "", "ret": 1}
                if(obj.getIntValue("ret")==1){
                    JSONObject user=obj.getJSONObject("content");
                    String user_id=user.getString("user_id");
                    if(user_id!=null){
                        rs.put("ext", user);
                        return openUserBase(user_id, getPlatformName(),channel);
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String postData="j_game_id=1&authorize_code=2";
        String request = RequestUtil.requestWithPost(queryLoginUrl, postData.getBytes(CharsetUtil.UTF_8));
        logger.info("YWan平台 post:"+queryLoginUrl+",param="+postData+",result="+request);
    }
}
