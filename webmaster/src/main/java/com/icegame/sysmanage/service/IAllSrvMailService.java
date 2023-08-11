package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.AllSrvMail;

import java.util.Map;

public interface IAllSrvMailService {
	
	public PageInfo<AllSrvMail> getAllSrvMailList(AllSrvMail allSrvMail,PageParam pageParam);
	
	public AllSrvMail getAllSrvMailById(Long id);
	
	public boolean addAllSrvMail(AllSrvMail allSrvMail);
	
	public boolean delAllSrvMail(Long id);
	
	public boolean updateAllSrvMail(AllSrvMail allSrvMail);

	public boolean refreshStatus(AllSrvMail allSrvMail);

	public String syncAllSrvMail(Long id);

	public String syncAllSrvMailByChannel(AllSrvMail srvMail, String channel, String format);

	public String syncAllSrvMailBySlave(AllSrvMail srvMail, String slave);


}

