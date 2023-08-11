package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.NumUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.WriteExcel;
import com.icegame.gm.entity.ExportTable;
import com.icegame.gm.entity.JEvaluate;
import com.icegame.gm.entity.JQuestionnaire;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

public interface IJQuestionnaireService {

	public PageInfo<JQuestionnaire> getJQuestionnaireList(JQuestionnaire jqn, PageParam pageParam);

	public List<JQuestionnaire> getJQuestionnairePlayerId(JQuestionnaire jqn);

	public List<JQuestionnaire> getJQuestionnairePlayerName(JQuestionnaire jqn);

	public JQuestionnaire getJQuestionnaireById(Long id);

	public boolean addJQuestionnaire(JQuestionnaire jqn);

    public boolean updateJQuestionnaire(JQuestionnaire jqn);

	public boolean exists(JQuestionnaire jqn);

	public boolean exists9(JQuestionnaire jqn);

	public InputStream getInputStreamRetention(ExportTable et) throws Exception;
}

