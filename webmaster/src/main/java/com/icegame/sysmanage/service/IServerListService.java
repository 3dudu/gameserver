package com.icegame.sysmanage.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.ServerList;
import net.sf.jsqlparser.schema.Server;

public interface IServerListService {

	public List<ServerList> download(ServerList serverList);

	public List<ServerList> getAllChannelServerList();

	public List<ServerList> checkExistServerList(ServerList serverList);

	public PageInfo<ServerList> getServerList(ServerList serverList,PageParam pageParam);

	public List<ServerList> getServerIdsList();

	public PageInfo<ServerList> getServerListOpen(ServerList serverList,PageParam pageParam);

	public List<ServerList> getAllChannelList();

	public ServerList getServerById(Long id);

	public List<ServerList> getServerBySlaveId(Long slaveId);

	public List<ServerList> getServerByChannelAndCreateTime(ServerList serverList);

	public List<ServerList> getServerByIP(String ip);

	public List<ServerList> getServerByChannel(String channel);

	public boolean addServer(ServerList serverList);

	public Long addServerReturnId(ServerList serverList);

	public boolean delServer(Long id);

	public boolean updateServer(ServerList serverList);

	public boolean updateServerClose(ServerList serverList);

	public boolean updateServerCloseBySlaveId(ServerList serverList);

	public void saveSync(ServerList serverList);

	public void deleteSync(ServerList serverList);

	public boolean updateSlaveIp(ServerList serverList);

	public boolean existsChannel(String channel);

	public boolean existsMainTainServer();

	public boolean syncSuccess(ServerList serverList);

	public boolean syncFailed(ServerList serverList);

	public boolean suggest(ServerList serverList);

	public Map<String,Object> syncServer(ServerList serverList);

	public Map<String,Object> syncDelete(Long serverId);

	/**
	 * 根据多个渠道获取区服列表
	 * @param multChannel
	 * @return
	 */
	public List<ServerList> getServerListByMultChannel(String multChannel, String format);

	/**
	 * 根据多个salveid 获取区服列表
	 * @param multSlaveId
	 * @return
	 */
	public List<ServerList> getServerListByMultSlaveId(String multSlaveId);


	public List<ServerList> getAllChannelServerListNoPage(ServerList serverList);

    /**
     * 查找某个channel下面有哪些推荐服
     * @param channel
     * @return
     */
	public List<ServerList> getSuggestServerInChannel(String channel);


}

