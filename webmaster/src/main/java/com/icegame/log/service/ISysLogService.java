package com.icegame.log.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.log.entity.SysLog;

public interface ISysLogService {

    public PageInfo<SysLog> getLogList(SysLog sysLog, PageParam pageParam);

    public void save(SysLog sysLog);
}
