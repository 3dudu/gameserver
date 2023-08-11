package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.MainTain;

public interface MainTainMapper {
		
	public List<MainTain> getMainTainList(MainTain mainTain);
				
	public MainTain getMainTainById(Long id);
	
	public boolean addMainTain(MainTain mainTain);
	
	public boolean delMainTain(Long id);

	public boolean updateMainTain(MainTain mainTain);

}
