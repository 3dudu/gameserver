package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.AllSrvMail;
import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.HelpSys;
import com.icegame.sysmanage.entity.ServerList;

import java.util.List;

public interface IAutoOpenServerService {

	// ========================
	//  次数是定时器用的的逻辑
	// ========================

	public int getPlayerCount(AutoOpenServer autoOpenServer);


	public Long getMaxId(AutoOpenServer autoOpenServer);


	public List<AutoOpenServer> getAutoOpenServerList();


	// ========================
	//  下面为自动更新任务的页面
	// ========================

	public PageInfo<AutoOpenServer> getAutoOpenServerList(AutoOpenServer autoOpenServer, PageParam pageParam);

	public AutoOpenServer getAutoOpenServerById(Long id);

	public boolean addAutoOpenServer(AutoOpenServer autoOpenServer);

	public boolean delAutoOpenServer(Long id);

	public boolean updateAutoOpenServer(AutoOpenServer autoOpenServer);

	public boolean changeStatus(Long id);

	public List<AutoOpenServer> checkExistAutoOpenServer(AutoOpenServer autoOpenServer);

	public boolean existsKey(String key);

	public AutoOpenServer getAutoOpenServerTipsStatus(AutoOpenServer autoOpenServer);

	public AutoOpenServer getAutoOpenServerTipsSlimit(AutoOpenServer autoOpenServer);

	public boolean isTipsStatusOpen(AutoOpenServer autoOpenServer);

	public boolean changeAutoOpenServerTipsStatus(AutoOpenServer autoOpenServer);

	public boolean changeAutoOpenServerTipsSlimit(AutoOpenServer autoOpenServer);

	public List<AutoOpenServer> existsEnableAutoOpenServer(String channel);
}

