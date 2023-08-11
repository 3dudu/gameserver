package com.icegame.gm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.gm.entity.JAutoreplyMessage;
import com.icegame.gm.mapper.JAutoreplyMessageMapper;
import com.icegame.gm.service.IJAutoreplyMessageService;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.service.impl.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JAutoreplyMessageService implements IJAutoreplyMessageService{
	
	@Autowired
	private JAutoreplyMessageMapper jamMapper;

	@Autowired
	private LogService logService;

	Log log = new Log();

	@Override
	public PageInfo<JAutoreplyMessage> getJAutoreplyMessageList(JAutoreplyMessage jam, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JAutoreplyMessage> jamList = jamMapper.getJAutoreplyMessageList(jam);
		PageInfo<JAutoreplyMessage> pageInfo = new PageInfo<JAutoreplyMessage>(jamList);
		return pageInfo;
	}

	@Override
	public List<JAutoreplyMessage> getJAutoreplyMessageList(JAutoreplyMessage jam) {
		return jamMapper.getJAutoreplyMessageList(jam);
	}

	@Override
	public JAutoreplyMessage getJAutoreplyMessageById(Long id) {
		return jamMapper.getJAutoreplyMessageById(id);
	}

	@Override
	public boolean addJAutoreplyMessage(JAutoreplyMessage jam) {
		log = UserUtils.recording("添加自动回复，内容：["+jam.getcontent()+"]", Type.ADD.getName());
		logService.addLog(log);
		return jamMapper.addJAutoreplyMessage(jam);
	}

	@Override
	public boolean updateJAutoreplyMessage(JAutoreplyMessage jam) {
		log = UserUtils.recording("修改自动回复，ID：["+jam.getId()+"]", Type.UPDATE.getName());
		logService.addLog(log);
		return jamMapper.updateJAutoreplyMessage(jam);
	}

	@Override
	public boolean delJAutoreplyMessage(Long id) {
		log = UserUtils.recording("删除自动回复，ID：["+id+"]", Type.DELETE.getName());
		logService.addLog(log);
		return jamMapper.delJAutoreplyMessage(id);
	}
}
