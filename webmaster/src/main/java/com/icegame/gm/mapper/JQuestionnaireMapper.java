package com.icegame.gm.mapper;

import com.icegame.gm.entity.JEvaluate;
import com.icegame.gm.entity.JQuestionnaire;

import java.util.List;

public interface JQuestionnaireMapper {
		
	public List<JQuestionnaire> getJQuestionnaireList(JQuestionnaire jqn);

    public List<JQuestionnaire> getJQuestionnairePlayerId(JQuestionnaire jqn);

    public List<JQuestionnaire> getJQuestionnairePlayerName(JQuestionnaire jqn);

    public List<JQuestionnaire> exportAll();

	public List<JQuestionnaire> exportSelected(String[] ids);
		
	public JQuestionnaire getJQuestionnaireById(Long id);
	
	public boolean addJQuestionnaire(JQuestionnaire jqn);

	public boolean updateJQuestionnaire(JQuestionnaire jqn);

	public List<JQuestionnaire> exists(JQuestionnaire jqn);

	public JQuestionnaire exists9(JQuestionnaire jqn);

}
