package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.data.GlobalCache;
import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.LoginServer.log.ElkLog;
import com.xuegao.LoginServer.log.LogConstants;
import com.xuegao.LoginServer.log.SLogService;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;
import com.xuegao.core.util.twitter.HmacSHA1Encryption;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class TwitterService extends AbsLoginService {

    private static org.apache.log4j.Logger logger = Logger.getLogger(TwitterService.class);

    /**共请求推特3个接口*/
    private static final String request_token_url = "https://api.twitter.com/oauth/request_token";
    private static final String authorize_url = "https://api.twitter.com/oauth/authorize";
    private static final String access_token_url = "https://api.twitter.com/oauth/access_token";

    /**
     * 推特登陆接口
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/user/TwitterLogin")
    public void TwitterLogin(User sender, HttpRequestJSONObject params) throws Exception {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        String channelCode = jsonObject.getString("channelCode");
        AbsLoginService loginService = GlobalCache.fetchLoginService(Constants.PlatformOption.Twitter.class.getSimpleName());
        Constants.PlatformOption.Twitter config = loginService.getConfig();
        if (config==null) {
            logger.info("推特参数未配置");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("推特参数未配置"));
        }
        Map<String, String> map = new HashMap<>();
        map.put("oauth_consumer_key", config.consumerKey);
        map.put("oauth_token", config.access_token);
        map.put("oauth_signature_method", "HMAC-SHA1");
        map.put("oauth_timestamp", String.valueOf(System.currentTimeMillis()/1000L));
        map.put("oauth_nonce", UUID.randomUUID().toString());
        map.put("oauth_version", "1.0");
        String sign = HmacSHA1Encryption.getSign(map, config.consumerSecret+"&"+config.access_token_secret,"POST");
        String authorization = "OAuth " +
                "oauth_callback=\""+config.callback_url+"?channelCode="+channelCode+"\"," +
                "oauth_consumer_key=\""+config.consumerKey+"\"," +
                "oauth_token=\""+config.access_token+"\"," +
                "oauth_signature_method=\"HMAC-SHA1\"," +
                "oauth_timestamp=\""+map.get("oauth_timestamp")+"\"," +
                "oauth_nonce=\""+map.get("oauth_nonce")+"\"," +
                "oauth_version=\"1.0\"," +
                "oauth_signature=\""+sign+"\"";
        //map.clear();
        map.put("Authorization", authorization);
        logger.info("请求头信息:"+map.toString());
        String result = RequestUtil.requestSetRp(request_token_url, map, "POST", null);
        //result:oauth_token=KY0j5AAAAAAA_KmtAAABa5eEiQQ&oauth_token_secret=whZ3cfhfDxN3shXcZgMrJP4oeVpHO6pe&oauth_callback_confirmed=true
        logger.info("result:" + result);
        if (result!=null) {
            String oauth_token = result.split("&")[0].split("=")[1];
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            response.headers().set(HttpHeaderNames.LOCATION, authorize_url + "?" + "oauth_token=" + oauth_token);
            sender.getChannel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 推特回调接口[回调地址在推特后台填写]
     * @param sender
     * @param params
     */
    @Cmd("/user/TwitterCallBack")
    public void TwitterCallBack(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        //json{"channelCode":"1001","oauth_token":"vyecOgAAAAAA_KmtAAABa50n82Q","oauth_verifier":"W0a66Vby9Gs1EExvtJijFhyJp7keihqk"}
        logger.info("json" + json);
        String oauth_verifier = jsonObject.getString("oauth_verifier");
        String oauth_token = jsonObject.getString("oauth_token");
        String channelCode = jsonObject.getString("channelCode");
        String postData = "oauth_verifier=" + oauth_verifier + "&oauth_token=" + oauth_token;
        String result = RequestUtil.requestWithPost(access_token_url, postData.getBytes(CharsetUtil.UTF_8));
        //result:oauth_token=4466357412-VRLwtUBr5bZDJuWXgiyh9gwz0yIU4GrFDFwpoln&oauth_token_secret=KfUMfVV4UoDwBXJhM037I66mP2xI9fDHCXFZCxF5oYnUI&user_id=4466357412&screen_name=LiuBin123456
        logger.info("result:" + result);
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        if (null!=result) {
            String user_id = result.split("&")[2].split("=")[1];
            UserPo userPo = openUserBase(user_id, "Twitter", channelCode);
            if (userPo==null) {
                sender.sendAndDisconnect(MsgFactory.getErrorMsg(Constants.ErrorCode.LOGIN_VALID_FAIL));
                return;
            }
            //创建token,写入redis
            String token = StringUtil.fetchUniqStr_32();
            userPo.saveToken(token);
            //返回
            rs.put("token", token);
            rs.put("uid", ""+userPo.getId());
            ElkLog log = new ElkLog(LogConstants.KindLog.userLoginLog, userPo);
            log.setUid(userPo.getId() == null ? "0" : userPo.getId().toString());
            SLogService.new_log_elk((JSONObject) JSON.toJSON(log));
            logger.info("平台登录成功:userId=" + userPo.getId()+",channelCode="+channelCode);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            logger.info("tokencallback://"+rs.toJSONString());
            response.headers().set(HttpHeaderNames.LOCATION, "tokencallback://"+token+","+userPo.getId());
            sender.getChannel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        return null;
    }
}
