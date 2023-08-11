package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.JActivityGift;
import com.icegame.sysmanage.entity.JActivityGiftKey;
import com.icegame.sysmanage.entity.JKeyUse;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.mapper.JActivityGiftKeyMapper;
import com.icegame.sysmanage.mapper.JActivityGiftMapper;
import com.icegame.sysmanage.mapper.JKeyUseMapper;
import com.icegame.sysmanage.service.IJActivityGiftKeyService;
import com.icegame.sysmanage.service.IJActivityGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JActivityGiftKeyService implements IJActivityGiftKeyService {
	
	@Autowired
	private JActivityGiftKeyMapper jActivityGiftKeyMapper;

	@Autowired
	private JKeyUseMapper jKeyUseMapper;

	@Autowired
	private LogService logService;
	
	Log log = new Log();

	/**
	 *
	 * @param jActivityGiftKey
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 11:17:42
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@Override
	public PageInfo<JActivityGiftKey> getGiftKeyList(JActivityGiftKey jActivityGiftKey, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JActivityGiftKey> giftKeyList = jActivityGiftKeyMapper.getGiftKeyList(jActivityGiftKey);
		if(giftKeyList.size() > 0){
			for(JActivityGiftKey jagk : giftKeyList){
				jagk.setStartTime(TimeUtils.stampToDateWithMill(jagk.getStartTime()));
				jagk.setPassTime(TimeUtils.stampToDateWithMill(jagk.getPassTime()));
			}
		}
		PageInfo<JActivityGiftKey> pageInfo = new PageInfo<JActivityGiftKey>(giftKeyList);
		return pageInfo;
	}

	@Override
	public JActivityGiftKey getGiftKeyById(Long id) {
		return jActivityGiftKeyMapper.getGiftKeyById(id);
	}

	@Override
	public JActivityGiftKey getGiftKeyByCDKey(String cdKey) {
		JActivityGiftKey jag = new JActivityGiftKey();jag.setCdKey(cdKey);
		JActivityGiftKey jActivityGiftKey = jActivityGiftKeyMapper.getGiftKeyByCDKey(jag);
		return jActivityGiftKey;
	}

	@Override
	public List<JActivityGiftKey> checkExistCdKey(JActivityGiftKey jActivityGiftKey) {
		return jActivityGiftKeyMapper.checkExistCdKey(jActivityGiftKey);
	}

	@Override
	public boolean addGiftKey(JActivityGiftKey jActivityGiftKey) {
		log = UserUtils.recording("添加礼包码",Type.ADD.getName());
		logService.addLog(log);
		return jActivityGiftKeyMapper.addGiftKey(jActivityGiftKey);
	}

	@Override
	public boolean delGiftKey(Long id) {

		boolean flag = false;

		JActivityGiftKey jActivityGiftKey = getGiftKeyById(id);

		log = UserUtils.recording("删除礼包码",Type.DELETE.getName());
		logService.addLog(log);

		flag = jActivityGiftKeyMapper.delGiftKey(id);

		JKeyUse jKeyUse = new JKeyUse();
		jKeyUse.setCdKey(jActivityGiftKey.getCdKey());

		flag = jKeyUseMapper.delKeyUseByCdKey(jKeyUse);

		return flag;
	}

	@Override
	public boolean updateGiftKey(JActivityGiftKey jActivityGiftKey) {
		log = UserUtils.recording("修改礼包码",Type.UPDATE.getName());
		logService.addLog(log);
		return jActivityGiftKeyMapper.updateGiftKey(jActivityGiftKey);
	}
}
