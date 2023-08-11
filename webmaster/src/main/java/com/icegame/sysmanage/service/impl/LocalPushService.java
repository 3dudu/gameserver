package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.LocalPush;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.Notice;
import com.icegame.sysmanage.mapper.LocalPushMapper;
import com.icegame.sysmanage.service.ILocalPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chesterccw
 * @date 2020/5/11
 */
@Service
public class LocalPushService implements ILocalPushService {

    @Autowired
    private LocalPushMapper localPushMapper;

    @Autowired
    private LogService logService;

    Log log = new Log();

    @Override
    public PageInfo<LocalPush> getLocalPushList(LocalPush localPush, PageParam pageParam) {
        PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
        if(localPush.getChannel().equals("选择渠道"))localPush.setChannel("");
        List<LocalPush> localPushList = localPushMapper.getLocalPushList(localPush);
        PageInfo<LocalPush> pageInfo = new PageInfo<LocalPush>(localPushList);
        return pageInfo;
    }

    @Override
    public LocalPush getLocalPushById(Long id) {
        return localPushMapper.getLocalPushById(id);
    }

    @Override
    public List<LocalPush> getLocalPushByChannel(String channel) {
        return localPushMapper.getLocalPushByChannel(channel);
    }

    @Override
    public List<LocalPush> getChannelList() {
        return localPushMapper.getChannelList();
    }

    @Override
    public List<LocalPush> checkExistChannel(LocalPush localPush) {
        return localPushMapper.checkExistChannel(localPush);
    }

    @Override
    public boolean addLocalPush(LocalPush localPush) {
        log = UserUtils.recording("添加本地推送，channel ：" + localPush.getChannel() + "，channelCode = " +
                localPush.getChannelCode() + "，内容："+localPush.getContext()+"", Type.ADD.getName());
        logService.addLog(log);
        return localPushMapper.addLocalPush(localPush);
    }

    @Override
    public boolean delLocalPush(Long id) {
        log = UserUtils.recording("删除本地推送：id = " + id, Type.DELETE.getName());
        logService.addLog(log);
        return localPushMapper.delLocalPush(id);
    }

    @Override
    public boolean updateLocalPush(LocalPush localPush) {
        log = UserUtils.recording("修改本地推送", Type.UPDATE.getName());
        logService.addLog(log);
        return localPushMapper.updateLocalPush(localPush);
    }
}
