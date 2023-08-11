package com.xuegao.core.netty.websocketproto;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.MessageLite;
import com.googlecode.protobuf.format.JsonFormat;
import com.xuegao.core.netty.websocketproto.PbMsg.BaseData;

public class PbJsonObject extends JSONObject{
	
	private static final long serialVersionUID = 1L;
	
	PbMsg.BaseData baseData;
	MessageLite extensionData;

	public PbJsonObject(BaseData baseData) {
		super();
		this.baseData = baseData;
	}

	public PbMsg.BaseData getBaseData() {
		return baseData;
	}

	public MessageLite getExtensionData() {
		return extensionData;
	}

	public void setExtensionData(MessageLite extensionData) {
		this.extensionData = extensionData;
	}
	
	@Override
	public String toString() {
		return JsonFormat.printToString(this.baseData);
	}
}
