package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.LoginServer.po.PlayerPo;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.vo.X7DataVo;
import com.xuegao.LoginServer.vo.X7V2DataVo;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;

import java.util.*;

public class X7GameSDKLoginService extends AbsLoginService {
    private static final String CHECK_LOGIN ="https://api.x7sy.com/user/check_v4_login";

    @Cmd("/api/x7GameRoleCallback")
    public void x7GameRoleCallback(User sender, HttpRequestJSONObject params){
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        logger.info("x7GameRoleCallback 参数："+jsonObject);
        String userStr = jsonObject.getString("user_str");
        String sign = jsonObject.getString("sign");
        String gameArea = jsonObject.getString("game_area");

        List<X7DataVo> userDataList = new ArrayList<>();
        X7DataVo x7DataVo = null;
        X7DataVo.GuidData guidData = null;
        X7DataVo.GuidData.AddData addData = null;

        if (userStr == null || "".equals(userStr)) {
            x7DataVo = new X7DataVo();
            x7DataVo.setGuid(userStr);
            List<X7DataVo.GuidData> guidDataList = new ArrayList<>();
            guidData = new X7DataVo.GuidData();
            guidData.setArea(gameArea);
            guidDataList.add(guidData);
            x7DataVo.setGuid_data(guidDataList);
            userDataList.add(x7DataVo);
            logger.info("[必要参数{user_str}没有设置]" + userStr);
            sender.sendAndDisconnect(MsgFactory.getX7ApiErrorMsg("-4","[必要参数{user_str}没有设置]" + userStr,userDataList));
            return;
        }
        if (sign == null || "".equals(sign)) {
            x7DataVo = new X7DataVo();
            x7DataVo.setGuid(userStr);
            List<X7DataVo.GuidData> guidDataList = new ArrayList<>();
            guidData = new X7DataVo.GuidData();
            guidData.setArea(gameArea);
            guidDataList.add(guidData);
            x7DataVo.setGuid_data(guidDataList);
            userDataList.add(x7DataVo);
            logger.info("[必要参数{sign}没有设置]" + sign);
            sender.sendAndDisconnect(MsgFactory.getX7ApiErrorMsg("-4","[必要参数{sign}没有设置]" + userStr,userDataList));
            return;
        }
        String[] userStrArray =  userStr.split("\\|");
        for (String string: userStrArray) {
          UserPo userPo =  UserPo.findByPfUid(string,"X7GameSDK");
            if (userPo.getId() == null ) {
                x7DataVo = new X7DataVo();
                x7DataVo.setGuid(string);
                List<X7DataVo.GuidData> guidDataList = new ArrayList<>();
                guidData = new X7DataVo.GuidData();
                guidData.setArea(gameArea);
                guidDataList.add(guidData);
                x7DataVo.setGuid_data(guidDataList);
                logger.info("该账号在雪糕平台未查询到角色 pf_user："+string);
                if (userStrArray.length == 1) {
                    userDataList.add(x7DataVo);
                    sender.sendAndDisconnect( MsgFactory.getX7ApiErrorMsg("0","唯一标识不存在",userDataList));
                    return;
                }
            }
            JSONArray playerPo = PlayerPo.findByUidAndArea(userPo.getId(),gameArea);
            if (playerPo.size() >0) {
                x7DataVo = new X7DataVo();
                x7DataVo.setGuid(string);
                List<X7DataVo.GuidData> guidDataList = new ArrayList<>();
                for(int i=0;i<playerPo.size();i++) {
                    guidData = new X7DataVo.GuidData();
                    guidData.setArea(playerPo.getJSONObject(i).getString("sid"));
                    guidData.setUsername(playerPo.getJSONObject(i).getString("name")==null ? "":playerPo.getJSONObject(i).getString("name"));
                    guidData.setLevel(playerPo.getJSONObject(i).getString("level") == null? "":playerPo.getJSONObject(i).getString("level"));
                    guidDataList.add(guidData);
                }
                x7DataVo.setGuid_data(guidDataList);
                userDataList.add(x7DataVo);
            }else {
                x7DataVo = new X7DataVo();
                x7DataVo.setGuid(string);
                List<X7DataVo.GuidData> guidDataList = new ArrayList<>();
                guidData = new X7DataVo.GuidData();
                guidData.setArea(gameArea);
                guidDataList.add(guidData);
                x7DataVo.setGuid_data(guidDataList);
                userDataList.add(x7DataVo);
            }

        }
        logger.info("小七角色信息回调正常返回，userDataList:"+userDataList);
        sender.sendAndDisconnect( MsgFactory.getX7ApiErrorMsg("0","",userDataList));
    }

