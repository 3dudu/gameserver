package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JAutoreplyMessage;

import java.util.List;

public interface IJAutoreplyMessageService {

	public PageInfo<JAutoreplyMessage> getJAutoreplyMessageList(JAutoreplyMessage jam, PageParam pageParam);

	public List<JAutoreplyMessage> getJAutoreplyMessageList(JAutoreplyMessage jam);

	public JAutoreplyMessage getJAutoreplyMessageById(Long id);

	public boolean addJAutoreplyMessage(JAutoreplyMessage jam);

	public boolean updateJAutoreplyMessage(JAutoreplyMessage jam);

	public boolean delJAutoreplyMessage(Long id);
	
}

