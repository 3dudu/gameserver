package com.icegame.sysmanage.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Console;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.gm.entity.ExportTable;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.JActivityGiftKey;
import com.icegame.sysmanage.entity.PayList;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.service.IJActivityGiftKeyService;
import com.icegame.sysmanage.service.IPayListService;
import com.icegame.sysmanage.service.IServerListService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sysmgr/paylist")
public class PayListController {

	private static Logger logger = Logger.getLogger(PayListController.class);

	@Autowired
	private IPayListService payListService;


	@Autowired
	private GroupUtils groupUtils;

	@Autowired
	private IServerListService serverListService;

	@RequestMapping("gotoPayList")
	public String gotoActivityGiftKey() {
		return "sysmanage/paylist/paylist";
	}

	@RequestMapping("getPayList")
	@ResponseBody
	public String getActivityGiftKeyList(String orderId,String thirdTradeNo,Integer proIdx,Float payPrice,Integer sid,Long userId,Integer pid,Integer status,String platform,String channel,String source,String startTime,String passTime,int pageNo, int pageSize) {

		startTime = TimeUtils.checkDateDetail(startTime,passTime).get("startDate");
		passTime = TimeUtils.checkDateDetail(startTime,passTime).get("endDate");

		startTime = TimeUtils.dateToStampWithDetail(startTime);
		passTime = TimeUtils.dateToStampWithDetail(passTime);

		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);

		PageInfo<PayList> pageInfo = null;
		// 获取分页数据总和
		List<PayList> payLists = new ArrayList<PayList>();
		PayList payList = new PayList(orderId,thirdTradeNo,proIdx,payPrice,userId,sid,pid,status,platform,channel,source,startTime,passTime);


		String hasChannel = groupUtils.getGroupHasChannel();

		List<ServerList> allServerList = null;

		if(StringUtils.isNotNull(hasChannel)){

			StringBuffer sb = new StringBuffer();

			hasChannel = StringUtils.multFormat(hasChannel);

			ServerList serverList = new ServerList();

			serverList.setHasChannel(hasChannel);

			allServerList = serverListService.getAllChannelServerListNoPage(serverList);

			if(allServerList.size() > 0){
				sb.append("(");
				for(int i = 0 ; i < allServerList.size() ; i++){
					sb.append("'").append(allServerList.get(i).getServerId()).append("'").append(",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(")");
			}
			payList.setMultServerId(sb.toString());

		}

		pageInfo = this.payListService.getPayList(payList,pageParam);

		payLists = pageInfo.getList();

		JSONArray jsonArr = JSONArray.fromObject(payLists);
		// 获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}


	@RequestMapping("/exportTable")
	public void exportTable(HttpServletRequest request, HttpServletResponse response, int flag, String playerIds) throws Exception {
		ExportTable et = new ExportTable();
		et.setIds(playerIds);et.setFlag(flag);
		InputStream is=payListService.getInputStreamRetention(et);
		response.setContentType("application/octet-stream;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename=paylist.xls");
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(is,output);
	}

	/**
	 * 导出一个月的充值订单
	 * @param request
	 * @param response
	 * @param orderId
	 * @param thirdTradeNo
	 * @param channel
	 * @param platform
	 * @param userId
	 * @param pid
	 * @param payPrice
	 * @param source
	 * @param proIdx
	 * @param status
	 * @param sid
	 * @param startTime
	 * @param passTime
	 * @throws Exception
	 */
	@RequestMapping("/exportTableOneMonth")
	public void exportTableOneMonth(HttpServletRequest request, HttpServletResponse response, String orderId,
									String thirdTradeNo, String channel, String platform, Long userId, Integer pid, Float payPrice,
									String source, Integer proIdx, Integer status, Integer sid, String startTime, String passTime) throws Exception {

		startTime = TimeUtils.dateToStampWithDetail(startTime);
		passTime = TimeUtils.dateToStampWithDetail(passTime);

		PayList payList = new PayList(orderId, thirdTradeNo, proIdx, payPrice, userId, sid, pid,status, platform, channel, source, startTime, passTime);

		Long curStartTime = Long.valueOf(startTime);
		Long curPassTime = Long.valueOf(passTime);

		Long montyMills = 7L * 86400 * 1000;

		// 这边对时间做二次验证，如果时间大于7天，就不做导出操作
		if(curPassTime - curStartTime >  montyMills){
			logger.info("startTime:" + startTime + ",passTime:" + passTime + ", 差为" + (curPassTime - curStartTime ) + ",时间差不符合规范，操作无效...");
			return;
		}

		String hasChannel = groupUtils.getGroupHasChannel();

		List<ServerList> allServerList = null;

		if(StringUtils.isNotNull(hasChannel)){

			StringBuffer sb = new StringBuffer();

			hasChannel = StringUtils.multFormat(hasChannel);

			ServerList serverList = new ServerList();

			serverList.setHasChannel(hasChannel);

			allServerList = serverListService.getAllChannelServerListNoPage(serverList);

			sb.append("(");
			for(int i = 0 ; i < allServerList.size() ; i++){
				sb.append("'").append(allServerList.get(i).getServerId()).append("'").append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			payList.setMultServerId(sb.toString());

		}


		InputStream is=payListService.exportOneMonth(payList);
		response.setContentType("application/octet-stream;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename=paylist.xls");
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(is,output);
	}

}
