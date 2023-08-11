package com.icegame.gm.service.impl;

import java.util.List;

import com.icegame.framework.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JMailMessage;
import com.icegame.gm.mapper.JMailMessageMapper;
import com.icegame.gm.service.IJMailCrossServerService;

@Service
public class JMailCrossServerService implements IJMailCrossServerService {
	
	@Autowired
	private JMailMessageMapper jmcsMapper;

	/**
	 *
	 * @param jmcs
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 12:22:19
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@Override
	public PageInfo<JMailMessage> getJMCSList(JMailMessage jmcs, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JMailMessage> jmcsList = jmcsMapper.getJMCSList(jmcs);
		if(jmcsList.size() > 0){
			for(JMailMessage jMailMessage : jmcsList){
				jMailMessage.setStrCreateTime(TimeUtils.stampToDateWithMill(jMailMessage.getCreateTime()));
			}
		}
		PageInfo<JMailMessage> pageInfo = new PageInfo<JMailMessage>(jmcsList);
		return pageInfo;
	}

	@Override
	public JMailMessage getJMCSById(Long id) {
		return jmcsMapper.getJMCSById(id);
	}

	@Override
	public boolean addJMCS(JMailMessage jmcs) {
		return jmcsMapper.addJMCS(jmcs);
	}


	
	
}
