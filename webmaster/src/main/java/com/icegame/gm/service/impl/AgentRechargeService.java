package com.icegame.gm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.*;
import com.icegame.gm.entity.AgentRecharge;
import com.icegame.gm.entity.Gmlogs;
import com.icegame.gm.mapper.AgentRechargeMapper;
import com.icegame.gm.service.IAgentRechargeService;
import com.icegame.gm.service.IGmlogsService;
import com.icegame.gm.util.RSAUtils;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.icegame.framework.utils.StringUtils;

@Service
public class AgentRechargeService implements IAgentRechargeService {

	private static Logger logger = Logger.getLogger(AgentRechargeService.class);
	//Rsa公钥
	private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApSUc4ibbTN4JPgkp/WEIIUGT5alHaq" +
			"VH9FSeerjdBkGmimJSjyVVNI4kDma97PVT++i11I7F9DUP+7pRuMRmoel4OOL6x3j3nYe0K6lcGNh0g48NDFyrUUWfQxfyMJgV7bLdIat5" +
			"AXL21HHRFIZgoZTJLGfhIdaDqTLTue2SqeGXmqXYNoq/0cgdrvvTJWiy3afIDbXlfjC537rRNeTFVNaviI/ItYcy1eWBQs7XZgQmPk2" +
			"B67q3pf/p7ykqCBw77FjtNcX8Sb3TkyfnLPpAMyUelFbKGW09G6cT750UZvOlMUaw1Saabr3AtFAqvecg/JwjpCxi7/OpLbc2VYT1RQIDAQAB";
	//Rsa私钥
	private static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQClJRziJttM3gk+CSn9YQgh" +
			"QZPlqUdqpUf0VJ56uN0GQaaKYlKPJVU0jiQOZr3s9VP76LXUjsX0NQ/7ulG4xGah6Xg44vrHePedh7QrqVwY2HSDjw0MXKtRRZ9DF/Iw" +
			"mBXtst0hq3kBcvbUcdEUhmChlMksZ+Eh1oOpMtO57ZKp4Zeapdg2ir/RyB2u+9MlaLLdp8gNteV+MLnfutE15MVU1q+Ij8i1hzLV5Y" +
			"FCztdmBCY+TYHrurel/+nvKSoIHDvsWO01xfxJvdOTJ+cs+kAzJR6UVsoZbT0bpxPvnRRm86UxRrDVJppuvcC0UCq95yD8nCOkLGLv86" +
			"kttzZVhPVFAgMBAAECggEAbhVNKZtJN/YSJx4otVQG/VQfaEns5zQBwObfNWMhQlhk0X41FmKGZ6AQfOET3W6zawp2mpgJcH4mh2Btt" +
			"UKGP4vHrfPvwyPpu0KIYUplr9Ip6MBkrEbhlC1aunit4qKei3JdYWJSKRsfWgH8ozfoFg1+BHHCarH51cGhzSCGUWKPn6Y2eDnJwdWxwG3" +
			"VNZQEGi3pRkTx9kj/V47X8NH/jeBfRxXm9vHaQbSY2QmKSh7aCqKtF502KHlSPA/u4Mih5Vh1IhORkZ0KHVL9dD1E3xJzcnNk7BiyHwDRuo" +
			"TLup+RMbYqNTRKrPiMbDPgm9BoMgav3QenGo9Z6IJhhho6QQKBgQDy9IG1NHDMLoVSimV5xT/ivds427fMGMB8/2ZhmnphfJ+SIr3Q+Ut" +
			"5RMfyyIvFeNboDO25yVSigvhAxtozESWG1CYCH5uD8Nes2lSKAL/5V31eu8RzojrSgVlyo5212R5T3XSxSH+s05Dbj5BtyoztIgQOq0q4S" +
			"gQd38qgGeUyaQKBgQCuAxTsafNnm/9q68VrNIfIM0oSck+HRpj0jITdru4D4cd8nTy2lC5nZogcj7IS+LbYtl5XgOI132WXqcFAredxqGN" +
			"UsUCofHE17rIuGRirReOrtKsYFUFPrwTJNRT0xc/ndAHW+n0upQ9UPqj/HJ/Ovi37PJo+fqjJqtaxlaWYfQKBgQDcSiTdx5nLGRdb2w7dl" +
			"aMylVETwe1qSrsl23HaZ/Y1NIl/OK8Brzjm0R23Hm3VdJbvuuFGRq3N2JD+Mw+fpBlxoiSAYmZhANyd5y0mID3w+Io9fmVHL77EJfKTxpT" +
			"2UNJ12mO3Z3QUoZRD8G1Vj4WucdxZ7KiIZtxKtiMEfdZamQKBgHE84CjY6eTKx3Q06cvR62qEtfc7HDXT40WBDBWW+JzeGIsnZ5MI6wmu1" +
			"3R+rktaPuLYCpy26n5UWjBP78q/YJW+FqXOk10RXjrSknEdM8iBOp9Keuy5KD9KjbrCKFkBQUJFY80aRMxN7aPNAvzBC93mNNYBof55Pi2" +
			"+VuhJkfGhAoGBANuHAgY6sWzhD/g93bSQZFtpfGtBpWzsmGOFFmN95v4I+LioPmfaV/3eKbmMekp9P0IWB9gT1bMeXJX7CkFB5Oa+W4hiS" +
			"3pk0WgDxxroDNM9TQOFH78tnYEeWkUG7/XdRQQA0u+Qdtq1jOjMVrC0wALE6lFoRt2iJVpPjhBlePqk";
	private static final String CREATE_ORDER_API = "/p/buy";
	private static final String FINISH_ORDER_API = "/p/GMPayNotify/cb";

