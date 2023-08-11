package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.JActivityGift;

import java.util.List;

public interface IJActivityGiftService {

	public PageInfo<JActivityGift> getGiftList(JActivityGift jActivityGift, PageParam pageParam);

	public JActivityGift getGiftById(Long id);

	public JActivityGift getGiftByCDKey(String cdKey);

	public boolean addGift(JActivityGift jActivityGift);

	public boolean delGift(Long id);

	public boolean updateGift(JActivityGift jActivityGift);
}

