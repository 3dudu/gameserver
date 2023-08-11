package com.icegame.gm.service.impl;

import java.util.List;

import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.service.impl.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JPreinstallMessage;
import com.icegame.gm.mapper.JPreinstallMessageMapper;
import com.icegame.gm.service.IJPreinstallMessageService;


@Service
public class JPreinstallMessageService implements IJPreinstallMessageService {
	
	@Autowired
	private JPreinstallMessageMapper jpmMapper;

	@Autowired
	private LogService logService;

	Log log = new Log();

	@Override
	public PageInfo<JPreinstallMessage> getJPreinstallMessageList(JPreinstallMessage jpm, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JPreinstallMessage> jpmList = jpmMapper.getJPreinstallMessageList(jpm);
		PageInfo<JPreinstallMessage> pageInfo = new PageInfo<JPreinstallMessage>(jpmList);
		return pageInfo;
	}

	@Override
	public JPreinstallMessage getJPreinstallMessageById(Long id) {
		return jpmMapper.getJPreinstallMessageById(id);
	}

	@Override
	public boolean addJPreinstallMessage(JPreinstallMessage jpm) {
		log = UserUtils.recording("添加预设消息，内容：["+jpm.getcontent()+"]", Type.ADD.getName());
		logService.addLog(log);
		return jpmMapper.addJPreinstallMessage(jpm);
	}

	@Override
	public boolean updateJPreinstallMessage(JPreinstallMessage jpm) {
		log = UserUtils.recording("修改预设消息，ID：["+jpm.getId()+"]", Type.UPDATE.getName());
		logService.addLog(log);
		return jpmMapper.updateJPreinstallMessage(jpm);
	}

	@Override
	public boolean delJPreinstallMessage(Long id) {
		log = UserUtils.recording("删除预设消息，ID：["+id+"]", Type.DELETE.getName());
		logService.addLog(log);
		return jpmMapper.delJPreinstallMessage(id);
	}

	@Override
	public List<JPreinstallMessage> getJPreinstallMessageList(JPreinstallMessage jpm) {
		return jpmMapper.getJPreinstallMessageList(jpm);
	}

}
