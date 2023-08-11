package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JEvaluate;

import java.util.List;

public interface IJEvaluateService {

	public PageInfo<JEvaluate> getJEvaluateList(JEvaluate jEvaluate, PageParam pageParam);

    public List<JEvaluate> getEvaluateNpcId(JEvaluate jEvaluate);

    public List<JEvaluate> getEvaluateNpcName(JEvaluate jEvaluate);

	public JEvaluate getJEvaluateById(Long id);

	public boolean addJEvaluate(JEvaluate jEvaluate);

	public boolean exists(JEvaluate jEvaluate);

    public boolean updateJEvaluate(JEvaluate jEvaluate);

	public boolean updateAvalue(JEvaluate jEvaluate);
}

