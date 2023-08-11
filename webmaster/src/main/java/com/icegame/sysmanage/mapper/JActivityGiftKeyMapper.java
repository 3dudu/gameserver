package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.JActivityGiftKey;

import java.util.List;

public interface JActivityGiftKeyMapper {
		
	public List<JActivityGiftKey> getGiftKeyList(JActivityGiftKey jActivityGiftKey);
			
	public JActivityGiftKey getGiftKeyById(Long id);

	public JActivityGiftKey getGiftKeyByCDKey(JActivityGiftKey jActivityGiftKey);

	public List<JActivityGiftKey> checkExistCdKey(JActivityGiftKey jActivityGiftKey);

	public boolean addGiftKey(JActivityGiftKey jActivityGiftKey);
	
	public boolean delGiftKey(Long id);

	public boolean updateGiftKey(JActivityGiftKey jActivityGiftKey);

}
