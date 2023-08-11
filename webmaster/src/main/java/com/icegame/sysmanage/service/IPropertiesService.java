package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.AllSrvMail;

public interface IPropertiesService {

	public String getProjectName();

	public String getBaseDir();

	public String getJsonPath();

	public String getJsonSuffix();

	public String getVersion();


	// 帮助系统-三国挂机2

	public String getCurrentProject();

	public String getCurrentLanguage();

	public String getPayAgentRechargePort();

	String getXqMallUrl();
}

