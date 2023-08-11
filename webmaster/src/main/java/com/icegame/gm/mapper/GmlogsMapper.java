package com.icegame.gm.mapper;

import java.util.List;

import com.icegame.gm.entity.Gmlogs;

public interface GmlogsMapper {
		
	public List<Gmlogs> getGmlogsList(Gmlogs log);
	
	public List<Gmlogs> getTypeList();
		
	public Gmlogs getGmlogsById(Long id);
	
	public boolean addGmlogs(Gmlogs log);
	
	public boolean delGmlogs(Long id);

	public boolean updateGmlogs(Gmlogs log);

}
