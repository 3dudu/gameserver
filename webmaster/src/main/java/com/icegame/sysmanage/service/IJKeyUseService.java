package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.JActivityGiftKey;
import com.icegame.sysmanage.entity.JKeyUse;

import java.util.List;

public interface IJKeyUseService {

	public PageInfo<JKeyUse> getGiftList(JKeyUse jKeyUse, PageParam pageParam);

	public JKeyUse getKeyUseById(Long id);

	public JKeyUse getKeyUseByKey(String key);

	public int usedTimes(String key);

	public boolean isUsedNoLimit(JKeyUse jKeyUse);

	public boolean isUsed(String key);

	public boolean isUsedSameType(JKeyUse jKeyUse);

	public boolean addKeyUse(JKeyUse jKeyUse);

	public boolean delKeyUse(Long id);

	public boolean updateKeyUse(JKeyUse jKeyUse);

	public boolean isThisPlayerUsed(JKeyUse jKeyUse);
}

