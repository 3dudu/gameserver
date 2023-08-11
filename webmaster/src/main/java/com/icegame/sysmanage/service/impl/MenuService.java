package com.icegame.sysmanage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.dto.MenuDto;
import com.icegame.sysmanage.entity.GroupToMenu;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.Menu;
import com.icegame.sysmanage.mapper.GroupMapper;
import com.icegame.sysmanage.mapper.MenuMapper;
import com.icegame.sysmanage.service.IMenuService;


@Service
public class MenuService implements IMenuService{
	
	@Autowired
	private MenuMapper menuMapper;
	
	@Autowired
	private GroupMapper groupMapper;	
	
	@Autowired
	private LogService logService;
	
	Log log = new Log();
	
	public List<Menu> getAllMenuList() {
		return menuMapper.getAllMenuList();
	}
	
	public List<Menu> checkExistMenu(Menu menu) {
		return menuMapper.checkExistMenu(menu);
	}

	public boolean delMenu(Long menuId) {
		boolean flag = false;
		flag = groupMapper.delGroupMenuByMenuId(menuId);
		flag= menuMapper.delMenu(menuId);
		log = UserUtils.recording("删除菜单["+menuId+"]",Type.DELETE.getName());
		logService.addLog(log);
		return flag;
	}

	public Integer getChildCount(Long menuId) {
		return menuMapper.getChildCount(menuId);
	}

	public Menu getMenuById(Long menuId) {
		return menuMapper.getMenuById(menuId);
	}

	public boolean addMenu(Menu menu) {
		boolean flag = false;
		//如果直接添加菜单的时候没有带上级菜单ID，那就默认其为顶级菜单
		if(menu.getParentId() == null){
			menu.setParentId(1l);
		}
		
		/*如果等于0 说明此菜单下面没有子菜单*/
		if(menu.getParentId() != 0 && menu.getParentId() != 1 && menuMapper.getChildCount(menu.getParentId())== 0){
			flag = menuMapper.addMenu(menu);
			/*所以在新增按钮的同事增加一条base记录，防止没有按钮权限的时候无法显示该菜单*/
			Menu menuBase = new Menu();
			menuBase.setParentId(menu.getParentId());menuBase.setName("base");
			menuBase.setSort(10000);menuBase.setHref("");menuBase.setTarget("");
			menuBase.setIcon("");menuBase.setIsShow("0");menuBase.setTodo("alert(\"此按钮为基础按钮,不可以删除,不可以修改为可见状态。如果有看到此菜单,请联系管理员进行修改\");");
		}else{
			flag = menuMapper.addMenu(menu);
		}
		//在增加菜单的时候,同时需要给超级管理增加一条映射记录
		GroupToMenu groupMenu = new GroupToMenu();
		groupMenu.setGroupId(1l);;
		groupMenu.setMenuId(menu.getId());		
		
		flag= this.groupMapper.addGroupToMenu(groupMenu);
		log = UserUtils.recording("添加菜单["+menu.getName()+"]",Type.ADD.getName());
		logService.addLog(log);
		return flag;
	}

	public boolean updateMenu(Menu menu) {
		log = UserUtils.recording("修改菜单["+menu.getName()+"]",Type.UPDATE.getName());
		logService.addLog(log);
 		return menuMapper.updateMenu(menu);
	}

	public List<Menu> getMenuListByUserId(Long currrentUserId) {
		return menuMapper.getMenuListByUserId(currrentUserId);
	}

	public List<Menu> getButtonListByUserId(MenuDto menuDto) {
		return menuMapper.getButtonListByUserId(menuDto);
	}
	
}
