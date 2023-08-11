package com.icegame.gm.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JPreinstallMessage;

public interface IJPreinstallMessageService {
	
	public PageInfo<JPreinstallMessage> getJPreinstallMessageList(JPreinstallMessage jpm,PageParam pageParam);
	
	public List<JPreinstallMessage> getJPreinstallMessageList(JPreinstallMessage jpm);
	
	public JPreinstallMessage getJPreinstallMessageById(Long id);
	
	public boolean addJPreinstallMessage(JPreinstallMessage jpm);
	
	public boolean updateJPreinstallMessage(JPreinstallMessage jpm);
	
	public boolean delJPreinstallMessage(Long id);
	
}

