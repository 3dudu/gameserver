package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.RoleMail;

import java.util.List;

public interface RoleMailMapper {
		
	public List<RoleMail> getRoleMailList(RoleMail roleMail);
			
	public RoleMail getRoleMailById(Long id);
	
	public boolean addRoleMail(RoleMail roleMail);
	
	public boolean delRoleMail(Long id);

	public boolean updateRoleMail(RoleMail roleMail);

	public boolean refreshStatus(RoleMail roleMail);

}
