package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.MainTain;
import com.icegame.sysmanage.entity.OpclServerStatus;

import java.util.List;

public interface IMainTainService {
	
	public PageInfo<MainTain> getMainTainList(MainTain mainTain,PageParam pageParam);

	public MainTain getMainTainById(Long id);
	
	public boolean addMainTain(MainTain mainTain);
	
	public boolean delMainTain(Long id);

	public boolean updateMainTain(MainTain mainTain);
	
	public List<OpclServerStatus> saveSync(MainTain mainTain);
	
}

