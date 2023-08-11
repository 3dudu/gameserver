package com.icegame.third_party.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.icegame.framework.utils.MyDateUtil;
import com.icegame.sysmanage.entity.ServerNodes;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.XQMail;
import com.icegame.sysmanage.service.IPropertiesService;
import com.icegame.sysmanage.service.IServerNodesService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.IXQMailService;
import com.icegame.third_party.config.XQApiMethod;
import com.icegame.third_party.config.XQResponseType;
import com.icegame.third_party.config.XQ_Config;
import com.icegame.third_party.entity.XQRequest;
import com.icegame.third_party.entity.XQResponse;
import com.icegame.third_party.entity.XQShop;
import com.icegame.third_party.security.XQRSA;
import com.icegame.third_party.service.XQService;
import com.icegame.third_party.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import java.util.Date;

@Service
public class XQServiceImpl implements XQService {

    @Autowired
    private ISlaveNodesService slaveNodesService;

    @Autowired
    private IXQMailService xqMailService;

    @Autowired
    private IServerNodesService serverNodesService;

    @Autowired
    private IPropertiesService propertiesService;

    private static final Logger LOG = LoggerFactory.getLogger(XQServiceImpl.class);

    private static final String OSS_PATH = "https://gamescn.xuegaogame.com/sx_acg/icons/";
    private static final String IMAGE_SUFFIX = "_b.png";

    @Override
    public XQResponse dealArgs(XQRequest request) {
        XQResponse response = new XQResponse().fromRequest(request);
        JSONObject bizResp = new JSONObject();
        try {
            // 签名验证
            String reqPayload = "POST" + " " + request.getApiMethod() +
                    "@" + request.getAppkey() + "#" +
                    request.getGameType() + "." + request.getReqTime() +
                    "\n\n" + request.getBizParams();
            boolean isVerified = XQRSA.verify(reqPayload.getBytes(),
                    XQ_Config.XQ_SECRET.get(request.getAppkey()),
                    request.getSignature());
            if (!isVerified) {
                throw new RuntimeException("验签失败");
            }

            // 逻辑分发处理
            String apiMethod = request.getApiMethod();
            if (apiMethod.equals(XQApiMethod.PROP_QUERY.getValue())) {
                propQuery(request.getBizParams(), bizResp);
            } else if (apiMethod.equals(XQApiMethod.ROLE_QUERY_MALL.getValue()) ||
                    apiMethod.equals(XQApiMethod.ROLE_QUERY_COMMON.getValue())) {
                roleQueryV2(request.getBizParams(), bizResp);
            } else if (apiMethod.equals(XQApiMethod.PROP_ISSUE.getValue())) {
                propIssue(JSON.parseObject(request.getBizParams()), bizResp);
            } else {
                throw new RuntimeException("Not found the api method: " + request.getApiMethod());
            }
        } catch (Exception e) {
            LOG.error("小7 dealAars params:{} error: {}, cause {}",
                    JSON.toJSONString(request,SerializerFeature.WriteMapNullValue),
                    e.getMessage(), e.getCause());
            bizResp.put("respCode", "Failed");
            bizResp.put("respMsg", e.getMessage());
        } finally {
            // 设置处理结果
            response.setBizResp(JSON.toJSONString(bizResp,SerializerFeature.WriteMapNullValue));
            // 设置时间戳
            String formattedDate = DateUtil.format(new Date(), MyDateUtil.ISO8601_DATE_FORMAT);
            response.setRespTime(formattedDate);
            // 设置签名
            String respPayload = "POST" + " " + response.getApiMethod() +
                    "@" + response.getAppkey() + "#" +
                    response.getGameType() + "." + formattedDate +
                    "\n\n" + response.getBizResp();
            String sign = XQRSA.sign(respPayload.getBytes(), XQ_Config.GAME_PRIVATE_KEY);
            response.setSignature(sign);
        }
        return response;
    }

