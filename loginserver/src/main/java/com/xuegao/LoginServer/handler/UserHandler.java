package com.xuegao.LoginServer.handler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.xuegao.LoginServer.redisData.MemAntilnduigedInfoList;
import com.xuegao.LoginServer.util.AESUtils;
import com.xuegao.LoginServer.util.IDCardHelper;
import com.xuegao.LoginServer.util.MD5Util;
import com.xuegao.LoginServer.vo.AntiIndulgedInfo;
import com.xuegao.core.util.*;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants.ErrorCode;
import com.xuegao.LoginServer.data.Constants.PlatformOption.AnySdk;
import com.xuegao.LoginServer.data.DBManager;
import com.xuegao.LoginServer.data.GlobalCache;
import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.LoginServer.log.ElkLog;
import com.xuegao.LoginServer.log.GuideLog;
import com.xuegao.LoginServer.log.LogConstants;
import com.xuegao.LoginServer.log.SLogService;
import com.xuegao.LoginServer.loginService.AbsLoginService;
import com.xuegao.LoginServer.po.LoginLogPo;
import com.xuegao.LoginServer.po.PlayerPo;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.redisData.MemLoginTokenBean;
import com.xuegao.LoginServer.vo.IdPlatform;
import com.xuegao.LoginServer.vo.LoginLogVo;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.SnowflakeIdWorker;
import com.xuegao.core.util.StringUtil;

import io.netty.util.CharsetUtil;

public class UserHandler {
    static Logger logger = Logger.getLogger(UserHandler.class);
    public static final Integer HISTORY_COUNT = 4;

