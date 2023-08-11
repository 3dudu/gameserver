package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.ServerList;
import org.apache.ibatis.annotations.Param;

public interface ServerListMapper {

	public List<ServerList> download(ServerList serverList);

	public List<ServerList> getAllChannelServerList();

	public List<ServerList> getAllChannelServerListNoPage(ServerList serverList);

	public List<ServerList> checkExistServerList(ServerList serverList);

	public List<ServerList> getServerList(ServerList serverList);

	public List<ServerList> getServerListOpen(ServerList serverList);

	public List<ServerList> getServerIdsList();

	public List<ServerList> getAllChannelList();

	public List<ServerList> getServerByChannel(ServerList server);

	public ServerList getServerById(Long id);

	public List<ServerList> getServerBySlaveId(Long slaveId);

	public List<ServerList> getServerByChannelAndCreateTime(ServerList server);

	public List<ServerList> getServerByIP(ServerList serverList);

	public boolean addServer(ServerList serverList);

	public Long addServerReturnId(ServerList serverList);

	public boolean delServer(Long id);

	public boolean updateServer(ServerList serverList);

	public boolean updateServerClose(ServerList serverList);

	public boolean updateServerCloseBySlaveId(ServerList serverList);

	public boolean updateSlaveIp(ServerList serverList);

	public List<ServerList> existsChannel(@Param("channel") String channel);

	public List<ServerList> existsMainTainServer();

	public boolean syncSuccess(ServerList serverList);

	public boolean syncFailed(ServerList serverList);

	public boolean suggest(ServerList serverList);

	/**
	 * 根据多个渠道获取区服列表
	 * @param multChannel
	 * @return
	 */
	public List<ServerList> getServerListByMultChannel(@Param("multChannel") String multChannel);

	/**
	 * 根据多个salveid 获取区服列表
	 * @param multSlaveId
	 * @return
	 */
	public List<ServerList> getServerListByMultSlaveId(@Param("multSlaveId") String multSlaveId);

    /**
     * 查找某个channel下面有哪些推荐服
     * @param channel
     * @return
     */
    public List<ServerList> getSuggestServerInChannel(@Param("channel") String channel);

}