    @Override
    public JSONObject xqShop(XQShop shop) {
        // 组装request对象
        XQRequest request = new XQRequest().fromXQShop(shop);
        shop.clearExtraAttribute();
        String formattedDate = DateUtil.format(new Date(), MyDateUtil.ISO8601_DATE_FORMAT);
        JSONObject bizParams = new JSONObject();
        bizParams.put("role", shop);
        request.otherAttribute(formattedDate,
                "x7mall.mallEntry", bizParams.toJSONString());

        String reqPayload = "POST" + " " + request.getApiMethod() +
                "@" + request.getAppkey() + "#" +
                request.getGameType() + "." + request.getReqTime() +
                "\n\n" + request.getBizParams();
        String sign = XQRSA.sign(reqPayload.getBytes(), XQ_Config.GAME_PRIVATE_KEY);
        request.setSignature(sign);
        JSONObject result = new JSONObject();
        mailEntry(request,result);
        return result;
    }

    /**
     * 道具查询
     * @param result 结果
     */
    private void propQuery(String params, JSONObject result) {
        SlaveNodes slaveNodes = CommonUtil.randomSlave(slaveNodesService.getSlaveNodesListNoPage());
        if (slaveNodes == null) {
            result.put("respCode", XQResponseType.ERROR_NO_SLAVE.getCode());
            result.put("respMsg", XQResponseType.ERROR_NO_SLAVE.getMsg());
            return;
        }
        try {
            String url = CommonUtil.slaveUrl(slaveNodes, "/gm/propQuery");
            // 请求Slave返回数据
            String resultString = HttpUtil.post(url, params, 10000);
            JSONArray itemArray = JSON.parseArray(resultString);
            for (Object s : itemArray) {
                JSONObject itemObject = (JSONObject) s;
                String propIcon = itemObject.getString("propIcon");
                itemObject.put("propIcon", OSS_PATH + propIcon + IMAGE_SUFFIX);
            }
            result.put("props", itemArray);
            result.put("respCode", XQResponseType.PROP_QUERY_SUCCESS.getCode());
            result.put("respMsg", XQResponseType.PROP_QUERY_SUCCESS.getMsg());
            // 处理图片的URL
        } catch (Exception e) {
            LOG.error("小7 propQuery params:{} error: {}, cause {}",
                    JSON.toJSONString(params,SerializerFeature.WriteMapNullValue),
                    e.getMessage(), e.getCause());
            result.put("respCode", XQResponseType.ERROR_SLAVE_HTTP.getCode());
            result.put("respMsg", XQResponseType.ERROR_SLAVE_HTTP.getMsg());
            result.put("props", new JSONArray());
        }
    }

    /**
     * 角色查询V2
     * @param result 结果
     */
    private void roleQueryV2(String params, JSONObject result) {
        ServerNodes loginServer = serverNodesService.findAnyServer(1);
        if (loginServer == null) {
            result.put("respCode", XQResponseType.ERROR_NO_LOGIN.getCode());
            result.put("respMsg", XQResponseType.ERROR_NO_LOGIN.getMsg());
            result.put("role", new JSONObject());
            result.put("guidRoles", new JSONArray());
            return;
        }
        try {
            String loginApi = loginServer.toUrl("/api/x7GameRoleCallbackV2");
            String resultString = HttpUtil.post(loginApi, params, 10000);
            result.putAll(JSON.parseObject(resultString));
        } catch (Exception e) {
            LOG.error("小7 roleQueryV2 params:{} error: {}, cause {}",
                    JSON.toJSONString(params,SerializerFeature.WriteMapNullValue),
                    e.getMessage(), e.getCause());
            result.put("respCode", XQResponseType.EXCEPTION_ROLE_QUERY.getCode());
            result.put("respMsg", XQResponseType.EXCEPTION_ROLE_QUERY.getMsg());
            result.put("role", new JSONObject());
            result.put("guidRoles", new JSONArray());
        }
    }

