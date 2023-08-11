package com.icegame.gm.service.impl;

import java.util.*;

import com.icegame.framework.utils.TimeUtils;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JCsMessage;
import com.icegame.gm.mapper.JCsMessageMapper;
import com.icegame.gm.service.IJCsMessageService;

@Service
public class JCsMessageService implements IJCsMessageService {
	
	@Autowired
	private JCsMessageMapper jCsMessageMapper;

	@Autowired
	private UserMapper userMapper;

	/**
	 *
	 * @param jcm
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 12:25:55
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@Override
	public PageInfo<JCsMessage> getJCsMessageList(JCsMessage jcm, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<JCsMessage> jcmList = jCsMessageMapper.getJCsMessageList(jcm);
		if(jcmList.size() > 0){
			for(JCsMessage jc : jcmList){
				jc.setStrCreateTime(TimeUtils.stampToDateWithMill(jc.getCreateTime()));
				jc.setStrUpdateTime(TimeUtils.stampToDateWithMill(jc.getUpdateTime()));
			}
		}
		PageInfo<JCsMessage> pageInfo = new PageInfo<JCsMessage>(jcmList);
		return pageInfo;
	}
	
	@Override
	public List<JCsMessage> getNewMessage(JCsMessage jcm) {
		return jCsMessageMapper.getNewMessage(jcm);
	}
	
	@Override
	public List<JCsMessage> getOldMessage(JCsMessage jcm) {
		return jCsMessageMapper.getOldMessage(jcm);
	}

	@Override
	public List<JCsMessage> getJCsMessageListChatting(JCsMessage jcm) {
		return jCsMessageMapper.getJCsMessageListChatting(jcm);
	}

	@Override
	public List<JCsMessage> getJCsMessageById(Long id) {
		return jCsMessageMapper.getJCsMessageById(id);
	}
	
	@Override
	public List<JCsMessage> getLatestMessage(JCsMessage jcm) {
		return jCsMessageMapper.getLatestMessage(jcm);
	}
	
	@Override
	public boolean addJCsMessage(JCsMessage jcm) {
		return jCsMessageMapper.addJCsMessage(jcm);
	}
	
	@Override
	public boolean addJCsMessageStatus(JCsMessage jcm) {
		return jCsMessageMapper.addJCsMessageStatus(jcm);
	}
	
	@Override
	public boolean addJCsMessageProcess(JCsMessage jcm) {
		return jCsMessageMapper.addJCsMessageProcess(jcm);
	}
	
	@Override
	public boolean addJCsMessageInfo(JCsMessage jcm) {
		return jCsMessageMapper.addJCsMessageInfo(jcm);
	}

	@Override
	public boolean updateJCsMessage(JCsMessage jcm) {
		return jCsMessageMapper.updateJCsMessage(jcm);
	}

	@Override
	public boolean isExistsStatus(JCsMessage jcm) {
		boolean flag = false;
		JCsMessage jCsMessage = jCsMessageMapper.isExistsStatus(jcm);
		if(null != jCsMessage){
			flag = true;
		}
		return flag;
	}
	
	@Override
	public boolean isStatusFinished(JCsMessage jcm) {
		boolean flag = false;
		JCsMessage jCsMessage = jCsMessageMapper.isExistsStatus(jcm);
		if(null != jCsMessage){
			flag = jCsMessage.getStatus() == 0?true:false;
		}else{
			flag = true;
		}
		return flag;
	}
	
	@Override
	public boolean isStatusNotFinished(JCsMessage jcm) {
		return !isStatusFinished(jcm);
	}
	
	@Override
	public boolean isExistsProcess(JCsMessage jcm) {
		boolean flag = false;
		JCsMessage jCsMessage = jCsMessageMapper.isExistsProcess(jcm);
		if(null != jCsMessage){
			flag = true;
		}
		return flag;
	}

	@Override
	public Long getCsIdBySPId(JCsMessage jcm) {
		Long csId = null;
		JCsMessage jCsMessage = jCsMessageMapper.isExistsProcess(jcm);
		if(null != jCsMessage){
			csId = jCsMessage.getCsId();
		}
		return csId;
	}
	
	@Override
	public boolean isExistsInfo(JCsMessage jcm) {
		boolean flag = false;
		JCsMessage jCsMessage = jCsMessageMapper.isExistsInfo(jcm);
		if(null != jCsMessage){
			flag = true;
		}
		return flag;
	}
	
	public boolean sendMail(JCsMessage jcm) {
		boolean flag = false;
		JCsMessage jCsMessage = jCsMessageMapper.isSendMail(jcm);
		if(null != jCsMessage){
			flag = jCsMessage.getSendMail() == 0?true:false;
		}
		return flag;
	}

	@Override
	public JCsMessage refreshBanData(JCsMessage jcm) {
		return jCsMessageMapper.refreshBanData(jcm);
	}	
	
	@Override
	public boolean updateStatus(JCsMessage jcm) {
		return jCsMessageMapper.updateStatus(jcm);
	}
	
	@Override
	public boolean updateProcess(JCsMessage jcm) {
		return jCsMessageMapper.updateProcess(jcm);
	}

	@Override
	public boolean quickClaim(JCsMessage jcm) {
		return jCsMessageMapper.quickClaim(jcm);
	}
	
	@Override
	public boolean updateInfo(JCsMessage jcm) {
		return jCsMessageMapper.updateInfo(jcm);
	}
	
	@Override
	public boolean openSendMail(JCsMessage jcm) {
		return jCsMessageMapper.openSendMail(jcm);
	}
	
	@Override
	public boolean closeSendMail(JCsMessage jcm) {
		return jCsMessageMapper.closeSendMail(jcm);
	}
	
	@Override
	public boolean addShield(JCsMessage jcm) {
		return jCsMessageMapper.addShield(jcm);
	}
	
	@Override
	public boolean subShield(JCsMessage jcm) {
		return jCsMessageMapper.subShield(jcm);
	}

	@Override
	public boolean claimQuestion(JCsMessage jcm) {
		return jCsMessageMapper.claimQuestion(jcm);
	}

	@Override
	public boolean isClaimed(JCsMessage jcm) {
		JCsMessage jCsMessage = jCsMessageMapper.isExistsProcess(jcm);
		boolean flag = false;
		if(null != jCsMessage){
			if(null != jCsMessage.getCsId() && null != jCsMessage.getCsName())
				flag = true;
		}
		return flag;
	}

	@Override
	public List<JCsMessage> getCsInfoForSearch(JCsMessage jcm) {
		return jCsMessageMapper.getCsInfoForSearch(jcm);
	}

	@Override
	public List<JCsMessage> getServerInfoForSearch(JCsMessage jcm) {
		return jCsMessageMapper.getServerInfoForSearch(jcm);
	}

	@Override
	public boolean finish(JCsMessage jcm) {
		return jCsMessageMapper.finish(jcm);
	}

	@Override
	public boolean open(JCsMessage jcm) {
		return jCsMessageMapper.open(jcm);
	}

	@Override
	public JCsMessage getJCsMessageByMsgId(JCsMessage jcm) {
		return jCsMessageMapper.getJCsMessageByMsgId(jcm);
	}

	@Override
	public User getEnableCs(JCsMessage jcm){
		List<JCsMessage> jcmList = jCsMessageMapper.getEnableCs(jcm);
        User user = new User();
		if(jcmList.size() <= 0){
            jcmList = jCsMessageMapper.getOnlineCs(jcm);
        }
		if(jcmList.size() <= 0){
			return null;
		}
        Map<Long,Integer> map = new HashMap<Long,Integer>();
        for(JCsMessage jCsMessage : jcmList){
            map.put(jCsMessage.getCsId(),jCsMessage.getNum());
        }
        List<Object> keyList = getKey(map,getMinValue(map));
        Random random = new Random();
        int n = random.nextInt(keyList.size());
        Long csId = (Long)keyList.get(n);
        user = userMapper.getUserInfoById(csId);

		return user;
	}

	public static Object getMinValue(Map<Long, Integer> map) {
		if (map == null) return null;
		Collection<Integer> c = map.values();
		Object[] obj = c.toArray();
		Arrays.sort(obj);
		return obj[0];
	}

	/**
	 * 根据value找出一个map中的keylist
	 * @param map
	 * @param value
	 * @return
	 */
	public static List<Object> getKey(Map map, Object value){
		List<Object> keyList = new ArrayList<>();
		for(Object key: map.keySet()){
			if(map.get(key).equals(value)){
				keyList.add(key);
			}
		}
		return keyList;
	}


	public static void main(String[] args){
		Map<Long,Integer> map = new HashMap<Long,Integer>();
		map.put(15L,1);
		map.put(22L,1);
		map.put(19L,2);
		map.put(13L,3);
		System.out.println(getMinValue(map));
		System.out.println(getKey(map,getMinValue(map)));
	}

	@Override
	public List<JCsMessage> getQuestionListNoclaim(JCsMessage jcm){
		return jCsMessageMapper.getQuestionListNoclaim(jcm);
	}

}