	enum Status{

		SUCCESS(0,"成功"),
		NODE_NOT_FIND(1, "找不到节点"),
		PAY_NODE_NOT_FIND(2, "找不到支付节点"),
		HTTP_POST_EXCEPTION(3, "请求异常"),
		PAY_RETURN_NULL(4, "支付返回内容为空"),
		NOT_GENERIC_ORDER_ID(5, "未生成订单编号"),
		FAIL(6, "失败"),
		PARAMS_ERROR(7, "参数错误"),
		PARAMS_ENCRYPT_FAIL(8,"参数加密失败"),
		PAY_RETURN_ERROR(9,"支付返回的参数错误"),
		AGENT_PAY_PORT_NULL(10,"代理支付端口未配置"),
		ORDER_IS_FINISH(11,"此订单已完成，不允许重复充值"),
		ORDER_IS_NOT_VALID(12,"此订单已失效，请重新创建订单"),
		IS_NOT_CURRENT_USE_CREATE(13,"非当前用户创建的订单，请勿操作！"),
		;

		Integer code;
		String msg;
		Status(Integer code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}

	@Autowired
	private AgentRechargeMapper agentRechargeMapper;

	@Autowired
	private IServerNodesService serverNodesService;

	@Autowired
	private IPropertiesService propertiesService;

	@Autowired
	private GroupUtils groupUtils;

	@Autowired
	private IServerListService serverListService;

	@Autowired
	private IGmlogsService gmlogsService;


	/**
	 * 据条件查询记录
	 * @param agentRecharge
	 * @param startTime
	 * @param passTime
	 * @param pageParam
	 * @return
	 */
	@Override
	public PageInfo<AgentRecharge> getAgentRechargeList(AgentRecharge agentRecharge, Long startTime,
														Long passTime, PageParam pageParam) {

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
			agentRecharge.setMultServerId(sb.toString());

		}
		List<AgentRecharge> lists;
		if(StringUtils.isNotNull(agentRecharge.getMultServerId()) && agentRecharge.getServerId() == null){
			PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
			lists = agentRechargeMapper.getAgentRechargeByConditionsAndMutServer(agentRecharge, startTime, passTime);
		}else{
			PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
			lists = agentRechargeMapper.getAgentRechargeByConditions(agentRecharge, startTime, passTime);
		}

		return new PageInfo<>(lists);
	}

//	@Override
//	public AgentRecharge getAgentRechargeById(Integer id) {
//		return agentRechargeMapper.getAgentRechargeById(id);
//	}

