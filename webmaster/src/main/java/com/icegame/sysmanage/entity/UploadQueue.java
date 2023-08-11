package com.icegame.sysmanage.entity;

import java.io.File;

public class UploadQueue {

    private Long id;

    private Long userId;

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String createTime;

    private String uploadTime;

    private String state;

    private String result;

    private String file;

    private String startTime;

    private String endTime;

    private String targetServer;

    private String targetServerName;

    private String language;

    private String openMd5;

    private String detail;

    public UploadQueue(){

    }

    public UploadQueue(Long id) {
        this.id = id;
    }

    public UploadQueue(Long id, String result, String detail) {
        this.id = id;
        this.result = result;
        this.detail = detail;
    }

    public UploadQueue(Long userId, String userName, String createTime, String uploadTime, String targetServer,String targetServerName, String language, String openMd5, String state, String file) {
        this.userId = userId;
        this.userName = userName;
        this.createTime = createTime;
        this.uploadTime = uploadTime;
        this.targetServer = targetServer;
        this.targetServerName = targetServerName;
        this.language = language;
        this.openMd5 = openMd5;
        this.state = state;
        this.file = file;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }



    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public String getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(String targetServer) {
        this.targetServer = targetServer;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getOpenMd5() {
        return openMd5;
    }

    public void setOpenMd5(String openMd5) {
        this.openMd5 = openMd5;
    }

    public String getTargetServerName() {
        return targetServerName;
    }

    public void setTargetServerName(String targetServerName) {
        this.targetServerName = targetServerName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
