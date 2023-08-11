package com.xuegao.PayServer.po;

import com.alibaba.fastjson.JSON;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.PoInfoVo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

//@Table(tableName="pay_count",proxoolAlias="proxool.PayServer")
public class Pay_New_Active  extends BasePo {

	@Column
	private String   createDate;// '流水记录日期',
	@Column
	private String  platform;//
	@Column
	private int   channel;// 
	@Column
	private float   newcount;  // 记录日期当天的新增付费
	@Column
	private float   actcount;// 记录日期 不在这一天注册的玩家付费
	
	

	/**
	 * 根据时间和平台,渠道联合查询
	 * findRecordByDateAndPaltformAndChannel 缩写: findRecordByDPC
	 * */
	public static Pay_New_Active findRecordByDPC(String date,String platform,int channel) {
		long start = System.currentTimeMillis();
		DBWrapper dbWrapper = BasePo.fetchDBWrapper(Pay_New_Active.class);
		Pay_New_Active ordPo = JSON.toJavaObject(dbWrapper.queryForBean("select * from pay_new_active where createDate=? and platform=? and channel=?", date,platform,channel),
				Pay_New_Active.class);
		logger.info("findRecordByDPC:"+(System.currentTimeMillis()-start));
		return ordPo;
	}
	
	
	/**
	 * 根据主键 更新 
	 * @param regDay 玩家注册时间 [0 新增, 1 活跃]
	 * @parma 更新数字 
	 * @param 主键
	 * 
	 * 根据注册天数 来决定 update 语句
	 * */
	public  static int updatePayStatisticsById(int regDay,float updateNum,long priKeyId){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderSynchPo.class);
		StringBuffer  updateSql =  new StringBuffer();
		updateSql.append(" update pay_new_active set ");
		switch(regDay){
		case 1:// 表示新增
			updateSql.append("`newcount` = ? ");
			break;
		case 2:// 表示活跃
			updateSql.append("`actcount` = ? ");
			break;
		}
		updateSql.append("  where `id`=? ");
		
		int rs = DBWrapper.getInstance(poInfoVo.getProxoolAlias()).execute(updateSql.toString(),updateNum,priKeyId);
		
		return rs;
	}


	public String getCreateDate() {
		return createDate;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getPlatform() {
		return platform;
	}


	public void setPlatform(String platform) {
		this.platform = platform;
	}


	public int  getChannel() {
		return channel;
	}


	public void setChannel(int channel) {
		this.channel = channel;
	}


	public float getNewcount() {
		return newcount;
	}


	public void setNewcount(float newcount) {
		this.newcount = newcount;
	}


	public float getActcount() {
		return actcount;
	}


	public void setActcount(float actcount) {
		this.actcount = actcount;
	}


	
}