	@Override
	public Map<String, Object> saveAgentRecharge(AgentRecharge agentRecharge) {


		Map<String, Object> result = new HashMap<>();

		//获取GM登录名
		User currrentUser = UserUtils.getCurrrentUser();
		String loginName = currrentUser == null ? "Unknown_User" : currrentUser.getLoginName();

		//向支付发请求 => 创建订单返回订单编号 http://159.138.33.74:9030/p/buy
		Map<String,Object> syncData = new HashMap<>();
		//1.获取所有的节点列表
		List<ServerNodes> serverNodesList = serverNodesService.getServerNodesSync();
		if (serverNodesList == null || serverNodesList.size() < 1){
			logger.info("【代充-新增】 未找不到节点！操作者: " + loginName);
			result.put("code", Status.NODE_NOT_FIND.getCode());
			result.put("msg", Status.NODE_NOT_FIND.getMsg());
			return result;
		}

		syncData.put("s", agentRecharge.getServerId());
		syncData.put("p", agentRecharge.getPlayerId());
		syncData.put("idx", agentRecharge.getProIdx());
		syncData.put("c", agentRecharge.getExt());
		syncData.put("money", agentRecharge.getPayPrice());
		syncData.put("u", agentRecharge.getUserId());
		syncData.put("wc", agentRecharge.getPlatform());
		syncData.put("aTest", agentRecharge.getOrderType());
		syncData.put("chl", agentRecharge.getChannel());
		syncData.put("channelCode", agentRecharge.getChannelCode());

		JSONObject jsonData = JSONObject.fromObject(syncData);
		//筛选出支付服节点列表
		List<ServerNodes> payNodesList = new ArrayList<ServerNodes>();
		for(int i = 0 ; i < serverNodesList.size() ; i++ ){
			if(serverNodesList.get(i).getDiff() == 2){
				payNodesList.add(serverNodesList.get(i));
			}
		}
		if (payNodesList.size() < 1){
			logger.info("【代充-新增】 找不到支付节点！操作者: " + loginName);
			result.put("code", Status.PAY_NODE_NOT_FIND.getCode());
			result.put("msg", Status.PAY_NODE_NOT_FIND.getMsg());
			return result;
		}

		ServerNodes sn = payNodesList.get(0);
		String syncHost = sn.getProtocol() +
				          sn.getIp() +
						  ":" +
						  sn.getPort() +
				  	 	  CREATE_ORDER_API;
		logger.info("向支付服[" + syncHost + "]请求创建订单,参数：" + StringUtils.formatMapToString(syncData));
		String payResult;
		try {
			payResult =  RequestUtil.request3TimesWithPost(syncHost, StringUtils.formatMapToString(syncData).getBytes());
		}catch (Exception e){
			logger.info("向支付服发送创建订单请求异常！", e);
			result.put("code", Status.HTTP_POST_EXCEPTION.getCode());
			result.put("msg", Status.HTTP_POST_EXCEPTION.getMsg());
			return result;
		}
		logger.info("来自支付服[" + syncHost + "]的返回内容：" + payResult);
		Object oResult = JSON.parse(payResult);
		JSONArray jsonArray = JSONArray.fromObject(oResult);
		JSONObject o = jsonArray.getJSONObject(0);
		if (o == null){
			logger.info("【代充-新增】 支付返回内容为空！操作者: " + loginName);
			result.put("code", Status.PAY_RETURN_NULL.getCode());
			result.put("msg", Status.PAY_RETURN_NULL.getMsg());
			return result;
		}
		JSONObject data = null;
		if ("0".equals(o.getString("ret"))){
			data = JSONObject.fromObject(o.get("data"));
		}else if ("-1".equals(o.getString("ret"))){
			logger.info("【代充-新增】 支付返回的参数错误！操作者: " + loginName);
			result.put("code", Status.PAY_RETURN_ERROR.getCode());
			result.put("msg", o.getString("msg"));
			return result;
		}else {
			logger.info("【代充-新增】 支付返回的参数类型未定义！操作者: " + loginName);
			result.put("code", Status.PAY_RETURN_ERROR.getCode());
			result.put("msg", o.getString("msg"));
			return result;
		}

		if (StrUtil.isEmpty(data.getString("order_id"))){
			logger.info("【代充-新增】 未生成订单编号！操作者: " + loginName);
			result.put("code", Status.NOT_GENERIC_ORDER_ID.getCode());
			result.put("msg", Status.NOT_GENERIC_ORDER_ID.getMsg());
			return result;
		}

		agentRecharge.setCreateTime(System.currentTimeMillis());
		agentRecharge.setOrderId(data.getString("order_id"));
		agentRecharge.setLoginName(loginName);
		agentRecharge.setStatus(0);
		boolean flag = agentRechargeMapper.addAgentRecharge(agentRecharge);
		if (flag){
			Gmlogs gmlogs = UserUtils.addGmlogs("代充-新增",
					UserUtils.getCurrrentUserName() + "向支付服["+sn.getIp()+"]创建订单号: "+agentRecharge.getOrderId()+" 的订单，" +
							"数据为" + JSONObject.fromObject(agentRecharge).toString());
			gmlogsService.addGmlogs(gmlogs);
			result.put("code", Status.SUCCESS.getCode());
			result.put("msg", Status.SUCCESS.getMsg());
		}else {
			result.put("code", Status.FAIL.getCode());
			result.put("msg", Status.FAIL.getMsg());
		}
		return result;
	}

