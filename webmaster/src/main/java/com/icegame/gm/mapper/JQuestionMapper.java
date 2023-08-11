package com.icegame.gm.mapper;

import java.util.List;

import com.icegame.gm.entity.JQuestion;

		
public interface JQuestionMapper {
	
	public List<JQuestion> getJQuestionList(JQuestion jquestion);
	
	public List<JQuestion> getJQuestionListNoPage(JQuestion jquestion);
	
	public List<JQuestion> getQuestionServerList(JQuestion jquestion);
		
	public JQuestion getJQuestionById(Long id);
	
	public JQuestion getJQuestionByCsId(Long csId);
	
	public boolean addJQuestion(JQuestion jquestion);
	
	public boolean claimQuestion(JQuestion jquestion);
	
	public boolean finish(JQuestion jquestion);

}
