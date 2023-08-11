package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.ExclusiveCsQQ;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExclusiveCsQQMapper {

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
	public ExclusiveCsQQ getVipQQStatus(ExclusiveCsQQ exclusiveCsQQ);

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

	public List<ExclusiveCsQQ> getQQList(ExclusiveCsQQ exclusiveCsQQ);


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
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean updateQQStatus(ExclusiveCsQQ exclusiveCsQQ);


	/**
	 * 修改金额
	 * @param exclusiveCsQQ
	 * @return
	 */
	public boolean updateMoney(ExclusiveCsQQ exclusiveCsQQ);

	/**
	 * 修改开关
	 * @param id
	 * @return
	 */
	public ExclusiveCsQQ getQQById(Long id);


	/**
	 * 查询是否存在qq
	 * @param id
	 * @return
	 */
	public List<ExclusiveCsQQ> existsQQ(ExclusiveCsQQ exclusiveCsQQ);


	/**
	 * 删除qq的时候，接触绑定的玩家
	 * @param qq
	 * @return
	 */
	public boolean unbindQQ(Long qq);


}
