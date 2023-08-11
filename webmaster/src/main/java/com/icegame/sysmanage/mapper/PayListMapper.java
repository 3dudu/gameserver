package com.icegame.sysmanage.mapper;

import com.icegame.gm.entity.JQuestionnaire;
import com.icegame.sysmanage.entity.JKeyUse;
import com.icegame.sysmanage.entity.JPlayer;
import com.icegame.sysmanage.entity.JUser;
import com.icegame.sysmanage.entity.PayList;

import java.util.List;

public interface PayListMapper {

	public List<PayList> getPayList(PayList payList);

	public List<PayList> getPayListMultServerId(PayList payList);

	public PayList getPayListByOrderId(PayList payList);

	public PayList getPayListByThirdTradeNo(PayList payList);

	public List<PayList> exportAll();

	public List<PayList> exportSelected(String[] ids);

	public List<JPlayer> getJPlayer(JPlayer jPlayer);

	public JUser getJUserById(JUser user);

	public JUser getJUserByName(JUser user);

	public boolean resetJUserPwd(JUser user);

}
