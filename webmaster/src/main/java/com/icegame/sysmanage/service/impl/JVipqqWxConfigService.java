package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.JVipqqWxConfig;
import com.icegame.sysmanage.mapper.JVipqqWxConfigMapper;
import com.icegame.sysmanage.service.IJVipqqWxConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JVipqqWxConfigService implements IJVipqqWxConfigService {

	@Autowired
	private JVipqqWxConfigMapper jVipqqWxConfigMapper;

	@Override
	public PageInfo<JVipqqWxConfig> getListPage(JVipqqWxConfig jVipqqWxConfig, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JVipqqWxConfig> configList = jVipqqWxConfigMapper.getListPage(jVipqqWxConfig);
		PageInfo<JVipqqWxConfig> pageInfo = new PageInfo<JVipqqWxConfig>(configList);
		return pageInfo;
	}

	@Override
	public JVipqqWxConfig getConfigById(Long id) {
		return jVipqqWxConfigMapper.getConfigById(id);
	}

	@Override
	public boolean addConfig(JVipqqWxConfig jVipqqWxConfig) {
		return jVipqqWxConfigMapper.addConfig(jVipqqWxConfig);
	}

	@Override
	public boolean delConfig(Long id) {
		return jVipqqWxConfigMapper.delConfig(id);
	}

	@Override
	public boolean updateConfig(JVipqqWxConfig jVipqqWxConfig) {
		return jVipqqWxConfigMapper.updateConfig(jVipqqWxConfig);
	}

	@Override
	public boolean getConfigByChannel(JVipqqWxConfig jVipqqWxConfig) {
		List<JVipqqWxConfig> configList = jVipqqWxConfigMapper.getConfigByChannel(jVipqqWxConfig);
		return configList.size() > 0 ? true : false ;
	}

	@Override
	public JVipqqWxConfig getConfigByChannelRetList(JVipqqWxConfig jVipqqWxConfig) {
		List<JVipqqWxConfig> config = jVipqqWxConfigMapper.getConfigByChannel(jVipqqWxConfig);

		// 如果没有配置 返回 一个 都默认关闭的 对象，防止报错
		if(config.size() == 0){
			return new JVipqqWxConfig(0,1000,0,"微信公告号","common");
		}else{
			return config.get(0);
		}
	}


}
