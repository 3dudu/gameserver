package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.dto.MenuDto;
import com.icegame.sysmanage.entity.Menu;

/**
 * 用户增删改查以及登陆验证的mapper代理接口
 * @author Administrator
 *
 */
public interface MenuMapper {
	
	public List<Menu> getAllMenuList();
	
	public List<Menu> checkExistMenu(Menu menu);
	
	public Menu getMenuById(Long menuId);
	
	public boolean addMenu(Menu menu);
	
	public boolean delMenu(Long menuId);

	public boolean updateMenu(Menu menu);
	
	/**
	 * 获取某个节点的子节点数目,用于删除的特殊判断
	 * @Description: TODO  
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public Integer getChildCount(Long menuId);

	/**
	 * 查询用户权限控制内的所有菜单
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