    public static String VERIFIED_APPCODE = PropertiesUtil.get("verified_appcode");
    public static String CHECK_APPID = PropertiesUtil.get("check_appId");
    public static String BIZID = PropertiesUtil.get("bizId");
    public static String SECRETKEY = PropertiesUtil.get("secretkey");
    public static String EYOU_SIGNKEY = PropertiesUtil.get("eyou_signKey");
    public static String URL = "https://api.wlc.nppa.gov.cn";
    /**
     * 新手引导日志
     */
    @Cmd("/user/log")
    public void reloadData(User sender, JSONObject params) {
        String tid = params.getString("tid");
        String tname = params.getString("tname");
        String deviceId = params.getString("deviceId");
        String imei = params.getString("imei");
        String idfa = params.getString("idfa");
        String userId = params.getString("u");
        if (StringUtil.empty(tid) || StringUtil.empty(tname) || StringUtil.empty(deviceId)) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.PARAMETER_ERROR));
            return;
        }
        InetSocketAddress insocket = (InetSocketAddress)sender.getChannel().remoteAddress();
        GuideLog log = new GuideLog(tid, tname, insocket.getAddress().getHostAddress());
        if (!StringUtil.empty(idfa)) {
            log.setIdfa(idfa);
        }
        if (!StringUtil.empty(imei)) {
            log.setImei(imei);
        }
        log.setDeviceId(deviceId);
        if (!StringUtil.empty(userId)) {
            log.setUserId(userId);
        }
        SLogService.log_elk((JSONObject)JSON.toJSON(log));
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        rs.put("msg", "success");
        sender.sendAndDisconnect(rs);
    }

    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }


    /**
     * 用户名，密码注册
     */
    @Cmd("/user/register")
    public void register(User sender, HttpRequestJSONObject params) {

        String u = params.getString("u");
        String p = params.getString("p");
        String name = params.getString("n");
        String idCard = params.getString("id");
        String channel = params.getString("channelCode");
        String userStatus = params.getString("us");//用户状态0-游客 1-非游客
        if(channel!=null){
            if(channel.equals("14501")||channel.equals("34511")||channel.equals("34515")){
                sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.SYSTEM_ERROR));
                return;
            }
        }
        // 1.检查用户名是否存下
        UserPo userPo = UserPo.findByUname(u);
        if (userPo != null) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.NAME_EXIST));
            return;
        }
        // 创建新用户
        UserPo user = new UserPo();
        user.setCreate_time(new Date());
        user.setName(u);
        user.setPasswd(Misc.md5(p));
        user.setPf("JUser");
        if (channel == null) {
            channel = "0";
        }
        user.setUserStatus(StringUtil.empty(userStatus)?null:Integer.valueOf(userStatus));
        user.setIdentety_status(2); //未实名认证
        user.setChannelCode(channel);
        // long userId=user.insertToDBAndGetId();
        Long userId = SnowflakeIdWorker.getInstance().nextId();
        user.setId(userId);
        user.setPf_user("" + userId);
        if (!StringUtil.empty(idCard)&&!StringUtil.empty(name)){
            user.setIdCard(idCard);
            user.setRealName(name);
            long timestamps = System.currentTimeMillis();
            Map<String, String> map = new HashMap<>();
            map.put("appId",CHECK_APPID);
            map.put("bizId",BIZID);
            map.put("timestamps",timestamps+"");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ai",userId);
            jsonObject.put("name",name);
            jsonObject.put("idNum",idCard);
            String data = AESUtils.encrypt(jsonObject.toString(),SECRETKEY);
            JSONObject postdata = new JSONObject();
            postdata.put("data",data);
            String sign = getSHA256(SECRETKEY+"appId"+CHECK_APPID+"bizId"+BIZID+"timestamps"+timestamps +postdata);
            map.put("sign",sign);
            logger.info("实名认证参数：" + "CHECK_APPID = " + CHECK_APPID + "BIZID = " + BIZID + "SECRETKEY = " + SECRETKEY );
            logger.info("timestamps="+timestamps  + "sign =" +sign);
            try {
                String request = RequestUtil.requestWithPost(URL+"/idcard/authentication/check", map,postdata.toString());
                if (StringUtil.empty(request) || JSONObject.parseObject(request).getIntValue("errcode") != 0) {
                    sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.VERIFIED));
                    return;
                }
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    if (json.getIntValue("errcode") == 0) {
                        JSONObject result = json.getJSONObject("data").getJSONObject("result");
                        if (ObjectUtil.isNotEmpty(result)){
                            user.setIdentety_status(result.getIntValue("status"));
                            user.setPi(result.getString("pi"));
                        }
                    }else {
                        user.setIdCard("");
                        user.setRealName("");
                        user.setIdentety_status(2);
                    }
                }
            } catch (Exception e) {
                logger.info(e.toString());
                user.setIdentety_status(3);
            }
        }else {
            user.setIdCard("");
            user.setRealName("");
        }
        // user.updateToDB_WithSync();
        user.insertToDB();

        // 返回
        JSONObject rs = new JSONObject();
        rs.put("uid", "" + userId);
        rs.put("age",getAge(idCard));
        rs.put("status",user.getIdentety_status());
        user.onRegist();
        ElkLog log = new ElkLog(LogConstants.KindLog.addUserLog, user);
        log.setUid(user.getId() == null ? "0" : user.getId().toString());
        SLogService.new_log_elk((JSONObject)JSON.toJSON(log));
        sender.sendAndDisconnect(rs);
    }

    /**
     * 账号绑定
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/bind")
    public void bind(User sender, HttpRequestJSONObject params) {
        String username = params.getString("u");
        String password = params.getString("p");
        String newUsername = params.getString("nu");
        String newPassword = params.getString("np");
        // 1校验用户名密码是否正确
        UserPo userPo = UserPo.findByUname(username);
        if (userPo == null) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.ACCOUNT_NOT_EXIST));
            return;
        }
        if (!userPo.getPasswd().equals(Misc.md5(password))) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.WRONG_PASSWORD));
            return;
        }
        // 校验新账户是否存在
        UserPo newUserPo = UserPo.findByUname(newUsername);
        if (newUserPo != null) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.NAME_EXIST));
            return;
        }
        userPo.setName(newUsername);
        userPo.setPasswd(Misc.md5(newPassword));
        //绑定账号之后设置为非游客账号
        userPo.setUserStatus(1);
        userPo.updateToDB();
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        sender.sendAndDisconnect(rs);
    }

    /**
     * 快速登录
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/fastInfo")
    public void fastInfo(User sender, HttpRequestJSONObject params) {
        String token = params.getString("t");
        MemLoginTokenBean memLoginTokenBean = new MemLoginTokenBean(token);
        IdPlatform idPlatform = memLoginTokenBean.get();
        if (idPlatform == null) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg());
            return;
        }
        UserPo userPo = UserPo.findByUId(idPlatform.id);
        logger.info("olduser:" + userPo.toString());
        Long userName = SnowflakeIdWorker.getInstance().nextId();
        String password = UUID.randomUUID().toString().replaceAll("-", "");
        userPo.setName("" + userName);
        userPo.setPasswd(Misc.md5(password));
        userPo.updateToDB();
        logger.info("newuser:" + userPo.toString());
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        rs.put("u", "" + userName);
        rs.put("p", password); // 客户端处理长整型精度丢失改为字符串
        logger.info("username:" + userName + "password:" + password);
        sender.sendAndDisconnect(rs);
    }

    /**
     * 实名认证
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/Verified")
    public void Verified(User sender, HttpRequestJSONObject params) {
        String token = params.getString("t");
        String name = params.getString("n");
        String idCard = params.getString("id");
        MemLoginTokenBean memLoginTokenBean = new MemLoginTokenBean(token);
        IdPlatform idPlatform = memLoginTokenBean.get();
        logger.info("username:" + name + "idCard:" + idCard);
        if (idPlatform == null) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg());
            return;
        }
        UserPo userPo = UserPo.findByUId(idPlatform.id);
        if (!StringUtil.empty(idCard)&&!StringUtil.empty(name)){
            userPo.setIdCard(idCard);
            userPo.setRealName(name);
            long timestamps = System.currentTimeMillis();
            Map<String, String> map = new HashMap<>();
            map.put("appId",CHECK_APPID);
            map.put("bizId",BIZID);
            map.put("timestamps",timestamps+"");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ai",userPo.getId());
            jsonObject.put("name",name);
            jsonObject.put("idNum",idCard);
            String data = AESUtils.encrypt(jsonObject.toString(),SECRETKEY);
            JSONObject postdata = new JSONObject();
            postdata.put("data",data);
            String sign = getSHA256(SECRETKEY+"appId"+CHECK_APPID+"bizId"+BIZID+"timestamps"+timestamps +postdata);
            map.put("sign",sign);
            try {
                String request = RequestUtil.requestWithPost(URL+"/idcard/authentication/check", map,postdata.toString());
                logger.info("实名认证返回："+ request);
                if (StringUtil.empty(request) || JSONObject.parseObject(request).getIntValue("errcode") != 0) {
                    sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.VERIFIED));
                    return;
                }
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    if (json.getIntValue("errcode") == 0) {
                        JSONObject result = json.getJSONObject("data").getJSONObject("result");
                        if (ObjectUtil.isNotEmpty(result)){
                            userPo.setIdentety_status(result.getIntValue("status"));
                            userPo.setPi(result.getString("pi"));
                        }
                    }
                }
            } catch (Exception e) {
                logger.info(e);
                userPo.setIdentety_status(3);
            }
        }
        userPo.updateToDB();
        JSONObject rs = new JSONObject();
        rs.put("age",getAge(idCard));
        rs.put("status",userPo.getIdentety_status());
        sender.sendAndDisconnect(rs);
        //sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.VERIFIED));
        //rs.put("u", "" + userName);
        //rs.put("p", password); // 客户端处理长整型精度丢失改为字符串
        //logger.info("username:" + userName + "password:" + password);
    }


    public static int getAge(String idCard){
        if (StringUtil.empty(idCard)){
            return 0;
        }
        IDCardHelper ih = new IDCardHelper();
        boolean b1 = ih.isValidatedAllIdcard(idCard);
        if(ih.is15Idcard(idCard)){
            idCard=ih.convertIdcarBy15bit(idCard);
        }
        int year = Integer.parseInt(idCard.substring(6, 10));
        int month = Integer.parseInt(idCard.substring(10, 12));
        int day = Integer.parseInt(idCard.substring(12, 14));
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - year;
        int monthMinus = monthNow - month;
        int dayMinus = dayNow - day;

        int age = yearMinus;// 先大致赋值
        if (yearMinus < 0) {// 选了未来的年份
            age = 0;
        } else if (yearMinus == 0) {// 同年的，要么为1，要么为0
            if (monthMinus < 0) {// 选了未来的月份
                age = 0;
            } else if (monthMinus == 0) {// 同月份的
                if (dayMinus < 0) {// 选了未来的日期
                    age = 0;
                } else if (dayMinus >= 0) {
                    age = 1;
                }
            } else if (monthMinus > 0) {
                age = 1;
            }
        } else if (yearMinus > 0) {
            if (monthMinus < 0) {// 当前月>生日月
                age = age - 1;
            } else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
                if (dayMinus < 0) {
                    age = age - 1;
                }
            }
        }
        return age;
    }

    /**
     * 用户名，密码登录
     */
    @Cmd("/user/login")
    public void login(User sender, JSONObject params) {
        String u = params.getString("u");
        String p = params.getString("p");
        // 1.检查用户名是否存下
        UserPo userPo = UserPo.findByUname(u);
        if (userPo == null) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.ACCOUNT_NOT_EXIST));
            return;
        }
        // 2.检查密码是否一致
        if (!userPo.getPasswd().equals(Misc.md5(p))) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.WRONG_PASSWORD));
            return;
        }
        // 生成token,保存至redis
        String token = StringUtil.fetchUniqStr_32();
        long time = userPo.saveToken(token);

        if (userPo.getIdentety_status()==1){
            long timestamps = System.currentTimeMillis();
            Map<String, String> param = new HashMap<>();
            param.put("ai",userPo.getId()+"");
            String sign = getSHA256(SECRETKEY+"ai"+userPo.getId()+"appId"+CHECK_APPID+"bizId"+BIZID+"timestamps"+timestamps);
            Map<String, String> map = new HashMap<>();
            map.put("appId",CHECK_APPID);
            map.put("bizId",BIZID);
            map.put("timestamps",timestamps+"");
            map.put("sign",sign);
            try {
                String request = RequestUtil.requestWithGet("http://api2.wlc.nppa.gov.cn/idcard/authentication/query", map,param);
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    if (json.getIntValue("errcode") == 0) {
                        JSONObject result = json.getJSONObject("result");
                        if (ObjectUtil.isNotEmpty(result)){
                            userPo.setIdentety_status(result.getIntValue("status"));
                            userPo.setPi(result.getString("pi"));

                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                userPo.setIdentety_status(3);; //请求失败
            }
        }
        if (userPo.getIdentety_status()==3){
            long timestamps = System.currentTimeMillis();
            Map<String, String> map = new HashMap<>();
            map.put("appId",CHECK_APPID);
            map.put("bizId",BIZID);
            map.put("timestamps",timestamps+"");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ai",userPo.getId());
            jsonObject.put("name",userPo.getRealName());
            jsonObject.put("idNum",userPo.getIdCard());
            String data = AESUtils.encrypt(jsonObject.toString(),SECRETKEY);
            JSONObject postdata = new JSONObject();
            postdata.put("data",data);
            String sign = getSHA256(SECRETKEY+"appId"+CHECK_APPID+"bizId"+BIZID+"timestamps"+timestamps +postdata);
            map.put("sign",sign);
            try {
                String request = RequestUtil.requestWithPost(URL+"/idcard/authentication/check", map,postdata.toString());
                if (StringUtil.empty(request) || JSONObject.parseObject(request).getIntValue("errcode") != 0) {
                    sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.VERIFIED));
                    return;
                }
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    if (json.getIntValue("errcode") == 0) {
                        JSONObject result = json.getJSONObject("data").getJSONObject("result");
                        if (ObjectUtil.isNotEmpty(result)){
                            userPo.setIdentety_status(result.getIntValue("status"));
                            userPo.setPi(result.getString("pi"));
                        }
                    }else {
                        userPo.setIdCard("");
                        userPo.setRealName("");
                        userPo.setIdentety_status(2);
                    }
                }
            } catch (Exception e) {
                logger.info(e.toString());
                userPo.setIdentety_status(3);
            }
        }
        userPo.updateToDB();
        // 返回
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        rs.put("token", token);
        rs.put("uid", "" + userPo.getId());
        // 下发历史登录信息
        JSONArray history = PlayerPo.findSidByUidAndDate(userPo.getId(), HISTORY_COUNT);
        rs.put("history", history);
        rs.put("us",userPo.getUserStatus());
        rs.put("age",getAge(userPo.getIdCard()));
        rs.put("time",System.currentTimeMillis());
        rs.put("logintime",time);
        rs.put("status",userPo.getIdentety_status()); //0：认证成功 1：认证中 2：认证失败
        sender.sendAndDisconnect(rs);
        try {
            if (!StringUtil.empty(userPo.getIdCard())){
                long timestamps = System.currentTimeMillis();
                MemAntilnduigedInfoList memAntilnduigedInfoList = new MemAntilnduigedInfoList();
                AntiIndulgedInfo antiIndulgedInfo = new AntiIndulgedInfo();
                antiIndulgedInfo.si =userPo.getId()+"";
                antiIndulgedInfo.bt = 0;
                antiIndulgedInfo.ot =timestamps/1000;
                antiIndulgedInfo.ct = userPo.getIdentety_status()==0?0:2;
                if (userPo.getIdentety_status()==0){
                    antiIndulgedInfo.pi = userPo.getPi();
                }else {
                    antiIndulgedInfo.di =userPo.getId()+"";
                }
                memAntilnduigedInfoList.add(0,antiIndulgedInfo);
                logger.info("用户登入："+antiIndulgedInfo.toString());
            }
            userPo.onLogin();
            ElkLog log = new ElkLog(LogConstants.KindLog.userLoginLog, userPo);
            log.setUid(userPo.getId() == null ? "0" : userPo.getId().toString());
            SLogService.new_log_elk((JSONObject)JSON.toJSON(log));
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    public static void main(String[] args) {


        String   secret = "b932657b019d34b4e26ab3ec7ba0b7b5";
        String   appid = "b45a6ff862924893905d0ee9bf45166e";
        String   bizId = "1101999999";
        long timestamps = System.currentTimeMillis();



        JSONArray collections = new JSONArray();
        JSONObject jsonObject3 = new JSONObject();
        JSONObject collection = new JSONObject();

        collection.put("pi" ,"1fffbkrwndszes1sngfx3v6pdqh87fi4zhz9ur");
        collection.put("no",1);
        collection.put("si", 24);
        collection.put("bt",1); //上线
        collection.put("ot",1661429602);
        collection.put("ct",0);
        collection.put("di","");

        collections.add(collection);
        jsonObject3.put("collections",collections);

        String data3 = AESUtils.encrypt(jsonObject3.toString(),secret);
        JSONObject postdata3 = new JSONObject();
        postdata3.put("data",data3);

        String sign3 = getSHA256(secret+"appId"+appid+"bizId"+bizId+"timestamps"+timestamps+postdata3);

        System.out.println(
            "time =" + timestamps + "    "  +   sign3  +
            "\n  postdate = " + postdata3.toString()
        );
    }

    /**
     * 校验登录token
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/validate")
    public void validate(User sender, JSONObject params) {
        String token = params.getString("token");
        MemLoginTokenBean memLoginTokenBean = new MemLoginTokenBean(token);
        IdPlatform idPlatform = memLoginTokenBean.get();
        if (idPlatform == null) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg());
            return;
        }
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        rs.put("id", idPlatform.id);
        rs.put("platform", idPlatform.platform);
        rs.put("openid", idPlatform.pf_user);
        sender.sendAndDisconnect(rs);
    }


    /**
     * eyou玩家等级接口
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/userlevel")
    public void userlevel(User sender, HttpRequestJSONObject params) {
        SortedMap<String, Object> playerMap = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            playerMap.put(entry.getKey(), entry.getValue());
        }
        //验签
        if (!playerMap.get("sign").equals(createSign("UTF-8", playerMap,EYOU_SIGNKEY))){
            logger.info( "EyouSDK:验签失败sign:"+playerMap.get("sign")+ ",createSign:"+ createSign("UTF-8", playerMap,EYOU_SIGNKEY) );
            return;
        }
        JSONObject rs = new JSONObject();
        rs.put("Code","0");
        rs.put("Reason","");
        String errorStr = "";
        UserPo userPo = UserPo.findByPfUid(params.getString("uid"),"EyouSDK");
        if (userPo == null){
            errorStr = "用户不存在";
            logger.info(errorStr);
            rs.put("Reason",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        PlayerPo playerPo = PlayerPo.findByUidAndSid(userPo.getId(),params.getIntValue("serverid"));
        if (playerPo == null){
            errorStr = "该大区不存在角色";
            logger.info(errorStr);
            rs.put("Reason",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        rs.put("create_time",df.format(playerPo.getDate_create()));
        rs.put("level",playerPo.getLevel());
        rs.put("total_amount",0f);
        rs.put("currency","USD");
        rs.put("Code",1);
        sender.sendAndDisconnect(rs);
    }


    /**
     * eyou角色列表接口
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/playerlist")
    public void playerlist(User sender, HttpRequestJSONObject params) {
        SortedMap<String, Object> playerMap = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            playerMap.put(entry.getKey(), entry.getValue());
        }
        //验签
        if (!playerMap.get("sign").equals(createSign("UTF-8", playerMap,EYOU_SIGNKEY))){
            logger.info( "EyouSDK:验签失败sign:"+playerMap.get("sign")+ ",createSign:"+ createSign("UTF-8", playerMap,EYOU_SIGNKEY) );
            return;
        }
        UserPo userPo = UserPo.findByPfUid(params.getString("uid"),"EyouSDK");
        JSONObject rs = new JSONObject();
        rs.put("code","0");
        rs.put("str","");
        String errorStr = "";
        if (userPo == null){
            errorStr = "用户不存在";
            logger.info(errorStr);
            rs.put("str",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        PlayerPo playerPo = PlayerPo.findByUidAndSid(userPo.getId(),params.getIntValue("s_id"));
        if (playerPo == null){
            errorStr = "该大区不存在角色";
            logger.info(errorStr);
            rs.put("str",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        rs.put("code", 1);
        JSONObject msg = new JSONObject();
        JSONArray user_list = new JSONArray();
        JSONObject player = new JSONObject();
        player.put("user_id",String.valueOf(playerPo.getPid()));
        player.put("user_name",playerPo.getName());
        user_list.add(player);
        msg.put("user_list",user_list);
        rs.put("msg",msg);
        sender.sendAndDisconnect(rs);
    }

    public static String createSign(String characterEncoding, SortedMap<String, Object> parameters, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)&!"sign".equals(k)) {
                sb.append(v);
            }
        }
        sb.append(API_KEY);
        logger.info("createSign:"+sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding);
        return sign;
    }

    /**
     * 校验登录token
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/logout")
    public void logout(User sender, JSONObject params) {
        Long id = params.getLong("userId");
        Long time = params.getLong("time");
        if (id == null || time ==null) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg());
            return;
        }
        UserPo userPo = UserPo.findByUId(id);
        if (userPo == null) {
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.ACCOUNT_NOT_EXIST));
            return;
        }
        sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
        try {
            String token = userPo.logout(time);
            if (!StringUtil.empty(userPo.getIdCard())){
                //上报国家登录
                long timestamps = System.currentTimeMillis();
                MemAntilnduigedInfoList memAntilnduigedInfoList = new MemAntilnduigedInfoList();
                AntiIndulgedInfo antiIndulgedInfo = new AntiIndulgedInfo();
                antiIndulgedInfo.si =userPo.getId()+"";
                antiIndulgedInfo.bt = 0;
                antiIndulgedInfo.ot =timestamps/1000;
                antiIndulgedInfo.ct = userPo.getIdentety_status()==0?0:2;
                if (userPo.getIdentety_status()==0){
                    antiIndulgedInfo.pi = userPo.getPi();
                }else {
                    antiIndulgedInfo.di =userPo.getId()+"";
                }
                memAntilnduigedInfoList.add(0,antiIndulgedInfo);
                logger.info("用户登入："+antiIndulgedInfo.toString());
            }
        }catch (Exception e){
            logger.info(e.getMessage());
        }

    }

    /**
     * 平台登录
     *
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/user/loginIdentity")
    public void loginIdentity(User sender, HttpRequestJSONObject params) throws Exception {
        String identity = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        logger.info("identity=" + identity);
        String[] parameters = StringUtil.split(identity, ",");
        if (parameters.length == 0) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg());
            return;
        }
        String pf = parameters[1];
        // 平台登录参数验证,并获得该平台用户对象
        AbsLoginService loginService = GlobalCache.fetchLoginService(pf);
        if (loginService == null) {
            logger.info("平台登录失败:没有找到登录服务类,identity=" + identity);
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.SYSTEM_ERROR));
            return;
        }
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        UserPo userPo = loginService.loginUser(parameters, rs);
        if (userPo == null) {
            logger.info("平台登录失败:认证失败,identity=" + identity);
            sender.sendAndDisconnect(MsgFactory.getErrorMsg(ErrorCode.LOGIN_VALID_FAIL));
            return;
        }
        // 创建token,写入redis
        String token = StringUtil.fetchUniqStr_32();
        long time = userPo.saveToken(token);
        // 返回
        rs.put("token", token);
        rs.put("uid", "" + userPo.getId());
        // 返回第三方账号
        rs.put("pf_user", userPo.getPf_user());
        // 下发历史登录信息
        JSONArray history = PlayerPo.findSidByUidAndDate(userPo.getId(), HISTORY_COUNT);
        rs.put("history", history);
        rs.put("age",getAge(userPo.getIdCard()));
        rs.put("time",System.currentTimeMillis());
        rs.put("logintime",time);
        rs.put("createtime",userPo.getCreate_time());
        rs.put("status",userPo.getIdentety_status()); //0：认证成功 1：认证中 2：认证失败
        rs.put("content",userPo.getExt() == null ? "" : userPo.getExt());
        ElkLog log = new ElkLog(LogConstants.KindLog.userLoginLog, userPo);
        log.setUid(userPo.getId() == null ? "0" : userPo.getId().toString());
        SLogService.new_log_elk((JSONObject)JSON.toJSON(log));
        logger.info("平台登录成功:userId=" + userPo.getId() + ",identity=" + identity);
        sender.sendAndDisconnect(rs);
    }

    /**
     * anysdk登录方式
     *
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/user/AnySdk/login")
    public void anySdkLogin(User sender, HttpRequestJSONObject params) {
        if (!(params.containsKey("channel") && params.containsKey("uapi_key") && params.containsKey("uapi_secret"))) {
            sender.sendAndDisconnect("parameter not complete");
            return;
        }
        AbsLoginService loginService = GlobalCache.fetchLoginService(AnySdk.class.getSimpleName());
        AnySdk config = loginService.getConfig();

        Set<Entry<String, Object>> entrySet = params.entrySet();
        Map<String, String> map = new HashMap<>();
        for (Entry<String, Object> entry : entrySet) {
            map.put(entry.getKey(), "" + entry.getValue());
        }
        if (!map.containsKey("private_key")) {
            map.put("private_key", config.merchantKey);
        }
        String post = StringUtil.fetchUrlParamStr(map, false);
        String result = RequestUtil.requestWithPost(config.loginCheckUrl, post.getBytes(CharsetUtil.UTF_8));
        if (result.startsWith("\uFEFF")) {
            result = result.substring(1);
        }
        JSONObject resultObject = JSON.parseObject(result);
        String status = resultObject.getString("status");
        if ("ok".equals(status)) {
            JSONObject common = resultObject.getJSONObject("common");
            String uid = common.getString("uid");
            String channel = common.getString("channel");
            String pf = loginService.getPlatformName() + "@" + channel;

            String server_ext_for_login = map.get("server_ext_for_login");
            JSONObject json = JSON.parseObject(server_ext_for_login);
            String cnl = json.getString("channelCode");
            UserPo userPo = loginService.openUserBase(uid, pf, cnl);
            // 生成token,保存至redis
            String token = StringUtil.fetchUniqStr_32();
            userPo.saveToken(token);
            // 返回
            // JSONObject rs = MsgFactory.getDefaultSuccessMsg();
            // 20190108 跟刘毅协定 anysdk登录返回为 uid#32位token
            JSONObject extObj = new JSONObject();
            extObj.put("uid", "" + userPo.getId());
            extObj.put("token", token);
            // 下发历史登录信息
            JSONArray history = PlayerPo.findSidByUidAndDate(userPo.getId(), HISTORY_COUNT);
            extObj.put("history", history);
            resultObject.put("ext", extObj.toString());
            ElkLog log = new ElkLog(LogConstants.KindLog.userLoginLog, userPo);
            log.setUid(userPo.getId() == null ? "0" : userPo.getId().toString());
            SLogService.new_log_elk((JSONObject)JSON.toJSON(log));
            sender.sendAndDisconnect(resultObject);
            return;
        }
        sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg());
    }

    /**
     * 登录日志
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/loginLog")
    public void loginLog(User sender, HttpRequestJSONObject params) {
        String str = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        LoginLogVo vo = JSON.parseObject(str, LoginLogVo.class);
        // 1.插入登录日志
        LoginLogPo loginLogPo = new LoginLogPo();
        Date date = new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        loginLogPo.setCreate_date(dateStr);
        loginLogPo.setCreate_time(new Date());
        loginLogPo.setPid(vo.pid);
        loginLogPo.setPlatform(vo.platform);
        loginLogPo.setReg(vo.reg);
        loginLogPo.setSid(vo.sid);
        loginLogPo.setUid(vo.uid);
        loginLogPo.insertToDB_WithSync();
        // 2.插入或修改player信息
        PlayerPo playerPo = PlayerPo.findByUidAndSid(vo.uid, vo.sid);
        if (playerPo == null) {
            playerPo = new PlayerPo();
            playerPo.setUid(vo.uid);
            playerPo.setSid(vo.sid);
        }
        playerPo.setPid(vo.pid);
        playerPo.setArea(vo.area);
        playerPo.setBattle_power(vo.battle_power);
        playerPo.setDiamond(vo.diamond);
        playerPo.setLevel(vo.level);
        playerPo.setMoney(vo.money);
        playerPo.setVip(vo.vip);
        playerPo.setName(vo.name);
        playerPo.setRoleStage(vo.roleStage);
        playerPo.setRoleRechargeAmount(vo.roleRechargeAmount);
        playerPo.setServerName(vo.serverName);
        playerPo.setDate_create(new Date());
        playerPo.updateToDB_WithSync();
        if (playerPo.getId() == null) {
            playerPo.insertToDB_WithSync();
        } else {
            playerPo.updateToDB_WithSync();
        }
        sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
    }

    /**
     * 是否拥有角色
     *
     * @param sender
     * @param params
     */
    @Cmd("/user/hasPlayer")
    public void hasPlayer(User sender, HttpRequestJSONObject params) {
        Long uid = params.getLong("uid");
        String sql = "select count(1) from player where uid =?";
        Long num = DBManager.getDB().queryForLong(sql, uid);
        JSONObject rs = MsgFactory.getDefaultSuccessMsg();
        rs.put("hasPlayer", num > 0);
        sender.sendAndDisconnect(rs);
    }

    @Cmd("/refresh")
    public void refresh(User sender, HttpRequestJSONObject params) {
        GlobalCache.reload();
        sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
    }
    @Cmd("/user/loginOff")
    public void loginOff(User sender, HttpRequestJSONObject params){
        String token = params.getString("t");
        MemLoginTokenBean memLoginTokenBean = new MemLoginTokenBean(token);
        IdPlatform idPlatform = memLoginTokenBean.get();
        if (idPlatform == null) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg());
            return;
        }
        UserPo userPo = UserPo.findByUId(idPlatform.id);
        logger.info("olduser:" + userPo.toString());

        //判断用户是否已经注销
        if (userPo.getLoginOff() ||  userPo.getPf_user().equals("")) {
            sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
            logger.info("用户已经注销/第三方用户已经注销");
        }
        //第三方sdk 解除角色绑定
        if (!"JUser".equals( userPo.getPf()) ){
            userPo.setPf_user("");
            logger.info(userPo.getPf_user() + " 平台用户解绑成功");
        }else {
            //修改注销状态 ，并将用户名注销
            long nextId = SnowflakeIdWorker.getInstance().nextId();
            userPo.setName(String.valueOf(nextId) );
        }
        userPo.setLoginOff(true);
        userPo.updateToDB();
        sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
    }

}
