package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.JActivityGift;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.mapper.JActivityGiftMapper;
import com.icegame.sysmanage.service.IJActivityGiftService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class JActivityGiftService implements IJActivityGiftService {
	
	@Autowired
	private JActivityGiftMapper jActivityGiftMapper;
	
	@Autowired
	private LogService logService;
	
	Log log = new Log();

	@Override
	public PageInfo<JActivityGift> getGiftList(JActivityGift jActivityGift, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JActivityGift> giftList = jActivityGiftMapper.getGiftList(jActivityGift);
		PageInfo<JActivityGift> pageInfo = new PageInfo<JActivityGift>(giftList);
		return pageInfo;
	}

	@Override
	public JActivityGift getGiftById(Long id) {
		JActivityGift jActivityGift = jActivityGiftMapper.getGiftById(id);
		StringBuffer sb = new StringBuffer();
		String[] cdKeyList = jActivityGift.getCdKeyList().split(",");
		for(String obj : cdKeyList){
			sb.append(obj).append("\r\n");
		}
		jActivityGift.setCdKeyList(sb.toString());
		return jActivityGift;
	}

	@Override
	public JActivityGift getGiftByCDKey(String cdKey) {
		JActivityGift jag = new JActivityGift();jag.setCdKeyList(cdKey);
		JActivityGift jActivityGift = jActivityGiftMapper.getGiftByCDKey(jag);
		return jActivityGift;
	}

	@Override
	public boolean addGift(JActivityGift jActivityGift) {
		log = UserUtils.recording("添加礼包",Type.ADD.getName());
		logService.addLog(log);
		return jActivityGiftMapper.addGift(jActivityGift);
	}

	@Override
	public boolean delGift(Long id) {
		log = UserUtils.recording("删除礼包",Type.DELETE.getName());
		logService.addLog(log);
		return jActivityGiftMapper.delGift(id);
	}

	@Override
	public boolean updateGift(JActivityGift jActivityGift) {
		log = UserUtils.recording("修改礼包",Type.UPDATE.getName());
		logService.addLog(log);
		return jActivityGiftMapper.updateGift(jActivityGift);
	}
}
