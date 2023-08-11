package com.icegame.gm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JEvaluate;
import com.icegame.gm.entity.JReduceLog;
import com.icegame.gm.mapper.JEvaluateMapper;
import com.icegame.gm.mapper.JReduceLogMapper;
import com.icegame.gm.service.IJEvaluateService;
import com.icegame.gm.service.IJReduceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JEvaluateService implements IJEvaluateService {
	
	@Autowired
	private JEvaluateMapper jEvaluateMapper;
	
	public PageInfo<JEvaluate> getJEvaluateList(JEvaluate jEvaluate, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JEvaluate> jevaluateList = jEvaluateMapper.getJEvaluateList(jEvaluate);
		PageInfo<JEvaluate> pageInfo = new PageInfo<JEvaluate>(jevaluateList);
		return pageInfo;
	}

    @Override
    public List<JEvaluate> getEvaluateNpcId(JEvaluate jEvaluate) {
        return jEvaluateMapper.getEvaluateNpcId(jEvaluate);
    }

    @Override
    public List<JEvaluate> getEvaluateNpcName(JEvaluate jEvaluate) {
        return jEvaluateMapper.getEvaluateNpcName(jEvaluate);
    }

    @Override
	public JEvaluate getJEvaluateById(Long id) {
		return jEvaluateMapper.getJEvaluateById(id);
	}

	@Override
	public boolean addJEvaluate(JEvaluate jEvaluate) {
		return jEvaluateMapper.addJEvaluate(jEvaluate);
	}

	@Override
	public boolean exists(JEvaluate jEvaluate) {
		boolean exists =  false;
		List<JEvaluate> jevaluateList = jEvaluateMapper.exists(jEvaluate);
		if(null != jevaluateList && jevaluateList.size() > 0){
			exists = true;
		}
		return exists;
	}

    @Override
    public boolean updateJEvaluate(JEvaluate jEvaluate) {
        return jEvaluateMapper.updateJEvaluate(jEvaluate);
    }

	@Override
	public boolean updateAvalue(JEvaluate jEvaluate) {
		return jEvaluateMapper.updateAvalue(jEvaluate);
	}


}
