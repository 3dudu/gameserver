package com.icegame.gm.service.impl;

import java.util.List;

import com.icegame.framework.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JWorldMessage;
import com.icegame.gm.mapper.JWorldMessageMapper;
import com.icegame.gm.service.IJWorldMessageService;

import javax.management.timer.Timer;

@Service
public class JWorldMessageService implements IJWorldMessageService {
	
	@Autowired
	private JWorldMessageMapper jMessageMapper;

	/**
	 *
	 * @param jmessage
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 12:21:48
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	public PageInfo<JWorldMessage> getJMessageList(JWorldMessage jmessage, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JWorldMessage> jmessageList = jMessageMapper.getJMessageList(jmessage);
		if(jmessageList.size() > 0){
			for(JWorldMessage jWorldMessage : jmessageList){
				jWorldMessage.setStrCreateTime(TimeUtils.stampToDateWithMill(jWorldMessage.getCreateTime()));
			}
		}
		PageInfo<JWorldMessage> pageInfo = new PageInfo<JWorldMessage>(jmessageList);
		return pageInfo;
	}

	@Override
	public JWorldMessage getJMessage(Long id) {
		return jMessageMapper.getJMessageById(id);
	}

	@Override
	public boolean addJMessage(JWorldMessage jmessage) {
		return jMessageMapper.addJMessage(jmessage);
	}

	
}
