package com.icegame.gm.mapper;

import com.icegame.gm.entity.MainTainDescTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MainTainDescTimeMapper {
		
	public List<MainTainDescTime> getMainTainDescTimeList(MainTainDescTime mtdt);
		
	public MainTainDescTime getMainTainDescTimeById(@Param("id") Long id);
	
	public boolean addMainTainDescTime(MainTainDescTime mtdt);
	
	public boolean updateMainTainDescTime(MainTainDescTime mtdt);
	
	public boolean delMainTainDescTime(@Param("id") Long id);

}
