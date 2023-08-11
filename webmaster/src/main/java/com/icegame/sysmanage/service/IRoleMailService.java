package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.RoleMail;

public interface IRoleMailService {
	
	public PageInfo<RoleMail> getRoleMailList(RoleMail roleMail,PageParam pageParam);

	public RoleMail getRoleMailById(Long id);
	
	public boolean addRoleMail(RoleMail roleMail);
	
	public boolean delRoleMail(Long id);

	public boolean updateRoleMail(RoleMail roleMail);

	public boolean refreshStatus(RoleMail roleMail);

	public String syncRoleMail(Long id);
	
}

