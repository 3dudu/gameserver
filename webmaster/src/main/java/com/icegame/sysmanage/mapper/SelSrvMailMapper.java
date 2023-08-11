package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.SelSrvMail;

import java.util.List;

public interface SelSrvMailMapper {
		
	public List<SelSrvMail> getSelSrvMailList(SelSrvMail selSrvMail);
			
	public SelSrvMail getSelSrvMailById(Long id);
	
	public boolean addSelSrvMail(SelSrvMail selSrvMail);
	
	public boolean delSelSrvMail(Long id);

	public boolean updateSelSrvMail(SelSrvMail selSrvMail);

	public boolean refreshStatus(SelSrvMail selSrvMail);

}
