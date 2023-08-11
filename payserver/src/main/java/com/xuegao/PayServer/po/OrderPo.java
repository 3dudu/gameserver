package com.xuegao.PayServer.po;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.PoInfoVo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;
import com.xuegao.core.util.DateUtil;

@Table(tableName="pay_order",proxoolAlias="proxool.PayServer")
public class OrderPo  extends BasePo {

	@Column
	private String   order_id;// 'UUID+serverId+playerId',
	@Column
	private Float pay_price;//'订单金额<回调写入>(该值验证后写入)',
	@Column
	private String   units;// '货比单位',<回调写入>美元,人民币,日元...
	@Column
	private Integer   pro_idx;// '档位',
	@Column
	private Integer   sid;// '服务器ID',
	@Column
	private Integer   pid;//'角色ID',
	@Column
	private Integer   status;// '状态码',0.未收到回调 1.收到回调验证失败2.验证成功3.请求slave增加元宝数量失败 或网络不通4.请求成功 且 元宝增加成功
	@Column
	private String   platform;// 平台
	@Column
	private String   channel;// 渠道
	@Column
	private String   source;//'AnySdk 使用字段  保存 返回 source内容',eyou网页充值存储元宝数
	@Column
	private String  third_trade_no;// 第三方订单号; 对于anysdk接入的是 就是 真实支付方服务器订单Id, 对于非anySDk 就是 支付平台订单号
	@Column
	private Long  create_time;// 订单创建时间
	@Column
	private Long  finish_time;// 订单创建时间
	@Column
	private Long user_id;
	@Column
	private String order_type; //0:普通用户，1：内部充值，2：沙箱充值 3：GM代充标识
	@Column
	private String channelCode;
	@Column
	private String ext;// 透传参数
	@Column
	public int bonus; //返利
	@Override
	public String toString() {
		return "OrderPo{" +
				"order_id='" + order_id + '\'' +
				", pay_price=" + pay_price +
				", units='" + units + '\'' +
				", pro_idx=" + pro_idx +
				", sid=" + sid +
				", pid=" + pid +
				", status=" + status +
				", platform='" + platform + '\'' +
				", channel='" + channel + '\'' +
				", source='" + source + '\'' +
				", third_trade_no='" + third_trade_no + '\'' +
				", create_time=" + create_time +
				", finish_time=" + finish_time +
				", user_id=" + user_id +
				", channelCode='" + channelCode + '\'' +
				", order_type='" + order_type + '\'' +
				", ext='" + ext + '\'' +
				'}';
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public Float getPay_price() {
		return pay_price;
	}

	public void setPay_price(Float pay_price) {
		this.pay_price = pay_price;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Integer getPro_idx() {
		return pro_idx;
	}

	public void setPro_idx(Integer pro_idx) {
		this.pro_idx = pro_idx;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getThird_trade_no() {
		return third_trade_no;
	}

	public void setThird_trade_no(String third_trade_no) {
		this.third_trade_no = third_trade_no;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Long getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Long finish_time) {
		this.finish_time = finish_time;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public static OrderPo findByOrdId(String order_id) {
		long start = System.currentTimeMillis();
		DBWrapper dbWrapper = BasePo.fetchDBWrapper(OrderPo.class);
		OrderPo ordPo = JSON.toJavaObject(dbWrapper.queryForBean("select * from pay_order where order_id=?", order_id),
				OrderPo.class);
		logger.info("findByOrdId:queryCost:"+(System.currentTimeMillis()-start));
		return ordPo;
	}

    public static OrderPo findByThirdId(String third_trade_no) {
        long start = System.currentTimeMillis();
        DBWrapper dbWrapper = BasePo.fetchDBWrapper(OrderPo.class);
        OrderPo ordPo = JSON.toJavaObject(dbWrapper.queryForBean("select * from pay_order where third_trade_no=?", third_trade_no),
                OrderPo.class);
        logger.info("findByThirdId:queryCost:"+(System.currentTimeMillis()-start));
        return ordPo;
    }

    /**
     * mycard 差异对比 报文 拼接
     * */
    public String MyCardTradeNoCompare(long start_time,long end_time)
    {
//		Class  clazz = OrderPo.class;
        PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderPo.class);
        String sql="select * from `"+poInfoVo.getTableName()+"` where platform=? and create_time> ? and  create_time< ? and status =4";
        JSONArray array=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForList(sql,"MyCardSDK",start_time,end_time);
        List<OrderPo> list=new ArrayList<OrderPo>();
        for(int i=0;i<array.size();i++){
            JSONObject object=array.getJSONObject(i);
            BasePo basePo=JSON.toJavaObject(object, OrderPo.class);
            list.add((OrderPo) basePo);
        }
        Iterator<OrderPo> iterator = list.iterator();
        StringBuffer info=new StringBuffer();
        while(iterator.hasNext())
        {
            OrderPo payTrade=iterator.next();
//			Map<String,String> data=HelperJson.decodeMap(payTrade.getSource());
//			SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime=dateFormater.format(payTrade.getCreateTime()/1000);
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(payTrade.getCreate_time()));
            String[] source = payTrade.getSource().split("\\|");
            logger.info("MyCardTradeNoCompare-source:"+ Arrays.toString(source));
            createTime=createTime.replace(' ', 'T');
            String trade =new StringBuffer().append(source[2]).append(",")
                    .append(source[0]).append(",")
                    .append(payTrade.getThird_trade_no()).append(",")
                    .append(payTrade.getOrder_id()).append(",")
                    .append(payTrade.getPid()).append(",")
                    .append(payTrade.getPay_price()).append(",")
                    .append("TWD").append(",")
                    .append(createTime)
                    .toString();
            info.append(trade).append("<BR>");
        }
        return info.toString();
    }
    public String MyCardTradeNo(String tradeNo)
    {
//		Class  clazz = OrderPo.class;
        PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderPo.class);
        String sql="select * from `"+poInfoVo.getTableName()+"` where platform=? and third_trade_no= ? and status = 4";
        JSONArray array=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForList(sql,"MyCardSDK",tradeNo);
        List<OrderPo> list=new ArrayList<>();
        for(int i=0;i<array.size();i++){
            JSONObject object=array.getJSONObject(i);
            BasePo basePo=JSON.toJavaObject(object, OrderPo.class);
            list.add((OrderPo) basePo);
        }
        Iterator<OrderPo> iterator = list.iterator();
        String trade="";
        while(iterator.hasNext())
        {
            OrderPo payTrade=iterator.next();
//			Map<String,String> data=HelperJson.decodeMap(payTrade.getSource());
//			SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime=dateFormater.format(payTrade.getCreateTime()/1000);
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(payTrade.getCreate_time()));
            String[] source = payTrade.getSource().split("\\|");
            logger.info("MyCardTradeNo-source:"+ Arrays.toString(source));
            createTime=createTime.replace(' ', 'T');
            trade =new StringBuffer().append(source[2]).append(",")
                    .append(source[0]).append(",")
                    .append(payTrade.getThird_trade_no()).append(",")
                    .append(payTrade.getOrder_id()).append(",")
                    .append(payTrade.getPid()).append(",")
                    .append(payTrade.getPay_price()).append(",")
                    .append("TWD").append(",")
                    .append(createTime).append("<BR>")
                    .toString();
        }
        return trade;
    }