	@Override
	public Map<String, Object> syncAgentRechargeToPay(Long id) {


		Map<String, Object> result = new HashMap<>();

		//获取GM登录名
		User currrentUser = UserUtils.getCurrrentUser();
		String loginName = currrentUser == null ? "Unknown_User" : currrentUser.getLoginName();

		if (id == null){
			logger.info("【代充-充值】 参数错误！操作者: " + loginName);
			result.put("code", Status.PARAMS_ERROR.getCode());
			result.put("msg", Status.PARAMS_ERROR.getMsg());
			return result;
		}

		//1.获取所有的节点列表
		List<ServerNodes> nodesList = serverNodesService.getServerNodesSync();
		if (nodesList == null || nodesList.size() < 1){
			logger.info("【代充-充值】 找不到节点！操作者: " + loginName);
			result.put("code", Status.NODE_NOT_FIND.getCode());
			result.put("msg", Status.NODE_NOT_FIND.getMsg());
			return result;
		}
		//筛选出支付服节点列表
		List<ServerNodes> payNodesList = new ArrayList<>();
		for (ServerNodes serverNodes : nodesList) {
			if (serverNodes.getDiff() == 2) {
				payNodesList.add(serverNodes);
			}
		}
		ServerNodes sn = payNodesList.get(0);

		//据id查找代理订单
		AgentRecharge agentRecharge = agentRechargeMapper.getAgentRechargeById(id);
		if (agentRecharge == null || StrUtil.isEmpty(agentRecharge.getOrderId())){
			logger.info("【代充-充值】 未找到代理订单或者代理订单id为空！操作者: " + loginName);
			result.put("code", Status.PARAMS_ERROR.getCode());
			result.put("msg", Status.PARAMS_ERROR.getMsg());
			return result;
		}
		if (!loginName.equals(agentRecharge.getLoginName())){
			logger.info("【代充-充值】 非当前用户创建的订单！ 操作者: " + loginName );
			result.put("code", Status.IS_NOT_CURRENT_USE_CREATE.getCode());
			result.put("msg", Status.IS_NOT_CURRENT_USE_CREATE.getMsg());
			return result;
		}
		if (agentRecharge.getFinishTime() != null && agentRecharge.getFinishTime() != 0
				&& agentRecharge.getStatus() == 1){
			logger.info("【代充-充值】 此订单已完成！ 操作者: " + loginName );
			result.put("code", Status.ORDER_IS_FINISH.getCode());
			result.put("msg", Status.ORDER_IS_FINISH.getMsg());
			return result;
		}else if(agentRecharge.getFinishTime() != null && agentRecharge.getFinishTime() != 0
				&& agentRecharge.getStatus() == -1){
			logger.info("【代充-充值】 此订单已失效！ 操作者: " + loginName );
			result.put("code", Status.ORDER_IS_NOT_VALID.getCode());
			result.put("msg", Status.ORDER_IS_NOT_VALID.getMsg());
			return result;
		}

		//向支付服完成充值 http://159.138.33.74:9031/p/GMPayNotify/cb

		Map<String,Object> syncData = new HashMap<>();

		syncData.put("amount", agentRecharge.getPayPrice().toString());
		syncData.put("role_id", agentRecharge.getUserId().toString());
		syncData.put("server_id", agentRecharge.getServerId().toString());
		syncData.put("order_id", agentRecharge.getOrderId());

		JSONObject jsonData = JSONObject.fromObject(syncData);
		byte[] encryptJsonData = null;
		try {
			encryptJsonData = RSAUtils.encrypt(jsonData.toString().getBytes(StandardCharsets.UTF_8), RSAUtils.getPublicKey(PUBLIC_KEY));
		}catch (Exception e){
			logger.info("获取公钥对象失败");
		}
		if (encryptJsonData == null){
			logger.info("【代充-充值】 参数加密失败！ 操作者: " + loginName );
			result.put("code", Status.PARAMS_ENCRYPT_FAIL.getCode());
			result.put("msg", Status.PARAMS_ENCRYPT_FAIL.getMsg());
			return result;
		}
		String encode = "data=" + cn.hutool.core.codec.Base64.encode(encryptJsonData).replaceAll("[+]", "@");
		String payAgentRechargePort = propertiesService.getPayAgentRechargePort();
		if (payAgentRechargePort == null){
			logger.info("【代充-充值】 代理支付端口未配置！ 操作者: " + loginName );
			result.put("code", Status.AGENT_PAY_PORT_NULL.getCode());
			result.put("msg", Status.AGENT_PAY_PORT_NULL.getMsg());
			return result;
		}
		String syncHost = sn.getProtocol() +
				sn.getIp() +
				":" +
				payAgentRechargePort +
				FINISH_ORDER_API;
		logger.info("向支付服[" + syncHost + "]请求完成订单,参数：" + jsonData.toString());
		String payResult;
		try {
			payResult = RequestUtil.request3TimesWithPost(syncHost, encode.getBytes());
		}catch (Exception e){
			logger.info("向支付服发送完成订单请求异常！", e);
			result.put("code", Status.HTTP_POST_EXCEPTION.getCode());
			result.put("msg", Status.HTTP_POST_EXCEPTION.getMsg());
			return result;
		}
		logger.info("来自支付服[" + syncHost + "]的返回内容：" + payResult);
		if (payResult == null){
			logger.info("【代充-充值】 支付返回内容为空！ 操作者: " + loginName );
			result.put("code", Status.PAY_RETURN_NULL.getCode());
			result.put("msg", Status.PAY_RETURN_NULL.getMsg());
			return result;
		}
		Object oResult = JSON.parse(payResult);
		JSONArray jsonArray = JSONArray.fromObject(oResult);
		JSONObject o = jsonArray.getJSONObject(0);
		int status = -7;
		String msg;
		try {
			status = Integer.parseInt(o.getString("ret"));
			msg = o.getString("msg");
		}catch (Exception e){
			logger.info("支付返回参数错误！", e);
			result.put("code", Status.PAY_RETURN_ERROR.getCode());
			result.put("msg", Status.PAY_RETURN_ERROR.getMsg());
			return result;
		}
		if (status == -7 || msg == null){
			logger.info("【代充-充值】 支付返回的参数错误！ 操作者: " + loginName );
			result.put("code", Status.PAY_RETURN_ERROR.getCode());
			result.put("msg", Status.PAY_RETURN_ERROR.getMsg());
			return result;
		}
		agentRecharge.setFinishTime(System.currentTimeMillis());
		agentRecharge.setStatus(status);

		boolean ret = agentRechargeMapper.updateAgentRecharge(agentRecharge);
		if (ret){
			Gmlogs gmlogs = UserUtils.addGmlogs("代充-充值",
					UserUtils.getCurrrentUserName() + "向支付服["+sn.getIp()+"]完成订单号: "+agentRecharge.getOrderId()+" 的订单的充值，" +
							"数据是: " + JSONObject.fromObject(agentRecharge).toString());
			gmlogsService.addGmlogs(gmlogs);
			result.put("code", Status.SUCCESS.getCode());
			result.put("msg", msg);
		}else {
			result.put("code", Status.FAIL.getCode());
			result.put("msg", msg);
		}

		return result;
	}


