package com.icegame.gm.service.impl;

import java.util.List;

import com.icegame.framework.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JReduceLog;
import com.icegame.gm.mapper.JReduceLogMapper;
import com.icegame.gm.service.IJReduceLogService;

@Service
public class JReduceLogService implements IJReduceLogService {
	
	@Autowired
	private JReduceLogMapper jReduceLogMapper;
	
	public PageInfo<JReduceLog> getJReduceLogList(JReduceLog jrl, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JReduceLog> jmessageList = jReduceLogMapper.getJReduceLogList(jrl);
		if(jmessageList.size() > 0){
			for(JReduceLog jReduceLog : jmessageList){
				jReduceLog.setStrCreateTime(TimeUtils.stampToDateWithMill(jReduceLog.getCreateTime()));
			}
		}
		PageInfo<JReduceLog> pageInfo = new PageInfo<JReduceLog>(jmessageList);
		return pageInfo;
	}

	@Override
	public JReduceLog getJReduceLogById(Long id) {
		return jReduceLogMapper.getJReduceLogById(id);
	}

	@Override
	public boolean addJReduceLog(JReduceLog jrl) {
		return jReduceLogMapper.addJReduceLog(jrl);
	}
	
	
}
