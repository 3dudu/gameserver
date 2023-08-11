package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.LocalPush;

import java.util.List;

/**
 * @author chesterccw
 * @date 2020/5/11
 */
public interface ILocalPushService {

    public PageInfo<LocalPush> getLocalPushList(LocalPush localPush, PageParam pageParam);

    public LocalPush getLocalPushById(Long id);

    public List<LocalPush> getLocalPushByChannel(String channel);

    public List<LocalPush> getChannelList();

    public List<LocalPush> checkExistChannel(LocalPush localPush);

    public boolean addLocalPush(LocalPush localPush);

    public boolean delLocalPush(Long id);

    public boolean updateLocalPush(LocalPush localPush);
}