	@Override
	public InputStream export(AgentRecharge agentRecharge, Long startTime, Long passTime) throws Exception {
		String[] title=new String[]{"ID","操作人","区服ID","区服名称","角色ID","角色名","充值档位","充值金额","货币类型","订单类型"
				,"渠道","channelCode","创建时间","充值时间","同步状态","订单ID"};
		List<AgentRecharge> allAgentRecharges;
		if(StringUtils.isNotNull(agentRecharge.getMultServerId()) && agentRecharge.getServerId() == null){
			allAgentRecharges = agentRechargeMapper.getAgentRechargeByConditionsAndMutServer(agentRecharge, startTime, passTime);
		}else{
			allAgentRecharges = agentRechargeMapper.getAgentRechargeByConditions(agentRecharge, startTime, passTime);
		}
		List<Object[]>  dataList = new ArrayList<Object[]>();
		Object[] obj = null;
		try{
			for(int i=0;i<allAgentRecharges.size();i++){
				obj=new Object[16];
				obj[0] = String.valueOf(allAgentRecharges.get(i).getId());
				obj[1] = (allAgentRecharges.get(i).getLoginName() != null)?allAgentRecharges.get(i).getLoginName():" ";
				obj[2] = (allAgentRecharges.get(i).getServerId() != null)?allAgentRecharges.get(i).getServerId().toString():" ";
				obj[3] = (allAgentRecharges.get(i).getName() != null)?allAgentRecharges.get(i).getName():" ";
				obj[4] = (allAgentRecharges.get(i).getPlayerId() != null)?allAgentRecharges.get(i).getPlayerId().toString():" ";
				obj[5] = (allAgentRecharges.get(i).getPlayerName() != null)?allAgentRecharges.get(i).getPlayerName():" ";
				obj[6] = (allAgentRecharges.get(i).getProIdx() != null)?allAgentRecharges.get(i).getProIdx().toString():" ";
				obj[7] = (allAgentRecharges.get(i).getPayPrice() != null)? String.valueOf(allAgentRecharges.get(i).getPayPrice()):"0";
				obj[8] = (allAgentRecharges.get(i).getMoneyType() != null)? String.valueOf(allAgentRecharges.get(i).getMoneyType()):"0";
				obj[9] = "GM代充";
				obj[10] = !StrUtil.isEmpty(allAgentRecharges.get(i).getChannel())?allAgentRecharges.get(i).getChannel():" ";
				obj[11] = !StrUtil.isEmpty(allAgentRecharges.get(i).getChannelCode())?allAgentRecharges.get(i).getChannelCode():" ";
				obj[12] = (allAgentRecharges.get(i).getCreateTime() != null)
						?TimeUtils.stampToDateWithMill(allAgentRecharges.get(i).getCreateTime()):" ";
				obj[13] = (allAgentRecharges.get(i).getFinishTime() != null)
						?TimeUtils.stampToDateWithMill(allAgentRecharges.get(i).getFinishTime()):" ";
				switch ((allAgentRecharges.get(i).getStatus() == null) ? 0 : allAgentRecharges.get(i).getStatus()){
					case -1:
						obj[14] = "失败";
						break;
					case 1:
						obj[14] = "成功";
						break;
					default:
						obj[14] = "未充值";
						break;
				}
				obj[15] = !StrUtil.isEmpty(allAgentRecharges.get(i).getOrderId())?allAgentRecharges.get(i).getOrderId():" ";
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
