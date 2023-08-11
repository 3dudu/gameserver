package com.icegame.sysmanage.entity;



public class PayList {

	private Long id;
	private String orderId; // 'UUID+serverId+playerId'
	private Float payPrice; //'订单金额<回调写入>(该值验证后写入)',
	private String units; // '货比单位',<回调写入>美元,人民币,日元...
	private Integer proIdx; // '档位',
	private Integer sid; // '服务器ID',
	private Integer pid; //'角色ID',
	private String playerName;
	private Integer status; // '状态码',0.待支付 1.收到回调验证失败 2.验证成功：支付成功，但是元宝没到账 3.请求slave增加元宝数量失败 或网络不通 4.请求成功 且 元宝增加成功
	private String platform; // 平台
	private String channel;// 渠道
	private String source;//'AnySdk 使用字段  保存 返回 source内容',
	private String thirdTradeNo;// 第三方订单号; 对于anysdk接入的是 就是 真实支付方服务器订单Id, 对于非anySDk 就是 支付平台订单号
	private Long  createTime;// 订单创建时间
	private Long  finishTime;// 订单创建时间
	private Long userId;
	private String startTime;
	private String passTime;
	private String username;
	private int flag;

	private String multServerId;

	/**
	 * 为了时间转化，新建两个string类型的变量存转化后的时间
	 */
	private String  strCreateTime;
	private String  strFinishTime;

	public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public String getStrFinishTime() {
		return strFinishTime;
	}

	public void setStrFinishTime(String strFinishTime) {
		this.strFinishTime = strFinishTime;
	}

	public PayList(){

	}

	public PayList(int flag,String orderId,String thirdTradeNo,Integer pid,Integer sid,String username,String playerName){
		this.flag = flag;
		this.orderId = orderId;
		this.thirdTradeNo = thirdTradeNo;
		this.pid = pid;
		this.sid = sid;
		this.username = username;
		this.playerName = playerName;
	}

	public PayList(String orderId,String thirdTradeNo,Integer proIdx,Float payPrice,Long userId,Integer sid,Integer pid,Integer status,String platform,String channel,String source,String startTime,String passTime){
		this.orderId = orderId;
		this.thirdTradeNo = thirdTradeNo;
		this.proIdx = proIdx;
		this.payPrice = payPrice;
		this.userId = userId;
		this.sid = sid;
		this.pid = pid;
		this.status = status;
		this.platform = platform;
		this.channel = channel;
		this.source = source;
		this.startTime = startTime;
		this.passTime = passTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Float getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(Float payPrice) {
		this.payPrice = payPrice;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Integer getProIdx() {
		return proIdx;
	}

	public void setProIdx(Integer proIdx) {
		this.proIdx = proIdx;
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

	public String getThirdTradeNo() {
		return thirdTradeNo;
	}

	public void setThirdTradeNo(String thirdTradeNo) {
		this.thirdTradeNo = thirdTradeNo;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getPassTime() {
		return passTime;
	}

	public void setPassTime(String passTime) {
		this.passTime = passTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getMultServerId() {
		return multServerId;
	}

	public void setMultServerId(String multServerId) {
		this.multServerId = multServerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
