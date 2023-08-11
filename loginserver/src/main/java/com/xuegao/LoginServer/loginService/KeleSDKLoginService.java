package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.MD5Util;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class KeleSDKLoginService extends AbsLoginService{
    private static final String requestUri = "https://api-h5.kelehuyu.com/api/wxxyx/check_uid";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
            //取配置参数
               Constants.PlatformOption.KeleSDK config = getConfig();

              if (parameters.length == 4) {
                String channel = parameters[0];
                String pf = parameters[1]; //KeleSDK
                String uid = parameters[2];
                String gameId =  parameters[3];
                long time = System.currentTimeMillis()/1000;

                String appkey=config.apps.get(gameId).appkey;
                if (appkey==null) {
                    logger.info("KeLeSDK appkey未配置");
                    return null;
                }

                String signStr = "gid="+gameId+"&time="+time+"&uid="+uid + appkey;
                //Sign,进行编码后传递
                String sign= MD5Util.md5(signStr).toLowerCase();
                logger.info("可乐 签名字符拼接为：" + signStr +",md5签名为：" + sign);
                String postData = "gid=" + gameId + "&time=" + time + "&uid=" + uid +"&sign="+sign;
                String result = RequestUtil.request(requestUri + "?" + postData );
                logger.info("可乐 请求url：" + requestUri +",请求参数：" + postData + ",返回参数为：" + result);
                if (result!= null) {
                    JSONObject datas = JSONObject.parseObject(result);
                    if (datas.get("code").equals(1)) {
                        return openUserBase(uid, getPlatformName(), channel);
                    }
                }
            }
                return null;
    }
}
