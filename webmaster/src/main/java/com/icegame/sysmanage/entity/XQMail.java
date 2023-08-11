package com.icegame.sysmanage.entity;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icegame.framework.utils.MyDateUtil;

public class XQMail {
    public static final String MAIL_TYPE = "小7邮件";
    private Long id;

    /**
     * 发放订单号，对同一订单号发放道具需要进行幂等性校验，具有唯一性
     */
    private String issueOrderId;

    /**
     * 发放角色ID
     * 格式：区服id@角色id@小7账号id
     */
    private String roleId;

    /**
     * 	角色所属小号ID
     */
    private String guid;

    /**
     * 角色所属区服ID
     */
    private String serverId;

    /**
     * 角色所属区服名称
     */
    private String serverName;

    /**
     * 发放时间，格式为ISO8601，示例：2022-06-27T09:11:13+0800
     */
    private String issueTime;

    /**
     * 邮件标题
     */
    private String mailTitle;

    /**
     * 邮件内容
     */
    private String mailContent;

    /**
     * 是否为测试发放
     */
    private Boolean test;

    /**
     * 发放的道具信息
     * <code>
     * {
     *     "propsCode": "xxx",
     *     "propName": "xxx",
     *     "propQuantity": "xxx",
     * }
     * </code>
     */
    private String issuedProps;

    private String status = "2";

    public XQMail() {

    }

    public XQMail(Long id, String status){
        this.id = id;
        this.status = status;
    }

    public XQMail(String issueOrderId, String roleId, String guid, String serverId, String serverName){
        this.issueOrderId = issueOrderId;
        this.roleId = roleId;
        this.guid = guid;
        this.serverId = serverId;
        this.serverName = serverName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIssueOrderId() {
        return issueOrderId;
    }

    public void setIssueOrderId(String issueOrderId) {
        this.issueOrderId = issueOrderId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public String formatCreateTime() {
        DateTime parse = DateUtil.parse(issueTime, MyDateUtil.ISO8601_DATE_FORMAT);
        return String.valueOf(parse.toTimestamp().getTime());
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getMailTitle() {
        return mailTitle;
    }

    public void setMailTitle(String mailTitle) {
        this.mailTitle = mailTitle;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public String getIssuedProps() {
        return issuedProps;
    }

    public String formatAwardStr() {
        JSONArray jsonArray = JSONObject.parseArray(issuedProps);
        StringBuilder sb = new StringBuilder();
        for (Object award : jsonArray) {
            JSONObject awardObj = JSON.parseObject(award.toString());
            sb.append("7:").append(awardObj.getString("propCode")).append(":").
                    append(awardObj.getString("propQuantity")).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void setIssuedProps(String issuedProps) {
        this.issuedProps = issuedProps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
