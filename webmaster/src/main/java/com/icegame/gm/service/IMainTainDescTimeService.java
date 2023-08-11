package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.MainTainDescTime;

import java.util.List;

public interface IMainTainDescTimeService {

	public PageInfo<MainTainDescTime> getMainTainDescTimeList(MainTainDescTime jpm, PageParam pageParam);

	public List<MainTainDescTime> getMainTainDescTimeList(MainTainDescTime mtdt);

	public MainTainDescTime getMainTainDescTimeById(Long id);

	public boolean addMainTainDescTime(MainTainDescTime mtdt);

	public boolean updateMainTainDescTime(MainTainDescTime mtdt);

	public boolean delMainTainDescTime(Long id);
	
}

