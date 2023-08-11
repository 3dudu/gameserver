package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.SelSrvMail;

public interface ISelSrvMailService {
	
	public PageInfo<SelSrvMail> getSelSrvMailList(SelSrvMail selSrvMail,PageParam pageParam);

	public SelSrvMail getSelSrvMailById(Long id);
	
	public boolean addSelSrvMail(SelSrvMail selSrvMail);
	
	public boolean delSelSrvMail(Long id);

	public boolean updateSelSrvMail(SelSrvMail selSrvMail);

	public boolean refreshStatus(SelSrvMail selSrvMail);

	public String syncSelSrvMail(Long id);


}

