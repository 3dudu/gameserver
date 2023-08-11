package com.icegame.sysmanage.service;

import java.util.List;

import com.icegame.sysmanage.dto.MenuDto;
import com.icegame.sysmanage.entity.Menu;

/**
 * 菜单业务处理接口
 * 
 * @Description: TODO
 * @Package com.icegame.sysmanage.service
 * @author chesterccw
 * @date 2018年1月22日
 */
public interface IMenuService {
	/**
	 * 获取所有操作菜单
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<Menu> getAllMenuList();
	
	/**
	 * 检查是否存在该按钮
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<Menu> checkExistMenu(Menu menu);

	/**
	 * 根据MenuId查询菜单
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public Menu getMenuById(Long menuId);

	/**
	 * 获取某个节点的子节点数目,用于删除的特殊判断
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public Integer getChildCount(Long menuId);

	/**
	 * 增加菜单
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean addMenu(Menu menu);

	/**
	 * 删除菜单
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean delMenu(Long menuId);

	/**
	 * 修改菜单
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean updateMenu(Menu menu);

	/**
	 * 查询用户权限控制内的所有菜单
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<Menu> getMenuListByUserId(Long currrentUserId);
	
	/**
	 * 查询按钮菜单
	 * @param menu
	 * @return
	 */
	public List<Menu> getButtonListByUserId(MenuDto menuDto);

}
