package com.icegame.gm.mapper;

import java.util.List;

import com.icegame.gm.entity.JReduceLog;

public interface JReduceLogMapper {
		
	public List<JReduceLog> getJReduceLogList(JReduceLog jrl);
		
	public JReduceLog getJReduceLogById(Long id);
	
	public boolean addJReduceLog(JReduceLog jrl);

}
