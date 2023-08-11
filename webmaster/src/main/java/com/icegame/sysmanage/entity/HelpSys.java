package com.icegame.sysmanage.entity;

/**
 * 帮助系统
 * @author wuzhijian
 * @date 2019-03-15 11:16:07
 */
public class HelpSys {

    private Long id;

    private String userName;

    private String createTime;

    private String title;

    private String description;

    private String context;

    // 1：官方  2：攻略
    private int diffType;

    private String diffName;

    //1：热门攻略  2：新手推荐  3：高玩攻略...
    private int signType;

    private String signName;

    private String startTime;

    private String endTime;



    // 点赞数
    private int zan;

    public HelpSys(){

    }

    public HelpSys(String userName, String createTime) {
        this.userName = userName;
        this.createTime = createTime;
    }

    public HelpSys(String userName, String createTime, String title, String context, int diffType) {
        this.userName = userName;
        this.createTime = createTime;
        this.title = title;
        this.context = context;
        this.diffType = diffType;
    }

    public HelpSys(String startTime, String endTime, int diffType, int signType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.diffType = diffType;
        this.signType = signType;
    }

    public HelpSys(String startTime, String endTime, int signType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.signType = signType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getDiffType() {
        return diffType;
    }

    public void setDiffType(int diffType) {
        this.diffType = diffType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDiffName() {
        return diffName;
    }

    public void setDiffName(String diffName) {
        this.diffName = diffName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    @Override
    public String toString() {
        return "HelpSys{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", title='" + title + '\'' +
                ", context='" + context + '\'' +
                ", diffType=" + diffType +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", diffName='" + diffName + '\'' +
                '}';
    }
}
