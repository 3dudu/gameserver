package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JReduceLog;

public interface IJReduceLogService {
	
	public PageInfo<JReduceLog> getJReduceLogList(JReduceLog jrl,PageParam pageParam);
	
	public JReduceLog getJReduceLogById(Long id);
	
	public boolean addJReduceLog(JReduceLog jrl);
}

