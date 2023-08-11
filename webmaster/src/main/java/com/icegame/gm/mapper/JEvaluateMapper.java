package com.icegame.gm.mapper;

import com.icegame.gm.entity.JEvaluate;
import java.util.List;

public interface JEvaluateMapper {
		
	public List<JEvaluate> getJEvaluateList(JEvaluate jEvaluate);

    public List<JEvaluate> getEvaluateNpcId(JEvaluate jEvaluate);

    public List<JEvaluate> getEvaluateNpcName(JEvaluate jEvaluate);
		
	public JEvaluate getJEvaluateById(Long id);
	
	public boolean addJEvaluate(JEvaluate jEvaluate);

	public List<JEvaluate> exists(JEvaluate jEvaluate);

    public boolean updateJEvaluate(JEvaluate jEvaluate);

	public boolean updateAvalue(JEvaluate jEvaluate);

}
