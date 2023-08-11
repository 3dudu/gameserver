package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.datasources.DataSwitch;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.mapper.AllSrvMailMapper;
import com.icegame.sysmanage.mapper.AutoOpenServerMapper;
import com.icegame.sysmanage.service.IAllSrvMailService;
import com.icegame.sysmanage.service.IAutoOpenServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AutoOpenServerService implements IAutoOpenServerService {

	@Autowired
	private AutoOpenServerMapper autoOpenServerMapper;

	// ========================
	//  定时任务相关逻辑
	// ========================

	@Override
	@DataSwitch(dataSource="dataSourceLoginPay")
	public int getPlayerCount(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.getPlayerCount(autoOpenServer).getPlayer();
	}


	@Override
	@DataSwitch(dataSource="dataSource")
	public Long getMaxId(AutoOpenServer autoOpenServer) {
		AutoOpenServer autoOpenServer1 = autoOpenServerMapper.getMaxId(autoOpenServer);
		if(autoOpenServer1 == null){
			return null;
		}
		return autoOpenServer1.getMaxId();
	}


	@Override
	@DataSwitch(dataSource="dataSource")
	public List<AutoOpenServer> getAutoOpenServerList() {
		return autoOpenServerMapper.getAutoOpenServerList(new AutoOpenServer());
	}

	// ========================
	//  下面为自动更新任务的页面
	// ========================

	@Override
	@DataSwitch(dataSource="dataSource")
	public PageInfo<AutoOpenServer> getAutoOpenServerList(AutoOpenServer autoOpenServer, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<AutoOpenServer> helpSysList = autoOpenServerMapper.getAutoOpenServerList(autoOpenServer);
		PageInfo<AutoOpenServer> pageInfo = new PageInfo<AutoOpenServer>(helpSysList);
		return pageInfo;
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public AutoOpenServer getAutoOpenServerById(Long id) {
		return autoOpenServerMapper.getAutoOpenServerById(id);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public boolean addAutoOpenServer(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.addAutoOpenServer(autoOpenServer);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public boolean delAutoOpenServer(Long id) {
		return autoOpenServerMapper.delAutoOpenServer(id);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public boolean updateAutoOpenServer(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.updateAutoOpenServer(autoOpenServer);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public boolean changeStatus(Long id) {
		return autoOpenServerMapper.changeStatus(id);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public List<AutoOpenServer> checkExistAutoOpenServer(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.checkExistAutoOpenServer(autoOpenServer);
	}

    @Override
    @DataSwitch(dataSource="dataSource")
    public boolean existsKey(String key) {
	    List<AutoOpenServer> list = autoOpenServerMapper.existsKey(key);
        return list.size() > 0 ? true : false;
    }

	@Override
	@DataSwitch(dataSource="dataSource")
	public AutoOpenServer getAutoOpenServerTipsStatus(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.getAutoOpenServerTipsStatus(autoOpenServer);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public AutoOpenServer getAutoOpenServerTipsSlimit(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.getAutoOpenServerTipsSlimit(autoOpenServer);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public boolean isTipsStatusOpen(AutoOpenServer autoOpenServer) {
		AutoOpenServer autoOpenServer1 = autoOpenServerMapper.getAutoOpenServerTipsStatus(autoOpenServer);
		return autoOpenServer1.getValue().equals("1") ? true : false;
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public boolean changeAutoOpenServerTipsStatus(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.changeAutoOpenServerTipsStatus(autoOpenServer);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public boolean changeAutoOpenServerTipsSlimit(AutoOpenServer autoOpenServer) {
		return autoOpenServerMapper.changeAutoOpenServerTipsSlimit(autoOpenServer);
	}

	@Override
	@DataSwitch(dataSource="dataSource")
	public List<AutoOpenServer> existsEnableAutoOpenServer(String channel) {
		return autoOpenServerMapper.existsEnableAutoOpenServer(channel);
	}
}
