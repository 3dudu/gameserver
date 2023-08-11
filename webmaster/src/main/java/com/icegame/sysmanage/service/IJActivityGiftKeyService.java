package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.JActivityGift;
import com.icegame.sysmanage.entity.JActivityGiftKey;

import java.util.List;

public interface IJActivityGiftKeyService {

	public PageInfo<JActivityGiftKey> getGiftKeyList(JActivityGiftKey jActivityGift, PageParam pageParam);

	public JActivityGiftKey getGiftKeyById(Long id);

	public JActivityGiftKey getGiftKeyByCDKey(String cdKey);

	public List<JActivityGiftKey> checkExistCdKey(JActivityGiftKey jActivityGiftKey);

	public boolean addGiftKey(JActivityGiftKey jActivityGiftKey);

	public boolean delGiftKey(Long id);

	public boolean updateGiftKey(JActivityGiftKey jActivityGiftKey);
}

