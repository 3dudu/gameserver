package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.JActivityGift;
import com.icegame.sysmanage.entity.JActivityGiftKey;
import com.icegame.sysmanage.entity.JKeyUse;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.mapper.JActivityGiftMapper;
import com.icegame.sysmanage.mapper.JKeyUseMapper;
import com.icegame.sysmanage.service.IJActivityGiftService;
import com.icegame.sysmanage.service.IJKeyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JKeyUseService implements IJKeyUseService {
	
	@Autowired
	private JKeyUseMapper jKeyUseMapper;

	@Override
	public PageInfo<JKeyUse> getGiftList(JKeyUse jKeyUse, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JKeyUse> keyList = jKeyUseMapper.getKeyUseList(jKeyUse);
		PageInfo<JKeyUse> pageInfo = new PageInfo<JKeyUse>(keyList);
		return pageInfo;
	}

	@Override
	public JKeyUse getKeyUseById(Long id) {
		return jKeyUseMapper.getKeyUseById(id);
	}

    @Override
    public JKeyUse getKeyUseByKey(String key) {
        return jKeyUseMapper.getKeyUseByKey(key);
    }

    @Override
    public int usedTimes(String key) {
	    List<JKeyUse> jKeyUsesList = jKeyUseMapper.usedTimes(new JKeyUse(key));
        return jKeyUsesList.size();
    }

    @Override
    public boolean isUsed(String key) {
        JKeyUse jKeyUses = jKeyUseMapper.getKeyUseByKey(key);
        //List<JKeyUse> jKeyUsesList = jKeyUseMapper.usedTimes(new JKeyUse(key));
        return null != jKeyUses ? true : false ;
    }

	@Override
	public boolean isUsedSameType(JKeyUse jKeyUse) {
		List<JKeyUse> jKeyUsesList = jKeyUseMapper.isUsedSameType(jKeyUse);
		return jKeyUsesList.size() > 0 ? true : false;
	}

	@Override
	public boolean isUsedNoLimit(JKeyUse jKeyUse) {
		JKeyUse jKeyUses = jKeyUseMapper.getKeyUseByKeyNoLimit(jKeyUse);
		//List<JKeyUse> jKeyUsesList = jKeyUseMapper.usedTimes(new JKeyUse(key));
		return null != jKeyUses ? true : false ;
	}

    @Override
	public boolean addKeyUse(JKeyUse jKeyUse) {
		return jKeyUseMapper.addKeyUse(jKeyUse);
	}

	@Override
	public boolean delKeyUse(Long id) {
		return jKeyUseMapper.delKeyUse(id);
	}

	@Override
	public boolean updateKeyUse(JKeyUse jKeyUse) {
		return jKeyUseMapper.updateKeyUse(jKeyUse);
	}

	@Override
	public boolean isThisPlayerUsed(JKeyUse jKeyUse){
		List<JKeyUse> jKeyUseList = jKeyUseMapper.isThisPlayerUsed(jKeyUse);
		return jKeyUseList.size() > 0 ? true : false;
	}
}
