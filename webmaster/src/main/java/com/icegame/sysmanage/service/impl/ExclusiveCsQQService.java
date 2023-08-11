package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.datasources.DataSwitch;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.ExclusiveCsQQ;
import com.icegame.sysmanage.mapper.AutoOpenServerMapper;
import com.icegame.sysmanage.mapper.ExclusiveCsQQMapper;
import com.icegame.sysmanage.service.IAutoOpenServerService;
import com.icegame.sysmanage.service.IExclusiveCsQQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExclusiveCsQQService implements IExclusiveCsQQService {
	
	@Autowired
	private ExclusiveCsQQMapper exclusiveCsQQMapper;


	/**************************
	 ******* api 接口  *********
	 ***************************/

	@Override
	public List<ExclusiveCsQQ> existCurrentPlayer(ExclusiveCsQQ exclusiveCsQQ) {
		return exclusiveCsQQMapper.existCurrentPlayer(exclusiveCsQQ);
	}

	@Override
	public boolean getVipQQStatus(ExclusiveCsQQ exclusiveCsQQ) {
		ExclusiveCsQQ exclusiveCsQQ1 = exclusiveCsQQMapper.getVipQQStatus(exclusiveCsQQ);
		return exclusiveCsQQ1.getIsOpen().equals("1") ? true : false;
	}

	@Override
	public ExclusiveCsQQ getVipQQStatusObject(ExclusiveCsQQ exclusiveCsQQ) {
		ExclusiveCsQQ exclusiveCsQQ1 = exclusiveCsQQMapper.getVipQQStatus(exclusiveCsQQ);
		return exclusiveCsQQ1;
	}

	@Override
	public ExclusiveCsQQ getMoney(ExclusiveCsQQ exclusiveCsQQ) {
		return exclusiveCsQQMapper.getMoney(exclusiveCsQQ);
	}

	@Override
	public List<ExclusiveCsQQ> getMinTimesQQ(ExclusiveCsQQ exclusiveCsQQ) {
		List<ExclusiveCsQQ> exclusiveCsQQList = exclusiveCsQQMapper.getMinTimesQQ(exclusiveCsQQ);
		return exclusiveCsQQList;
	}

	@Override
	public boolean addPlayerRecode(ExclusiveCsQQ exclusiveCsQQ) {
		return exclusiveCsQQMapper.addPlayerRecode(exclusiveCsQQ);
	}

	@Override
	public boolean timesPlus(ExclusiveCsQQ exclusiveCsQQ) {
		return exclusiveCsQQMapper.timesPlus(exclusiveCsQQ);
	}



	/**************************
	 ******* web 页面 接口  ****
	 **************************/


	@Override
	public PageInfo<ExclusiveCsQQ> getQQList(ExclusiveCsQQ exclusiveCsQQ, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<ExclusiveCsQQ> qqList = exclusiveCsQQMapper.getQQList(exclusiveCsQQ);
		PageInfo<ExclusiveCsQQ> pageInfo = new PageInfo<ExclusiveCsQQ>(qqList);
		return pageInfo;
	}

	@Override
	public boolean addQQ(ExclusiveCsQQ exclusiveCsQQ) {
		return exclusiveCsQQMapper.addQQ(exclusiveCsQQ);
	}

	@Override
	public boolean updateQQ(ExclusiveCsQQ exclusiveCsQQ) {
		return exclusiveCsQQMapper.updateQQ(exclusiveCsQQ);
	}

	@Override
	public boolean delQQ(Long id) {
		return exclusiveCsQQMapper.delQQ(id);
	}

	@Override
	public boolean updateQQStatus(String isOpen) {
		ExclusiveCsQQ exclusiveCsQQ = new ExclusiveCsQQ();
		exclusiveCsQQ.setIsOpen(isOpen);
		return exclusiveCsQQMapper.updateQQStatus(exclusiveCsQQ);
	}

	@Override
	public boolean updateMoney(String money) {
		ExclusiveCsQQ exclusiveCsQQ = new ExclusiveCsQQ();
		exclusiveCsQQ.setMoney(money);
		return exclusiveCsQQMapper.updateMoney(exclusiveCsQQ);
	}

	@Override
	public ExclusiveCsQQ getQQById(Long id) {
		return exclusiveCsQQMapper.getQQById(id);
	}

	@Override
	public boolean existsQQ(ExclusiveCsQQ exclusiveCsQQ) {
		List<ExclusiveCsQQ> qqList = exclusiveCsQQMapper.existsQQ(exclusiveCsQQ);
		return qqList.size() > 0 ? true : false;
	}

	@Override
	public boolean unbindQQ(Long id) {
		return exclusiveCsQQMapper.unbindQQ(id);
	}

}
