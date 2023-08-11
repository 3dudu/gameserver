package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.data.GlobalCache;
import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.LoginServer.po.PlayerPo;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.MD5Util;
import com.xuegao.LoginServer.vo.FacebookLoginResult;
import com.xuegao.LoginServer.vo.GOSUDataVo;
import com.xuegao.LoginServer.vo.X7V2DataVo;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections4.map.HashedMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class GOSULoginService extends  AbsLoginService{

    private static final String  LOGIN_VERIFY = "https://id.gosu.vn/auth/profile2";
    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        if (parameters.length == 3) {
            String channel_code = parameters[0]; //渠道号
            String pf = parameters[1];     //GOSUSDK
            String access_token  = parameters[2];

            String request = RequestUtil.request(LOGIN_VERIFY+"?access_token="+access_token);
            logger.info("GOSU SDK get请求url：" + LOGIN_VERIFY+"?access_token="+access_token );
            if (request != null) {
                JSONObject json = JSON.parseObject(request);
                return openUserBase(json.getString("UserID"), getPlatformName(), channel_code);
            }
        }
        return null;
    }

    @Cmd("/api/gosuRoleList")
    public void gosuRoleList(User sender, HttpRequestJSONObject params){
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject request = JSONObject.parseObject(json);
        logger.info("GOSU SDK 角色查询传入信息：" + request);

        String uid  = request.containsKey("uid") ? request.getString("uid") : null;
        long time = request.containsKey("time") ? request.getIntValue("time") : 0;
        String sid = request.containsKey("sid") ? request.getString("sid") : null;
        String sign = request.containsKey("sign") ? request.getString("sign") : null;

        if (sign == null || uid == null || time == 0) {
            sender.sendAndDisconnect(MsgFactory.getGOSUApiErrorMsg(0,"[缺失必要参数]"));
            return;
        }
        //获取配置参数
        AbsLoginService loginService = GlobalCache.fetchLoginService(Constants.PlatformOption.GOSUSDK.class.getSimpleName());
        Constants.PlatformOption.GOSUSDK config = loginService.getConfig();
        String private_key = config.private_key;
        if (private_key == null) {
            sender.sendAndDisconnect(MsgFactory.getGOSUApiErrorMsg(0,"[private_key 未配置]"));
            return;
        }
        List<GOSUDataVo> gosuDataVos = new ArrayList<>();
        GOSUDataVo gosuDataVo = null;
        String signStr = MD5Util.MD5Encode(  uid+time+private_key ,"UTF-8");
        logger.info( "计算签名private-key 为：" + private_key +"计算签名结果为：" +signStr + "传入签名为：" + sign);
        //校验签名
        if (signStr.equals(sign)) {
            JSONArray byPfUid = PlayerPo.findByPfUid(uid);
            if (byPfUid.size() <= 0) {
                sender.sendAndDisconnect(MsgFactory.getGOSUApiErrorMsg(1,gosuDataVos));
            }else {
                List<PlayerPo> playerPoList = byPfUid.toJavaList(PlayerPo.class);
                HashSet<PlayerPo> set = new HashSet<>(playerPoList);
                for (PlayerPo playerPo :set) {
                    gosuDataVo = new GOSUDataVo();
                    gosuDataVo.setGold(playerPo.getDiamond());
                    gosuDataVo.setCharid( String.valueOf(playerPo.getPid()));
                    gosuDataVo.setCharname(playerPo.getName());
                    gosuDataVo.setServerid(String.valueOf( playerPo.getSid()));
                    gosuDataVo.setLevel(String.valueOf( playerPo.getLevel()));
                    gosuDataVo.setServerName(playerPo.getServerName());
                    gosuDataVo.setServerStatus("0");
                    gosuDataVo.setVip(String.valueOf( playerPo.getVip()));
                    gosuDataVos.add(gosuDataVo);
                }
                sender.sendAndDisconnect(MsgFactory.getGOSUApiErrorMsg(1,gosuDataVos));
                logger.info("gosu 查询结束 查询内容为：" + gosuDataVos);
            }

        }else{
            sender.sendAndDisconnect(MsgFactory.getGOSUApiErrorMsg(-1,"[验签失败 sign error]"));
        }
    }
}
