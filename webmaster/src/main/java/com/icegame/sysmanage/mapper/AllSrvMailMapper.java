package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.AllSrvMail;

import java.util.List;

public interface AllSrvMailMapper {
		
	public List<AllSrvMail> getAllSrvMailList(AllSrvMail allSrvMail);
			
	public AllSrvMail getAllSrvMailById(Long id);
	
	public boolean addAllSrvMail(AllSrvMail allSrvMail);
	
	public boolean delAllSrvMail(Long id);

	public boolean updateAllSrvMail(AllSrvMail allSrvMail);

	public boolean refreshStatus(AllSrvMail allSrvMail);

}
