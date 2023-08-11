package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.twitter.HmacSHA1Encryption;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class DMMLoginService extends AbsLoginService{
    private static org.apache.log4j.Logger logger = Logger.getLogger(DMMLoginService.class);

    private static final String SANDBOX_PC = "https://sbx-osapi.dmm.com/social_pc/rest";
    private static final String SANDBOX_SP = "https://sbx-osapi.dmm.com/social_sp/rest";
    private static final String NORMAL_PC  = "https://osapi.dmm.com/social_pc/rest";
    private static final String NORMAL_SP  = "https://osapi.dmm.com/social_sp/rest";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        Constants.PlatformOption.DMMSDK config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String pf = parameters[1];//DMMSDK
            String pc = parameters[2];//pc为  0
            String appid = parameters[3];
            String opensocialViewerId =parameters[4];

            if (config.apps.get(appid) == null) {
                logger.info("DMM 参数未配置,appid=" + appid);
                return null;
            }
            if (config.apps.get(appid).consumer_key == null) {
                logger.info("DMM 参数未配置,consumer_key null");
                return null;
            }
            if (config.apps.get(appid).consumer_secret == null) {
                logger.info("DMM 参数未配置,consumer_secret null");
                return null;
            }
            //$url =  $ENDPOINT_URL . '/people/'.$opensocialViewerId.'/@self?fields=id,userType';
            String url = null;
            switch (pc){
                case "0":
                        url = config.apps.get(appid).pc_url;
                    break;
                case "1":
                        url = config.apps.get(appid).sp_url;
                    break;
            }
            url+= "/people/" + opensocialViewerId + "/@self";
            //生成base-string
            //base-string：GET&https%3A%2F%2Fsbx-osapi.dmm.com%2Fsocial_sp%2Frest%2Fpeople%2F3067411%2F%40self&
            // fields%3Did%252CuserType%26oauth_consumer_key%3D0VhisrMCGswQ6OU4%26oauth_nonce%3D2855288%26
            // oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1685245629%26oauth_version%3D1.0%26xoauth_requestor_id%3D3067411
            String base_string = null;
            String hmacsha1key = null;
            String oauth_signature = null;
            long oauth_timestamp = System.currentTimeMillis()/1000;
            long oauth_nonce = new Random().nextInt(101) + oauth_timestamp;
            try {
                 base_string ="GET&" + URLEncoder.encode( url ,"utf-8" )+ "&" + URLEncoder.encode("fields=","utf-8")+ URLEncoder.encode(URLEncoder.encode("id,userType","utf-8") + "&oauth_consumer_key="
                          + config.apps.get(appid).consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=HMAC-SHA1" + "&oauth_timestamp=" +oauth_timestamp
                          + "&oauth_version=1.0&xoauth_requestor_id=" + opensocialViewerId,"utf-8");
                 hmacsha1key = URLEncoder.encode(config.apps.get(appid).consumer_secret,"utf-8") +"&";
                //生成oauth_signature 签名
                oauth_signature = URLEncoder.encode(HmacSHA1Encryption.hmacSHA1Encrypt(base_string, hmacsha1key) ,"utf-8");
                logger.info("base_string:" + base_string +"  ,hmacsha1key:" + hmacsha1key + ",生成oauth_signature 签名："+ oauth_signature);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //生成请求头 Authorization
            //Authorization：OAuth oauth_version="1.0",oauth_nonce="2855288",oauth_timestamp="1685245629",oauth_consumer_key="0VhisrMCGswQ6OU4",
            // oauth_signature_method="HMAC-SHA1",oauth_signature="BCktvIcIOWCRWCaJVSQ6jDGP3cM%3D",xoauth_requestor_id="3067411"
            String authorization = "OAuth " +
                    "oauth_version=\"1.0\"," +
                    "oauth_nonce=\""+oauth_nonce+"\"," +
                    "oauth_timestamp=\""+oauth_timestamp+"\"," +
                    "oauth_consumer_key=\""+config.apps.get(appid).consumer_key+"\"," +
                    "oauth_signature_method=\"HMAC-SHA1\"," +
                    "oauth_signature=\""+oauth_signature+"\"," +
                    "xoauth_requestor_id=\""+opensocialViewerId+"\"";
            Map<String ,String > map = new HashMap<>();
            map.put("Authorization",authorization);
            logger.info("DMM 登录 请求头信息:"+map);
            String request = RequestUtil.requestSetRp(url+ "?fields=id,userType", map, "GET", null);
            logger.info("DMM 返回的request为：" + request);
            if (request != null) {
                JSONObject json = JSONObject.parseObject(request);
                String entry = json.getString("entry");
                UserPo userPo =  openUserBase(opensocialViewerId, pf, channel);
                userPo.setExt(entry);
                return userPo;
            }
        }
        return null;
    }
    @Cmd("/user/dmmUrlAnalysis")
    public void dmmUrlAnalysis(User sender, HttpRequestJSONObject params){
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        logger.info("DMM 解析转接数据 json：" + jsonObject);
        sender.sendAndDisconnect(jsonObject);
    }
    @Cmd("/dmm/addapp")
    public void addapp(User sender , HttpRequestJSONObject params){
        sender.sendAndDisconnect("OK");
    }
    @Cmd("/dmm/suspendapp")
    public void suspendapp(User sender , HttpRequestJSONObject params){
        sender.sendAndDisconnect("OK");
    }
    @Cmd("/dmm/resumeapp")
    public void resumeapp(User sender , HttpRequestJSONObject params){
        sender.sendAndDisconnect("OK");
    }
    @Cmd("/dmm/removeapp")
    public void removeapp(User sender , HttpRequestJSONObject params){
        sender.sendAndDisconnect("OK");
    }
    public static void main(String[] args) throws Exception {
        String  k = "RqtIBm%40Sg-y6m%40SaY1jbc1HNd7r_Ia-h&3a149aaae52337d59f33957486992047";
        String  p = "POST&https%3A%2F%2Fsbx-osapi.dmm.com%2Fsocial_sp%2Frest%2Fpayment%2F%40me%2F%40self%2F%40app&oauth_consumer_key%3D0VhisrMCGswQ6OU4%26oauth_nonce%3D2855288%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1685245629%26oauth_token%3D053f3d0f91554a30be4900ca4bc5e0ab%26oauth_version%3D1.0%26xoauth_requestor_id%3D3067411";
        String s = HmacSHA1Encryption.hmacSHA1Encrypt(p, k);
        System.out.println(s);
    }
}
