package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.ExclusiveCsQQ;
import com.icegame.sysmanage.entity.WechatPub;
import com.icegame.sysmanage.mapper.ExclusiveCsQQMapper;
import com.icegame.sysmanage.mapper.WechatPubMapper;
import com.icegame.sysmanage.service.IExclusiveCsQQService;
import com.icegame.sysmanage.service.IWechatPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WechatPubService implements IWechatPubService {
	
	@Autowired
	private WechatPubMapper wechatPubMapper;

	/**************************
	 ******* api 接口  *********
	 ***************************/

	@Override
	public WechatPub getWeixinStatusObject(WechatPub wechatPub) {
		return wechatPubMapper.getWeixinStatus(wechatPub);
	}

	@Override
	public boolean getWeixinStatus(WechatPub wechatPub) {
		WechatPub wechatPub1 = wechatPubMapper.getWeixinStatus(wechatPub);
		return wechatPub1.getIsOpen().equals("1") ? true : false ;
	}

	@Override
	public WechatPub getPubNumber(WechatPub wechatPub) {
		return wechatPubMapper.getPubNumber(wechatPub);
	}


	/**************************
	 ******* web 页面 接口  ****
	 **************************/


	@Override
	public boolean updateWeixinStatus(String isOpen) {
		return wechatPubMapper.updateWeixinStatus(isOpen);
	}

	@Override
	public boolean updatePubNumber(String name) {
		return wechatPubMapper.updatePubNumber(name);
	}


}
