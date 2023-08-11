package com.xuegao.LoginServer.handler;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.GlobalCache;
import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.LoginServer.po.PfOptionPo;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;

import io.netty.util.CharsetUtil;

public class OpenHandler {
	static Logger logger=Logger.getLogger(OpenHandler.class);
	
	/**由master 发来数据
	 * 同步数据 <pf_option> 到 网关本地数据库
	 * */
	@Cmd("/api/open/login/sycnData")
	public  void notifySycnData(User sender, HttpRequestJSONObject params){
//		String json=null;
//		if(!params.isEmpty()){
//			json=params.toJSONString();
//		}else{
//			json=params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
//		}
//		logger.debug("loginsycnData:"+json);
//		JSONObject  jsonObj= JSON.parseObject(json);
//		String  tableName=  jsonObj.getString("table");
//		String cmd= jsonObj.getString("operCmd");
//		String id=jsonObj.getString("id");
//		String record=jsonObj.getString("value");
//		int rsCnt=0;
//		if("pf_options".equals(tableName)){
//			PfOptionPo pfnotifyData = JSONObject.toJavaObject(JSON.parseObject(record), PfOptionPo.class);
//			if("insert".equals(cmd)){
//				rsCnt=pfnotifyData.insertToDB();
//			}else if("update".equals(cmd)){
//				rsCnt=pfnotifyData.updateToDB();
//			}else if("delete".equals(cmd)){
//				rsCnt=pfnotifyData.deleteFromDB();
//			}else{
//				//返回
//				JSONObject rs = MsgFactory.getDefaultErrorMsg("不支持操作");
//				sender.sendAndDisconnect(rs);
//				return;
//			}
//		}else{
//			//返回
//			JSONObject rs = MsgFactory.getDefaultErrorMsg("表名不存在");
//			sender.sendAndDisconnect(rs);
//			return;
//		}
		int rsCnt=1;
		if(rsCnt>0){
			//返回
			JSONObject rs = MsgFactory.getDefaultSuccessMsg();
			rs.put("msg", "success");
			sender.sendAndDisconnect(rs);
			return;
		}else{
			//返回
			JSONObject rs = MsgFactory.getDefaultErrorMsg("执行结果个数为0条");
			sender.sendAndDisconnect(rs);
			return;
		}
	}
	
	
	/**通知回调  数据进行同步
	 * serviceName 业务名称
	 * get  方式
	 * */
	@Cmd("/api/open/login/reloadData")
	public void reloadData(User sender, JSONObject params){
//		String  serviceName = params.getString("serviceName");
//		logger.info("serviceName:"+serviceName);
//		if("pf_options".equals(serviceName)){
//			GlobalCache.reload();
//		}else{
//			//返回错误
//			JSONObject rs = MsgFactory.getDefaultErrorMsg("不支持操作");
//			sender.sendAndDisconnect(rs);
//			return;
//		}
		//返回
		JSONObject rs = MsgFactory.getDefaultSuccessMsg();
		rs.put("msg", "success");
		sender.sendAndDisconnect(rs);
	}
}
