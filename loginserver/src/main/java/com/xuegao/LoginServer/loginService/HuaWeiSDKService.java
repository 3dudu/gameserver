package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.SignHelper;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

import java.net.URLEncoder;
import java.util.*;

public class HuaWeiSDKService extends AbsLoginService {
    private static final String requestUri = "https://jos-api.cloud.huawei.com/gameservice/api/gbClientApi";

    private final static  String accessTokenUri = "https://oauth-api.cloud.huawei.com/rest.php?nsp_fmt=JSON&nsp_svc=huawei.oauth2.user.getTokenInfo";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        try {
            //取配置参数
            Constants.PlatformOption.HuaWeiSDK config = getConfig();
            if (parameters.length == 10) {
                String channel = parameters[0];
                String method = parameters[2];
                String openId = parameters[3];
                String openIdSign = parameters[4];
                String ts = parameters[5];
                String playerId = parameters[6];
                String playerLevel = parameters[7];
                String playerSSign = parameters[8];
                String access_token = parameters[9];

                if (config.apps.get(channel) == null) {
                    logger.info("华为平台channel参数未配置,channel=" + channel);
                    return null;
                }

                String  appId = config.apps.get(channel).appId;
                String  cpId  = config.apps.get(channel).cpId;
                UserPo huaWeiPlyaerUser = null ;
                UserPo huaWeiOpenUser = null;
                if ( playerId !=null ) {
                    huaWeiPlyaerUser =  UserPo.findByPfUid(playerId, getPlatformName());
                }
                if (openId != null) {
                    huaWeiOpenUser = UserPo.findByPfUid(Misc.md5(openId),getPlatformName());
                }
                String postData = "access_token=" +URLEncoder.encode(access_token ,"UTF-8") + "&open_id=OPENID";

                //判断playerid是否存在，或者playerid查询到的userPo是否存在，不存在直接用openid做注册 登录
                if (huaWeiOpenUser.getCreate_time() !=null) {
                    String request = RequestUtil.requestWithPost(accessTokenUri, postData.getBytes(CharsetUtil.UTF_8));
                    logger.info("华为验证access_token请求 post:" + requestUri + ",param=" + postData + ",result=" + request);
                    if (request != null) {
                        JSONObject json = JSONObject.parseObject(request);
                        logger.info("json:" + json.toString());
                        return openUserBase(Misc.md5(json.getString("open_id")), getPlatformName(), channel);
                    }
                }else{
                    Map<String, String> requestParams = new HashMap<>();
                    requestParams.put("method", method);
                    requestParams.put("appId", appId);
                    requestParams.put("cpId", cpId);
                    requestParams.put("ts", ts);
                    requestParams.put("playerId", playerId);
                    requestParams.put("openId",openId);
                    requestParams.put("openIdSign",openIdSign);
                    requestParams.put("playerLevel", playerLevel);
                    requestParams.put("playerSSign", playerSSign);
                    logger.info("requestParams:" + requestParams.toString());
                    String cpSign = SignHelper.generateCPSign(requestParams, config.apps.get(channel).CP_AUTH_SIGN_PRIVATE_KEY);

                    //cpSign,playerSSign进行编码后传递
                    cpSign=URLEncoder.encode(cpSign, "UTF-8");
                    playerSSign=URLEncoder.encode(playerSSign, "UTF-8");
                    openIdSign = URLEncoder.encode(openIdSign,"UTF-8");

                    postData = "method=" + method + "&appId=" + appId + "&cpId=" + cpId + "&ts=" + ts + "&playerId=" + playerId
                            + "&playerLevel=" + playerLevel + "&playerSSign=" + playerSSign + "&cpSign=" + cpSign + "&openId=" +openId +"&openIdSign=" +openIdSign;
                    String request = RequestUtil.requestWithPost(requestUri, postData.getBytes(CharsetUtil.UTF_8));
                    logger.info("华为 post:" + requestUri + ",param=" + postData + ",result=" + request);
                    if (request != null) {
                        JSONObject json = JSONObject.parseObject(request);
                        logger.info("json:" + json.toString());
                        if (json.getIntValue("rtnCode") == 0) {
                            huaWeiPlyaerUser =  openUserBase(playerId, getPlatformName(), channel);
                            assert openId != null;
                            huaWeiPlyaerUser.setPf_user(Misc.md5(openId));;
                            huaWeiPlyaerUser.updateToDB();
                            return huaWeiPlyaerUser;
                        }
                    }
                }
            }else if(parameters.length == 9) {   //兼容老版本
                String channel = parameters[0];
                String method = parameters[2];
                String appId = parameters[3];
                String cpId = parameters[4];
                String ts = parameters[5];
                String playerId = parameters[6];
                String playerLevel = parameters[7];
                String playerSSign = parameters[8];
                if (config.apps.get(channel) == null) {
                    logger.info("华为平台channel参数未配置,channel=" + channel);
                    return null;
                }
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("method", method);
                requestParams.put("appId", appId);
                requestParams.put("cpId", cpId);
                requestParams.put("ts", ts);
                requestParams.put("playerId", playerId);
                requestParams.put("playerLevel", playerLevel);
                requestParams.put("playerSSign", playerSSign);
                logger.info("requestParams:" + requestParams.toString());
                String cpSign = SignHelper.generateCPSign(requestParams, config.apps.get(channel).CP_AUTH_SIGN_PRIVATE_KEY);
                //cpSign,playerSSign进行编码后传递
                cpSign=URLEncoder.encode(cpSign, "UTF-8");
                playerSSign=URLEncoder.encode(playerSSign, "UTF-8");
                String postData = "method=" + method + "&appId=" + appId + "&cpId=" + cpId + "&ts=" + ts + "&playerId=" + playerId
                        + "&playerLevel=" + playerLevel + "&playerSSign=" + playerSSign + "&cpSign=" + cpSign;
                String request = RequestUtil.requestWithPost(requestUri, postData.getBytes(CharsetUtil.UTF_8));
                logger.info("华为 post:" + requestUri + ",param=" + postData + ",result=" + request);
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    logger.info("json:" + json.toString());
                    if (json.getIntValue("rtnCode") == 0) {
                        return openUserBase(playerId, getPlatformName(), channel);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
