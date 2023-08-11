package com.icegame.log.mapper;

import com.icegame.log.entity.SysLog;

import java.util.List;

public interface SysLogMapper {

    public List<SysLog> getLogList(SysLog sysLog);

    public void save(SysLog sysLog);
}
