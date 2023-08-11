package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.LocalPush;

import java.util.List;

/**
 * @author wuzhijian
 */
public interface LocalPushMapper {

	public List<LocalPush> getLocalPushList(LocalPush localPush);

	public LocalPush getLocalPushById(Long id);

	public List<LocalPush> getLocalPushByChannel(String channel);

	public List<LocalPush> getChannelList();

	public List<LocalPush> checkExistChannel(LocalPush localPush);

	public boolean addLocalPush(LocalPush localPush);

	public boolean delLocalPush(Long id);

	public boolean updateLocalPush(LocalPush localPush);

}
