package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.ExclusiveCsQQ;

import java.util.List;

public interface IExclusiveCsQQService {

	/**************************
	 ******* api 接口  *********
	 ***************************/

	/**
	 * 根据 playerId 和 serverId 查询是否已经存在此记录
	 * @param exclusiveCsQQ
	 * @return
	 */
	public List<ExclusiveCsQQ> existCurrentPlayer(ExclusiveCsQQ exclusiveCsQQ);

	/**
	 * 获取 功能的开关
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean getVipQQStatus(ExclusiveCsQQ exclusiveCsQQ);

	/**
	 * 获取 功能的开关 返回对象
	 * @param exclusiveCsQQ
	 * @return
	 */
	public ExclusiveCsQQ getVipQQStatusObject(ExclusiveCsQQ exclusiveCsQQ);

	/**
	 * 获取 配置的总金额
	 * @param exclusiveCsQQ
	 * @return
	 */
	public ExclusiveCsQQ getMoney(ExclusiveCsQQ exclusiveCsQQ);


	/**
	 * 获取 分配次数最少的qq
	 * @param exclusiveCsQQ
	 * @return
	 */
	public List<ExclusiveCsQQ> getMinTimesQQ(ExclusiveCsQQ exclusiveCsQQ);

	/**
	 * 获取 添加一个记录到记录表中
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean addPlayerRecode(ExclusiveCsQQ exclusiveCsQQ);


	/**
	 * 自增1
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean timesPlus(ExclusiveCsQQ exclusiveCsQQ);




	/**************************
	 ******* web 页面 接口  ****
	 **************************/

	public PageInfo<ExclusiveCsQQ> getQQList(ExclusiveCsQQ exclusiveCsQQ, PageParam pageParam);

	/**
	 * 增加一条qq记录
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean addQQ(ExclusiveCsQQ exclusiveCsQQ);


	/**
	 * 修改一条qq记录
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean updateQQ(ExclusiveCsQQ exclusiveCsQQ);


	/**
	 * 删除qq
	 * @param id
	 * @return
	 */
	public boolean delQQ(Long id);


	/**
	 * 修改开关
	 * @param isOpen
	 * @return
	 */
	public boolean updateQQStatus(String isOpen);


	/**
	 * 修改金额
	 * @param money
	 * @return
	 */
	public boolean updateMoney(String money);

	/**
	 * 根据id查询qq
	 * @param id
	 * @return
	 */
	public ExclusiveCsQQ getQQById(Long id);

	/**
	 * 判断是否存在
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean existsQQ(ExclusiveCsQQ exclusiveCsQQ);

	/**
	 * 删除一个qq的时候，同时接触绑定的玩家
	 * @param id
	 * @return
	 */
	public boolean unbindQQ(Long id);
}

