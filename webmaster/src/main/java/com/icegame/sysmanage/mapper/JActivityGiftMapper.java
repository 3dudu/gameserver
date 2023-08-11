package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.JActivityGift;

import java.util.List;

public interface JActivityGiftMapper {
		
	public List<JActivityGift> getGiftList(JActivityGift jActivityGift);
			
	public JActivityGift getGiftById(Long id);

	public JActivityGift getGiftByCDKey(JActivityGift jActivityGift);

	public boolean addGift(JActivityGift jActivityGift);
	
	public boolean delGift(Long id);

	public boolean updateGift(JActivityGift jActivityGift);

}
