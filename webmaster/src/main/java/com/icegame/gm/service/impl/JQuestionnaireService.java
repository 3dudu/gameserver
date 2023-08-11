package com.icegame.gm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.NumUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.WriteExcel;
import com.icegame.gm.controller.CSController;
import com.icegame.gm.entity.ExportTable;
import com.icegame.gm.entity.JQuestionnaire;
import com.icegame.gm.mapper.JQuestionnaireMapper;
import com.icegame.gm.service.IJQuestionnaireService;
import com.icegame.gm.util.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

@Service
public class JQuestionnaireService implements IJQuestionnaireService {

	private static Logger logger = Logger.getLogger(JQuestionnaireService.class);
	
	@Autowired
	private JQuestionnaireMapper jQuestionnaireMapper;

	@Override
	public PageInfo<JQuestionnaire> getJQuestionnaireList(JQuestionnaire jqn, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JQuestionnaire> jquestionnaireList = jQuestionnaireMapper.getJQuestionnaireList(jqn);
		PageInfo<JQuestionnaire> pageInfo = new PageInfo<JQuestionnaire>(jquestionnaireList);
		return pageInfo;
	}

	@Override
	public List<JQuestionnaire> getJQuestionnairePlayerId(JQuestionnaire jqn) {
		return jQuestionnaireMapper.getJQuestionnairePlayerId(jqn);
	}

	@Override
	public List<JQuestionnaire> getJQuestionnairePlayerName(JQuestionnaire jqn) {
		return jQuestionnaireMapper.getJQuestionnairePlayerName(jqn);
	}

	@Override
	public JQuestionnaire getJQuestionnaireById(Long id) {
		return jQuestionnaireMapper.getJQuestionnaireById(id);
	}

	@Override
	public boolean addJQuestionnaire(JQuestionnaire jqn) {
		return jQuestionnaireMapper.addJQuestionnaire(jqn);
	}

    @Override
    public boolean updateJQuestionnaire(JQuestionnaire jqn) {
        return jQuestionnaireMapper.updateJQuestionnaire(jqn);
    }

    @Override
    public boolean exists(JQuestionnaire jqn) {
	    boolean flag = false;
	    List<JQuestionnaire> list = jQuestionnaireMapper.exists(jqn);
	    if(null != list && list.size() > 0){
	        flag = true;
        }
        return flag;
    }

	@Override
	public boolean exists9(JQuestionnaire jqn) {
		boolean flag = false;
		JQuestionnaire jQuestionnaire = jQuestionnaireMapper.exists9(jqn);
		if(null != jQuestionnaire && jQuestionnaire.getQuestion9().length() > 0){
			flag = true;
		}
		return flag;
	}

	@Override
	public InputStream getInputStreamRetention(ExportTable et) throws Exception {
		String[] title=new String[]{"玩家ID","玩家姓名","问题1","问题2","问题3","问题4","问题5","问题6","问题7","问题8","问题9","问题10"};
		List<JQuestionnaire> jQuestionnairesList = new ArrayList<JQuestionnaire>();
		if(et.getFlag() == 1){
            jQuestionnairesList = jQuestionnaireMapper.exportAll();
        }else if(et.getFlag() == 2){
            jQuestionnairesList = jQuestionnaireMapper.exportSelected(et.getIds().split(","));
        }
		List<Object[]>  dataList = new ArrayList<Object[]>();
		Object[] obj = null;
		try{
			for(int i=0;i<jQuestionnairesList.size();i++){
				obj=new Object[12];
				obj[0] = jQuestionnairesList.get(i).getPlayerId();
				obj[1] = jQuestionnairesList.get(i).getPlayerName();
				obj[2] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion1())?jQuestionnairesList.get(i).getQuestion1():" ";
				obj[3] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion2())?jQuestionnairesList.get(i).getQuestion2():" ";
				obj[4] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion3())?jQuestionnairesList.get(i).getQuestion3():" ";
				obj[5] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion4())?jQuestionnairesList.get(i).getQuestion4():" ";
				obj[6] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion5())?jQuestionnairesList.get(i).getQuestion5():" ";
				obj[7] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion6())?jQuestionnairesList.get(i).getQuestion6():" ";
				obj[8] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion7())?jQuestionnairesList.get(i).getQuestion7():" ";
				obj[9] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion8())?jQuestionnairesList.get(i).getQuestion8():" ";
				obj[10] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion9())?jQuestionnairesList.get(i).getQuestion9():" ";
				obj[11] = StringUtils.isNotNull(jQuestionnairesList.get(i).getQuestion10())?jQuestionnairesList.get(i).getQuestion10():" ";
				dataList.add(obj);
			}
		}catch (Exception e){
			logger.error("- ---|export table failed|--- ->"+e.getMessage());
		}

		WriteExcel ex = new WriteExcel(title, dataList);
		InputStream in = null;
		in = ex.export();
		return in;
	}

}
