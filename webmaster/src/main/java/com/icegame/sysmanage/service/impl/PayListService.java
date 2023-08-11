package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.datasources.DataSwitch;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.WriteExcel;
import com.icegame.gm.entity.ExportTable;
import com.icegame.gm.entity.JQuestionnaire;
import com.icegame.gm.service.impl.JQuestionnaireService;
import com.icegame.gm.util.Misc;
import com.icegame.sysmanage.dto.PayListDto;
import com.icegame.sysmanage.entity.JKeyUse;
import com.icegame.sysmanage.entity.JPlayer;
import com.icegame.sysmanage.entity.JUser;
import com.icegame.sysmanage.entity.PayList;
import com.icegame.sysmanage.mapper.JKeyUseMapper;
import com.icegame.sysmanage.mapper.PayListMapper;
import com.icegame.sysmanage.service.IJKeyUseService;
import com.icegame.sysmanage.service.IPayListService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayListService implements IPayListService {

	private static Logger logger = Logger.getLogger(PayListService.class);

	@Autowired
	private PayListMapper payListMapper;

	/**
	 *
	 * @param payList
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 11:25:10
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@Override
	@DataSwitch(dataSource="dataSourceLoginPay")
	public PageInfo<PayList> getPayList(PayList payList, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<PayList> payLists = new ArrayList<>();
		if(StringUtils.isNotNull(payList.getMultServerId()) && payList.getSid() == null){
			payLists = payListMapper.getPayListMultServerId(payList);
		}else{
			payLists = payListMapper.getPayList(payList);
		}
		if(payLists.size() > 0){
			for(PayList pl : payLists){
				pl.setStrCreateTime(TimeUtils.stampToDateWithMill(pl.getCreateTime()));

				// 注释此行是 因为 支付订单页面没有显示完成时间，所以先注释掉，后续在打开
				//pl.setStrFinishTime(TimeUtils.stampToDateWithMill(pl.getFinishTime()));
			}
		}
		PageInfo<PayList> pageInfo = new PageInfo<PayList>(payLists);
		return pageInfo;
	}

	@Override
	@DataSwitch(dataSource="dataSourceLoginPay")
	public PageInfo<PayListDto> getPayListResetPwd(PayList payList, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<PayListDto> payListDtos = new ArrayList<PayListDto>();
		JUser user = new JUser();
		List<JPlayer> playerList = new ArrayList<JPlayer>();
		if(payList.getOrderId() != "" || payList.getThirdTradeNo() != ""){	 //订单
			PayList payLists = new PayList();
			if(payList.getOrderId() != ""){
				payLists = payListMapper.getPayListByOrderId(payList);
			}else if(payList.getThirdTradeNo() != ""){
				payLists = payListMapper.getPayListByThirdTradeNo(payList);
			}

			if(payLists != null && payLists.getUserId() != null){
				user = payListMapper.getJUserById(new JUser(Long.valueOf(payLists.getUserId())));
				if(user != null){
					JPlayer jPlayer = new JPlayer();
					jPlayer.setUid(user.getId());
					List<JPlayer> jPlayers =  payListMapper.getJPlayer(jPlayer);
					if (jPlayers.size() > 0){
						for (JPlayer player:jPlayers) {
							PayListDto payListDto = new PayListDto();
							payListDto.adapt(user,player);
							payListDtos.add(payListDto);
						}
					}else {
						PayListDto payListDto = new PayListDto();
						payListDto.adapt(user,null);
						payListDtos.add(payListDto);
					}
				}
			}else if(payLists != null && payLists.getPid() != null){
				playerList = payListMapper.getJPlayer(new JPlayer(Long.valueOf(payLists.getPid())));
				if(playerList.size() > 0){
					for(JPlayer player : playerList){
						if(player != null){
							user = payListMapper.getJUserById(new JUser(Long.valueOf(player.getUid())));
							if(user != null){
								PayListDto payListDto = new PayListDto();
								payListDto.adapt(user,player);
								payListDtos.add(payListDto);
							}
						}
					}
				}
			}

		}else if(null != payList.getPid() || payList.getPlayerName() != ""){	//角色ID && 角色名 player表
			JPlayer jPlayer = new JPlayer();
			if (null != payList.getPid()){
				jPlayer.setPid(Long.valueOf(payList.getPid()));
			}
			if (null != payList.getSid()){
				jPlayer.setSid(payList.getSid().intValue());
			}
			jPlayer.setName(payList.getPlayerName());
			playerList = payListMapper.getJPlayer(jPlayer);
			if(playerList.size() > 0){
				for(JPlayer player : playerList){
					if(player != null){
						user = payListMapper.getJUserById(new JUser(Long.valueOf(player.getUid())));
						if(user != null){
							PayListDto payListDto = new PayListDto();
							payListDto.adapt(user,player);
							payListDtos.add(payListDto);
						}
					}
				}
			}
			/*if(player.getUid() > 0){
				user = payListMapper.getJUserById(new JUser(Long.valueOf(player.getUid())));
				userLists.add(user);
			}*/
		}else if(payList.getUsername() != ""){	//用户名
			if(payList.getUsername().length() > 0){
				user = payListMapper.getJUserByName(new JUser(payList.getUsername()));
				if(user != null){
					JPlayer jPlayer = new JPlayer();
					jPlayer.setUid(user.getId());
					List<JPlayer> jPlayers =  payListMapper.getJPlayer(jPlayer);
					if (jPlayers.size() > 0){
						for (JPlayer player:jPlayers) {
							PayListDto payListDto = new PayListDto();
							payListDto.adapt(user,player);
							payListDtos.add(payListDto);
						}
					}else {
						PayListDto payListDto = new PayListDto();
						payListDto.adapt(user,null);
						payListDtos.add(payListDto);
					}
				}
			}
		}

		PageInfo<PayListDto> pageInfo = new PageInfo<PayListDto>(payListDtos);
		return pageInfo;
	}

	@Override
	@DataSwitch(dataSource="dataSourceLoginPay")
	public boolean resetJUserPwd(Long id,String username,String passwd) {
		JUser jUser = new JUser(id,username, Misc.md5(passwd));
		return payListMapper.resetJUserPwd(jUser);
	}

	@Override
	@DataSwitch(dataSource="dataSourceLoginPay")
	public InputStream getInputStreamRetention(ExportTable et) throws Exception {
		String[] title=new String[]{"订单号","第三方订单号","创建时间","玩家ID","区服ID","角色ID","支付金额","档位","平台","渠道","资源","交易状态"};
		List<PayList> payLists = new ArrayList<PayList>();
		if(et.getFlag() == 1){
			payLists = payListMapper.exportAll();
		}else if(et.getFlag() == 2){
			payLists = payListMapper.exportSelected(et.getIds().split(","));
		}
		List<Object[]>  dataList = new ArrayList<Object[]>();
		Object[] obj = null;
		try{
			for(int i=0;i<payLists.size();i++){
				obj=new Object[12];
				obj[0] = StringUtils.isNotNull(payLists.get(i).getOrderId())?payLists.get(i).getOrderId():" ";
				obj[1] = StringUtils.isNotNull(payLists.get(i).getThirdTradeNo())?payLists.get(i).getThirdTradeNo():" ";
				obj[2] = TimeUtils.stampToDateWithMill(String.valueOf(StringUtils.isNotNull(payLists.get(i).getCreateTime())?payLists.get(i).getCreateTime():" "));
				obj[3] = StringUtils.isNotNull(payLists.get(i).getUserId())?payLists.get(i).getUserId():" ";
				obj[4] = StringUtils.isNotNull(payLists.get(i).getSid())?payLists.get(i).getSid():" ";
				obj[5] = StringUtils.isNotNull(payLists.get(i).getPid())?payLists.get(i).getPid():" ";
				obj[6] = StringUtils.isNotNull(String.valueOf(payLists.get(i).getPayPrice()))?String.valueOf(payLists.get(i).getPayPrice()):" ";
				obj[7] = StringUtils.isNotNull(payLists.get(i).getProIdx())?payLists.get(i).getProIdx():" ";
				obj[8] = StringUtils.isNotNull(payLists.get(i).getPlatform())?payLists.get(i).getPlatform():" ";
				obj[9] = StringUtils.isNotNull(payLists.get(i).getChannel())?payLists.get(i).getChannel():" ";
				obj[10] = StringUtils.isNotNull(payLists.get(i).getSource())?payLists.get(i).getSource():" ";
				obj[11] = StringUtils.isNotNull(payLists.get(i).getStatus())?payLists.get(i).getStatus():" ";
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

	@Override
	@DataSwitch(dataSource="dataSourceLoginPay")
	public InputStream exportOneMonth(PayList payList) throws Exception {
		String[] title=new String[]{"订单号","第三方订单号","创建时间","玩家ID","区服ID","角色ID","支付金额","档位","平台","渠道","资源","交易状态"};
		List<PayList> payLists = new ArrayList<PayList>();
		if(StringUtils.isNotNull(payList.getMultServerId()) && payList.getSid() == null){
			payLists = payListMapper.getPayListMultServerId(payList);
		}else{
			payLists = payListMapper.getPayList(payList);
		}
		List<Object[]>  dataList = new ArrayList<Object[]>();
		Object[] obj = null;
		try{
			for(int i=0;i<payLists.size();i++){
				obj=new Object[12];
				obj[0] = StringUtils.isNotNull(payLists.get(i).getOrderId())?payLists.get(i).getOrderId():" ";
				obj[1] = StringUtils.isNotNull(payLists.get(i).getThirdTradeNo())?payLists.get(i).getThirdTradeNo():" ";
				obj[2] = TimeUtils.stampToDateWithMill(String.valueOf(StringUtils.isNotNull(payLists.get(i).getCreateTime())?payLists.get(i).getCreateTime():" "));
				obj[3] = StringUtils.isNotNull(payLists.get(i).getUserId())?payLists.get(i).getUserId():" ";
				obj[4] = StringUtils.isNotNull(payLists.get(i).getSid())?payLists.get(i).getSid():" ";
				obj[5] = StringUtils.isNotNull(payLists.get(i).getPid())?payLists.get(i).getPid():" ";
				obj[6] = StringUtils.isNotNull(String.valueOf(payLists.get(i).getPayPrice()))?String.valueOf(payLists.get(i).getPayPrice()):" ";
				obj[7] = StringUtils.isNotNull(payLists.get(i).getProIdx())?payLists.get(i).getProIdx():" ";
				obj[8] = StringUtils.isNotNull(payLists.get(i).getPlatform())?payLists.get(i).getPlatform():" ";
				obj[9] = StringUtils.isNotNull(payLists.get(i).getChannel())?payLists.get(i).getChannel():" ";
				obj[10] = StringUtils.isNotNull(payLists.get(i).getSource())?payLists.get(i).getSource():" ";
				obj[11] = StringUtils.isNotNull(payLists.get(i).getStatus())?payLists.get(i).getStatus():" ";
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
