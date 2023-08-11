package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.JVipqqWxConfig;

import java.util.List;

public interface IJVipqqWxConfigService {

	/**
	 * 查询配置
	 * @param jVipqqWxConfig
	 * @return
	 */
	public PageInfo<JVipqqWxConfig> getListPage(JVipqqWxConfig jVipqqWxConfig, PageParam pageParam);

	/**
	 * 查询配置
	 * @param id
	 * @return
	 */
	public JVipqqWxConfig getConfigById(Long id);


	/**
	 * 添加配置
	 * @param jVipqqWxConfig
	 * @return
	 */
	public boolean addConfig(JVipqqWxConfig jVipqqWxConfig);

	/**
	 * 删除配置
	 * @param id
	 * @return
	 */
	public boolean delConfig(Long id);

	/**
	 * 修改配置
	 * @param jVipqqWxConfig
	 * @return
	 */
	public boolean updateConfig(JVipqqWxConfig jVipqqWxConfig);

	/**
	 * 修改配置
	 * @param jVipqqWxConfig
	 * @return
	 */
	public boolean getConfigByChannel(JVipqqWxConfig jVipqqWxConfig);


	public JVipqqWxConfig getConfigByChannelRetList(JVipqqWxConfig jVipqqWxConfig);

}

