package com.icegame.sysmanage.entity;

/**
 * @author chesterccw
 * @date 2020/7/29
 */
public class ExcelMail {

    private Long id;
    private Long serverId;
    private Long playerId;
    private String content;
    private String createTime;
    private String syncTime;
    private String startDate;
    private String endDate;
    private String awardStr;
    private String subject;
    /**
     * 标识邮件状态{0:发送成功,1:发送失败,等待重新发送,2:新加邮件,还没有发送,3:重新发送成功}
     */
    private String status;
    private String userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAwardStr() {
        return awardStr;
    }

    public void setAwardStr(String awardStr) {
        this.awardStr = awardStr;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public enum syncStatus{
        SUCCESS("0"),
        FAIL("1"),
        NEW("2"),
        RE_SUCCESS("3")
        ;

        private final String value;

        syncStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

}
