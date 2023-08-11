package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.JVipqqWxConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JVipqqWxConfigMapper {

	/**
	 * 查询配置
	 * @param jVipqqWxConfig
	 * @return
	 */
	public List<JVipqqWxConfig> getListPage(JVipqqWxConfig jVipqqWxConfig);

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
	public List<JVipqqWxConfig> getConfigByChannel(JVipqqWxConfig jVipqqWxConfig);



}
