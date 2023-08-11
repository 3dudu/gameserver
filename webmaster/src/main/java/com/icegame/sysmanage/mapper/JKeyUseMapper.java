package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.JKeyUse;

import java.util.List;

public interface JKeyUseMapper {
		
	public List<JKeyUse> getKeyUseList(JKeyUse jKeyUse);
			
	public JKeyUse getKeyUseById(Long id);

	public JKeyUse getKeyUseByKey(String key);

	public JKeyUse getKeyUseByKeyNoLimit(JKeyUse jKeyUse);

	public List<JKeyUse> usedTimes(JKeyUse jKeyUse);

	public List<JKeyUse> isUsedSameType(JKeyUse jKeyUse);
	
	public boolean addKeyUse(JKeyUse jKeyUse);

	public boolean delKeyUse(Long id);

	public boolean delKeyUseByCdKey(JKeyUse jKeyUse);

	public boolean updateKeyUse(JKeyUse jKeyUse);

	public List<JKeyUse> isThisPlayerUsed(JKeyUse jKeyUse);

}