	/**
	 * ltv和新增付费和活跃付费统计
	 * @param
	 *
	 * */
//	public  void  payFlowStatitic(){
//
//		Long user_id = this.getUser_id();
//
//		UserPo userPo =  UserPo.findByUserId(user_id);
//		if(null==userPo){
//			logger.info("user_id:"+user_id+",不存在");
//			return;
//		}
//
//		String platform = userPo.getPf();
//		int channel = userPo.getChannel();
//		float amount =  this.getPay_price();
//		int  regDay =  DateUtil.diffDays(userPo.getCreate_time(), new Date())+1;
//		int paramRegDayNum =0;
//		if(7>=regDay){
//			paramRegDayNum = regDay;
//		}else if(regDay>=8 && regDay<15){
//			paramRegDayNum=14;
//		}
//		else if(regDay>=16 && regDay<22){
//			paramRegDayNum=21;
//		}
//		else if(regDay>=22 && regDay<31){
//			paramRegDayNum=30;
//		}
//		else if(regDay>=31 && regDay<61){
//			paramRegDayNum=60;
//		}
//		else if(regDay>=61 && regDay<181){
//			paramRegDayNum=180;
//		}
//		else if(regDay>=181 && regDay<361){
//			paramRegDayNum=360;
//		}else
//		{
//			paramRegDayNum=361;
//		}
//
//		String currDate = DateUtil.format(new Date());
//
//		// 查询 今天[对应的channle和平台]记录是否存在
//
//		Pay_Statis_Ltv ltv = Pay_Statis_Ltv.findRecordByDateAndPaltformAndChannel(currDate, platform, channel);
//		Long ltvPriId = 0L;
//		if(null==ltv){// 插入一条数据
//			ltv =  new Pay_Statis_Ltv();
//			ltv.setChannel(channel);
//			ltv.setCreateDate(currDate);
//			ltv.setPlatform(platform);
//			ltvPriId = ltv.insertToDBAndGetId();//插入成功之后 返回 主键Id
//			Pay_Statis_Ltv.updatePayStatisticsById(paramRegDayNum, amount, ltvPriId);
//		}else{
//			ltvPriId = ltv.getId();
//			float updateNum = Pay_Statis_Ltv.findAmountTotalByRegDay(ltv,paramRegDayNum) + amount;
//			Pay_Statis_Ltv.updatePayStatisticsById(paramRegDayNum, updateNum, ltvPriId);
//		}
//
//
//		// 新增付费和 活跃付费
//		Pay_New_Active new_active = 	Pay_New_Active.findRecordByDPC(currDate, platform, channel);
//		Long prikeyId = 0L;//
//		if(null==new_active){// 新增一条记录
//			new_active =  new Pay_New_Active();
//			new_active.setChannel(channel);
//			new_active.setCreateDate(currDate);
//			new_active.setPlatform(platform);
//			prikeyId = new_active.insertToDBAndGetId();
//
//			float amountTotle = amount;
//			if(regDay==1){// 新增
//			}else{// 活跃
//				regDay = 2;
//			}
//			Pay_New_Active.updatePayStatisticsById(regDay, amountTotle, prikeyId);
//		}else{// 更新
//			prikeyId = new_active.getId();
//			float amountTotle = 0.0f;
//			if(regDay==1){// 新增
//				amountTotle = new_active.getNewcount()+amount;
//			}else{// 活跃
//				regDay = 2;
//				amountTotle = new_active.getActcount()+amount;
//			}
//			Pay_New_Active.updatePayStatisticsById(regDay, amountTotle, prikeyId);
//		}
//	}

}
