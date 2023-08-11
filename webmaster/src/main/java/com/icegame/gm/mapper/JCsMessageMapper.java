package com.icegame.gm.mapper;

import java.util.List;

import com.icegame.gm.entity.JCsMessage;

public interface JCsMessageMapper {
		
	public List<JCsMessage> getJCsMessageList(JCsMessage jcm);
	
	public List<JCsMessage> getNewMessage(JCsMessage jcm);
	
	public List<JCsMessage> getOldMessage(JCsMessage jcm);
	
	public List<JCsMessage> getJCsMessageListChatting(JCsMessage jcm);
		
	public List<JCsMessage> getJCsMessageById(Long id);
	
	public List<JCsMessage> getLatestMessage(JCsMessage jcm);
	
	public boolean addJCsMessage(JCsMessage jcm);
	
	public boolean addJCsMessageStatus(JCsMessage jcm);
	
	public boolean addJCsMessageProcess(JCsMessage jcm);
	
	public boolean addJCsMessageInfo(JCsMessage jcm);
	
	public boolean updateJCsMessage(JCsMessage jcm);
	
	public JCsMessage getJCsMessageByMsgId(JCsMessage jcm);
	
	public JCsMessage isExistsStatus(JCsMessage jcm);

	public JCsMessage isExistsProcess(JCsMessage jcm);
	
	public JCsMessage isExistsInfo(JCsMessage jcm);
	
	public JCsMessage isSendMail(JCsMessage jcm);
	
	public JCsMessage refreshBanData(JCsMessage jcm);

	public boolean updateStatus(JCsMessage jcm);
	
	public boolean updateProcess(JCsMessage jcm);

	public boolean quickClaim(JCsMessage jcm);

	public boolean updateInfo(JCsMessage jcm);
	
	public boolean openSendMail(JCsMessage jcm);

	public boolean removeClaimState(JCsMessage jcm);
	
	public boolean closeSendMail(JCsMessage jcm);
	
	public boolean addShield(JCsMessage jcm);
	
	public boolean subShield(JCsMessage jcm);
	
	public boolean claimQuestion(JCsMessage jcm);
	
	public boolean finish(JCsMessage jcm);
	
	public boolean open(JCsMessage jcm);
	
	public List<JCsMessage> getCsInfoForSearch(JCsMessage jcm);
	
	public List<JCsMessage> getServerInfoForSearch(JCsMessage jcm);

	public List<JCsMessage> getEnableCs(JCsMessage jcm);

	public List<JCsMessage> getOnlineCs(JCsMessage jcm);

	public List<JCsMessage> getQuestionListNoclaim(JCsMessage jcm);

}