    /**
     * 道具发放
     * @param result 结果
     */
    private void propIssue(JSONObject params, JSONObject result) {
        String issueOrderId = params.getString("issueOrderId");
        boolean exists = xqMailService.hasSameIssueOrderId(issueOrderId, 0);
        if (exists) {
            result.put("respCode", XQResponseType.ORDER_ID_EXISTS.getCode());
            result.put("respMsg", XQResponseType.ORDER_ID_EXISTS.getMsg());
            return;
        }
        try {
            XQMail xqMail = params.toJavaObject(XQMail.class);
            xqMailService.addXQMail(xqMail);
            String syncResult = xqMailService.syncXQMail(xqMail.getId());
            String resultString = JSON.parseObject(syncResult).getString("ret");
            if ("0".equals(resultString)) {
                result.put("respCode", XQResponseType.PROP_ISSUE_SUCCESS.getCode());
                result.put("respMsg", XQResponseType.PROP_ISSUE_SUCCESS.getMsg());
            } else {
                result.put("respCode", XQResponseType.PROP_ISSUE_FAILED.getCode());
                result.put("respMsg", XQResponseType.PROP_ISSUE_FAILED.getMsg());
            }
        } catch (Exception e) {
            LOG.error("小7 propIssue params:{} error: {}, cause {}",
                    JSON.toJSONString(params,SerializerFeature.WriteMapNullValue),
                    e.getMessage(), e.getCause());
            result.put("respCode", XQResponseType.EXCEPTION_PROP_ISSUE.getCode());
            result.put("respMsg", XQResponseType.EXCEPTION_PROP_ISSUE.getMsg() + e.getMessage());
        }
    }

    /**
     * 商城入口
     * @param result 结果
     */
    private void mailEntry(XQRequest request, JSONObject result) {
        try {
            String sqMailUrl = propertiesService.getXqMallUrl();
            if (sqMailUrl.isEmpty()) {
                throw new ConfigurationException("配置错误");
            }
            String data = URLUtil.encode(request.toParams()).replaceAll("\\+", "%2B");
            LOG.info("商城入口 请求小7: {}, 传入参数 :{}",sqMailUrl,data);
            String responseString = HttpUtil.post(sqMailUrl, data, 10000);
            LOG.info("商城入口 小7响应: {}", responseString);
            XQResponse response = JSON.parseObject(responseString, XQResponse.class);
            String respPayload = "POST" + " " + response.getApiMethod() +
                    "@" + response.getAppkey() + "#" +
                    response.getGameType() + "." + response.getRespTime() +
                    "\n\n" + response.getBizResp();
            boolean verify = XQRSA.verify(respPayload.getBytes(), XQ_Config.XQ_SECRET.get(request.getAppkey()), response.getSignature());
            if (verify) {
                String bizRespString = response.getBizResp();
                JSONObject bizResp = JSON.parseObject(bizRespString);
                if (bizResp.getString("respCode").equals("SUCCESS")) {
                    result.putAll(bizResp);
                    return;
                }
            } else {
                result.put("respCode",XQResponseType.ERROR_ACCESS_XQ_MALL.getCode());
                result.put("respMsg",XQResponseType.ERROR_ACCESS_XQ_MALL.getCode());
                result.put("isOpen", false);
                result.put("showNotification", false);
            }
            result.putAll(JSON.parseObject(responseString));
        } catch (Exception e) {
            LOG.error("小7 mailEntry params:{} error: {}, cause {}",
                    JSON.toJSONString(request,SerializerFeature.WriteMapNullValue),
                    e.getMessage(), e.getCause());
            result.put("respCode", XQResponseType.EXCEPTION_ACCESS_XQ_MALL.getCode());
            result.put("respMsg", XQResponseType.EXCEPTION_ACCESS_XQ_MALL.getMsg() + e.getMessage());
            result.put("isOpen", false);
            result.put("showNotification", false);
        }
    }

}