    @Cmd("/api/x7GameRoleCallbackV2")
    public void x7GameRoleCallbackV2(User sender, HttpRequestJSONObject params){
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        logger.info("x7GameRoleCallbackV2 参数："+jsonObject);

        List<X7V2DataVo> x7V2DataVoList = new ArrayList<>();
        X7V2DataVo x7V2DataVo = new X7V2DataVo();
        JSONObject errorMsg = new JSONObject();
        HashSet<String > cargo = null;
        String roleId = jsonObject.containsKey("roleId") ? jsonObject.getString("roleId") : null;
        String guid = jsonObject.containsKey("guid") ? jsonObject.getString("guid") : null;
        String serverId = jsonObject.containsKey("serverId") ? jsonObject.getString("serverId") : null;
        JSONArray guids = new JSONArray();
        try {
            guids = jsonObject.containsKey("guids") ?  jsonObject.getJSONArray("guids") : new JSONArray();
        }catch (Exception e){
            errorMsg.put("respCode","FAIL");
            errorMsg.put("respMsg","[guids参数格式错误]");
            errorMsg.put("role",new JSONObject());
            errorMsg.put("guidRoles",x7V2DataVoList);
            sender.sendAndDisconnect( errorMsg );
            return;
        }
        /*if ((roleId == null || "".equals(roleId)) && (guid == null || "".equals(guid))) {
            errorMsg.put("respCode","FAIL");
            errorMsg.put("respMsg","[缺失必要参数]");
            errorMsg.put("role",new JSONObject());
            errorMsg.put("guidRoles",x7V2DataVoList);
            sender.sendAndDisconnect( errorMsg );
            return;
        }*/
        if (roleId != null && !roleId.equals("")) {
            String[] split = roleId.split("@");
            if (split.length <3) {
                errorMsg.put("respCode","FAIL");
                errorMsg.put("respMsg","[roleId格式错误]");
                errorMsg.put("role",new JSONObject());
                errorMsg.put("guidRoles",x7V2DataVoList);
                sender.sendAndDisconnect(errorMsg);
                return;
            }
            String server_id = split[0]; //区服id
            String role_id = split[1]; //角色id
            String gid = split[2]; //小七用户id
            PlayerPo playerPo  = PlayerPo.findByPfUidAndSid(gid,Integer.parseInt(server_id));
            if (playerPo != null) {
                logger.info("player:" + playerPo);
                x7V2DataVo = new X7V2DataVo();
                x7V2DataVo.setRoleCE(String.valueOf(playerPo.getBattle_power()));
                x7V2DataVo.setRoleId(roleId);
                x7V2DataVo.setGuid(gid);
                x7V2DataVo.setRoleLevel(String.valueOf( playerPo.getLevel()));
                x7V2DataVo.setRoleName(StringUtil.empty(playerPo.getName())? "" : playerPo.getName());
                x7V2DataVo.setRoleRechargeAmount(playerPo.getRoleRechargeAmount());
                x7V2DataVo.setRoleStage(StringUtil.empty(playerPo.getRoleStage()) ? "0-0" : playerPo.getRoleStage());
                x7V2DataVo.setServerName(StringUtil.empty( playerPo.getServerName()) ? "" : playerPo.getServerName());
                x7V2DataVo.setServerId(String.valueOf(playerPo.getSid()));
            }
            errorMsg.put("respCode","SUCCESS");
            errorMsg.put("respMsg","返回成功");
            errorMsg.put("role",x7V2DataVo);
        }else {
            errorMsg.put("respCode","SUCCESS");
            errorMsg.put("respMsg","返回成功");
            errorMsg.put("role",new JSONObject());
        }
        if (guids.size() > 0 || guid!=null) {
            guids.add(guid);
            cargo = new HashSet<>();
            for (int i = 0; i < guids.size(); i++) {
                String type = guids.getString(i);
                cargo.add(type);
            }
            if (serverId !=null && !"".equals(serverId) ) {
                for (String pfUid :cargo) {
                    PlayerPo playerPo  = PlayerPo.findByPfUidAndSid(pfUid,Integer.parseInt(serverId));
                    if (playerPo != null) {
                        x7V2DataVo = new X7V2DataVo();
                        x7V2DataVo.setRoleCE(String.valueOf(playerPo.getBattle_power()));
                        x7V2DataVo.setRoleId(playerPo.getSid() +"@"+playerPo.getPid() + "@"+pfUid);
                        x7V2DataVo.setGuid(pfUid);
                        x7V2DataVo.setRoleLevel(String.valueOf( playerPo.getLevel()));
                        x7V2DataVo.setRoleName(StringUtil.empty(playerPo.getName())  ? "" : playerPo.getName());
                        x7V2DataVo.setRoleRechargeAmount(playerPo.getRoleRechargeAmount());
                        x7V2DataVo.setRoleStage(StringUtil.empty(playerPo.getRoleStage()) ? "0-0" : playerPo.getRoleStage());
                        x7V2DataVo.setServerName(StringUtil.empty(playerPo.getServerName()) ? "" : playerPo.getServerName());
                        x7V2DataVo.setServerId(serverId);

                        x7V2DataVoList.add(x7V2DataVo);
                    }
                }
            }else {
                for (String pfUid :cargo) {
                    JSONArray playerPoArray  = PlayerPo.findByPfUid(pfUid);
                    if (playerPoArray.size()>0) {
                        List<PlayerPo> playerPoList = playerPoArray.toJavaList(PlayerPo.class);
                        HashSet<PlayerPo> set = new HashSet<>(playerPoList);
                        for (PlayerPo playerPo : set) {
                            logger.info("查询结果为"  + playerPo.toString());
                            x7V2DataVo = new X7V2DataVo();
                            x7V2DataVo.setRoleCE(String.valueOf(playerPo.getBattle_power()));
                            x7V2DataVo.setRoleId( playerPo.getSid() +"@"+playerPo.getPid() + "@"+pfUid );
                            x7V2DataVo.setGuid(pfUid);
                            x7V2DataVo.setRoleLevel(String.valueOf( playerPo.getLevel()));
                            x7V2DataVo.setRoleName(StringUtil.empty(playerPo.getName())  ? "" : playerPo.getName());
                            x7V2DataVo.setRoleRechargeAmount(playerPo.getRoleRechargeAmount());
                            x7V2DataVo.setRoleStage(StringUtil.empty(playerPo.getRoleStage()) ? "0-0" : playerPo.getRoleStage());
                            x7V2DataVo.setServerName(StringUtil.empty(playerPo.getServerName()) ? "" : playerPo.getServerName());
                            x7V2DataVo.setServerId(String.valueOf( playerPo.getSid() ));
                            x7V2DataVoList.add(x7V2DataVo);
                        }

                    }
                }
            }
        }
        errorMsg.put("guidRoles",x7V2DataVoList);
        logger.info("小七角色查询接口完成，查询内容为：" + errorMsg);
        sender.sendAndDisconnect(errorMsg);

    }
    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //获取配置参数
        Constants.PlatformOption.X7GameSDK config = getConfig();

        if (parameters.length == 3) {
            String channel = parameters[0]; //渠道号
            String pf = parameters[1];      //X7GameSDK
            String tokenkey = parameters[2];
            if (config.apps.get(channel) == null) {
                logger.info("小七后台参数 channel 未配置"+channel);
                return null;
            }
            String appkey = config.apps.get(channel).appKey;
            if (appkey == null) {
                logger.info("小七后台参数 appkey 未配置"+appkey);
                return null;
            }
            String signStr = appkey+tokenkey;
            String sign = Misc.md5(signStr).toLowerCase();
            String postData = "tokenkey="+tokenkey+"&sign="+sign;
            String request = RequestUtil.requestWithPost(CHECK_LOGIN, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("X7GameSDK请求的参数postData:"+postData+"返回的参数request："+request);
            if (request != null) {
                JSONObject json = JSONObject.parseObject(request);
                logger.info("json:"+json.toString());
                if (json.getIntValue("errorno")==0) {
                    JSONObject dataJson = json.getJSONObject("data");
                    String guid = dataJson.getString("guid");
                    return openUserBase(guid, getPlatformName(),channel);
                }
            }

        }
        return null;
    }
}
