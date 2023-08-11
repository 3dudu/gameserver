package com.icegame.gm.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JCsMessage;
import com.icegame.sysmanage.entity.User;

public interface IJCsMessageService {
	
	public PageInfo<JCsMessage> getJCsMessageList(JCsMessage jcm,PageParam pageParam);
	
	public List<JCsMessage> getNewMessage(JCsMessage jcm);
	
	public List<JCsMessage> getOldMessage(JCsMessage jcm);
	
	public List<JCsMessage> getJCsMessageListChatting(JCsMessage jcm);
	
	public List<JCsMessage> getJCsMessageById(Long id);
	
	public List<JCsMessage> getLatestMessage(JCsMessage jcm);
	
	public boolean addJCsMessage(JCsMessage jcm);
	
	public boolean addJCsMessageStatus(JCsMessage jcm);
	
	public boolean addJCsMessageProcess(JCsMessage jcm);
	
	public boolean addJCsMessageInfo(JCsMessage jcm);
	
	public boolean isExistsStatus(JCsMessage jcm);
	
	public boolean isStatusFinished(JCsMessage jcm);
	
	public boolean isStatusNotFinished(JCsMessage jcm);

	public Long getCsIdBySPId(JCsMessage jcm);
	
	public boolean sendMail(JCsMessage jcm);
	
	public boolean isExistsProcess(JCsMessage jcm);
	
	public boolean isExistsInfo(JCsMessage jcm);
	
	public JCsMessage getJCsMessageByMsgId(JCsMessage jcm);
	
	public JCsMessage refreshBanData(JCsMessage jcm);
		
	public boolean updateStatus(JCsMessage jcm);
	
	public boolean updateProcess(JCsMessage jcm);

	public boolean quickClaim(JCsMessage jcm);
	
	public boolean updateJCsMessage(JCsMessage jcm);
	
	public boolean updateInfo(JCsMessage jcm);
	
	public boolean openSendMail(JCsMessage jcm);
	
	public boolean closeSendMail(JCsMessage jcm);
	
	public boolean addShield(JCsMessage jcm);
	
	public boolean subShield(JCsMessage jcm);
	
	public boolean claimQuestion(JCsMessage jcm);
	
	public boolean finish(JCsMessage jcm);

	public boolean isClaimed(JCsMessage jcm);
	
	public boolean open(JCsMessage jcm);
	
	public List<JCsMessage> getCsInfoForSearch(JCsMessage jcm);
	
	public List<JCsMessage> getServerInfoForSearch(JCsMessage jcm);

	public User getEnableCs(JCsMessage jcm);

	public List<JCsMessage> getQuestionListNoclaim(JCsMessage jcm);

}

