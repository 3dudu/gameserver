package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.ExclusiveCsQQ;
import com.icegame.sysmanage.entity.WechatPub;

import java.util.List;

public interface WechatPubMapper {

    /**************************
     ******* api 接口  *********
	 ***************************/
	/**
	 * 获取 功能的开关
	 * @param wechatPub
	 * @return
	 */
	public WechatPub getWeixinStatus(WechatPub wechatPub);

	/**
	 * 获取 配置的公众号名字
	 * @param wechatPub
	 * @return
	 */
	public WechatPub getPubNumber(WechatPub wechatPub);



	/**************************
	 ******* web 页面 接口  ****
	 **************************/


	/**
	 * 修改微信公众号开关
	 * @param isOpen
	 * @return
	 */
	public boolean updateWeixinStatus(String isOpen);


	/**
	 * 修改 公众号名字
	 * @param name
	 * @return
	 */
	public boolean updatePubNumber(String name);


}
