package com.icegame.sysmanage.mapper;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.ServerList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AutoOpenServerMapper {

	public AutoOpenServer getPlayerCount(AutoOpenServer autoOpenServer);

	public AutoOpenServer getMaxId(AutoOpenServer autoOpenServer);

	// ========================
	//  下面为自动更新任务的页面
	// ========================

	public List<AutoOpenServer> getAutoOpenServerList(AutoOpenServer autoOpenServer);

	public AutoOpenServer getAutoOpenServerById(Long id);

	public AutoOpenServer getAutoOpenServerTipsStatus(AutoOpenServer autoOpenServer);

	public AutoOpenServer getAutoOpenServerTipsSlimit(AutoOpenServer autoOpenServer);

	public boolean addAutoOpenServer(AutoOpenServer autoOpenServer);

	public boolean delAutoOpenServer(Long id);

	public boolean updateAutoOpenServer(AutoOpenServer autoOpenServer);

	public boolean changeAutoOpenServerTipsStatus(AutoOpenServer autoOpenServer);

	public boolean changeAutoOpenServerTipsSlimit(AutoOpenServer autoOpenServer);

	public boolean changeStatus(Long id);

	public List<AutoOpenServer> checkExistAutoOpenServer(AutoOpenServer autoOpenServer);

	public List<AutoOpenServer> existsKey(@Param("key") String key);

	/**
	 * 检查是否有开启的自动开服配置
	 * @return
	 */
	public List<AutoOpenServer> existsEnableAutoOpenServer(@Param("channel") String channel);

}
