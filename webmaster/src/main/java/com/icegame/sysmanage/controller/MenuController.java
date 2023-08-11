package com.icegame.sysmanage.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.icegame.framework.utils.TreeUtils;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.dto.TreeDto;
import com.icegame.sysmanage.entity.Menu;
import com.icegame.sysmanage.service.IMenuService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 菜单管理的controller
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/sysmgr/menu")
public class MenuController {
	
	private static Logger logger = Logger.getLogger(MenuController.class);
	
	@Autowired
	private IMenuService menuService;
	//进入菜单查询列表功能
	@RequestMapping("/gotoMenuList")
	public String gotoMenuList(Model model){
		return "sysmanage/menu/menuList";
	}
	
	
	@RequestMapping("/getMenuList")
	@ResponseBody
	public String getMenuList(@RequestBody Menu menu){
		//进入功能初始化所有的列表
		List<Menu> menuList = this.menuService.getMenuListByUserId(UserUtils.getCurrrentUserId());
		//List<Menu> menuList = this.menuService.getAllMenuList();
		List<Menu> sortMenuList = new ArrayList<Menu>();
		//因为前台组件treeTable正常显示树形结构的数据,就必须让我们的列表按照树形的结构顺序摆放
		TreeUtils.sortTreeList(sortMenuList, menuList, 0l);
		JSONArray jsonArr = JSONArray.fromObject(sortMenuList);
		return jsonArr.toString();
	}
	
	
	//进入字典编辑功能
	//进入编辑功能的时候，要根据id获取编辑对象的明细记录
	@RequestMapping("/gotoMenuEdit")
	@ResponseBody
	public String gotoMenuEdit(@ModelAttribute("editFlag") int editFlag,
					Long menuId,Long parentId,Model model){
		//如果editFlag = 2 则是进入修改页面，我们需要查询待修改记录的明细信息
		JSONObject menuObj = new JSONObject();
		if(editFlag == 2){
			Menu menu=menuService.getMenuById(menuId);
			menuObj = JSONObject.fromObject(menu);
		} 
		if(editFlag == 1){//进入增加页面
			if(parentId!=null){
				Menu parentMenu = menuService.getMenuById(parentId);
				Menu menu = new Menu();
				menu.setParentId(parentMenu.getId());
				menu.setParentName(parentMenu.getName());
				menuObj = JSONObject.fromObject(menu);
			}
		}
		return menuObj.toString();   
	}
	
	@RequestMapping("/gotoIconSelect")
	public String gotoIconSelect(@ModelAttribute("value") String value){		 
		return "sysmanage/menu/iconSelect";
	}
	
	//获取所有树形结构 菜单节点信息
	@RequestMapping("/getParentMenuTreeData")
	public @ResponseBody  List<TreeDto> getParentMenuTreeData(Long menuId){
		List<TreeDto> treeList = new ArrayList<TreeDto>();
		List<Menu> menuList = this.menuService.getAllMenuList();
		for(Menu menu : menuList){
			TreeDto tree = new TreeDto();
			tree.setId(menu.getId());
			tree.setName(menu.getName()); 
			tree.setParentId(menu.getParentId());
			treeList.add(tree);
		}
		
		//如果是进入修改页面,为放置死循环 ,我们必须本节点以及本节点所有的儿子,孙子,全部过滤掉
		//1:把他的儿子,孙子全部找出来
		//2:把他的儿子,孙子从列表里面remove
		if(menuId!=null){//代表进入的是修改页面
			Map<Long,TreeDto> childrenMap = new HashMap<Long,TreeDto>();
			//1:把他的儿子,孙子全部找出来
			TreeUtils.getAllChildrenMap(childrenMap, treeList, menuId);
			//2:把他的儿子,孙子从列表里面remove
			Iterator<TreeDto> treeIterator = treeList.iterator();
			while(treeIterator.hasNext()){
				TreeDto tree = treeIterator.next();
				//如果子节点列表里面存在这个对象,则删除
				if(childrenMap.get(tree.getId())!=null){
					treeIterator.remove();
				}
				//删除本身
				if(tree.getId().equals(menuId)){
					treeIterator.remove();
				}
			}			
		}
		return treeList;
	}
	
	@RequestMapping("/checkExistMenu")
	public @ResponseBody String checkExistMenu(String name){
		Menu menu = new Menu();
		menu.setParentName(name);
		List<Menu> menuList = menuService.checkExistMenu(menu);
		if(menuList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}
	
	@RequestMapping("/saveMenu") 
	public @ResponseBody Map<String,Object> saveMenu(@RequestBody Menu menu){
		if(null == menu.getParentId())menu.setParentId(1l);
		Map<String,Object> resultMap = new HashMap<String,Object>();	
		try{
			if(menu!=null&&menu.getId()!=null){//修改 
				menuService.updateMenu(menu);
				resultMap.put("result","修改菜单信息成功");
				logger.info("修改["+menu.getName()+"]菜单成功");
			}else{
				if(menu.getIcon().equals("") && menu.getParentId() != 1 && menu.getParentId() != 0 && menu.getSort() != 100)menu.setIcon("list-alt");
				if(menu.getParentId().equals(""))menu.setParentId(1L);
				menuService.addMenu(menu);
				resultMap.put("result","增加菜单信息成功");
				logger.info("增加["+menu.getName()+"]菜单成功");
			}
		}catch(Exception e){
			logger.error("操作["+menu.getName()+"]菜单失败",e);
			resultMap.put("result","操作菜单信息失败");
		}
		return resultMap;
	}  
	
	
	//删除菜单
	@RequestMapping("/delMenu")
	public @ResponseBody Map<String,Object>  delMenu(Long menuId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//对于树形结构的数据,我们在删除的时候要注意 ,必须确保无子节点才可以直接删除,否则要给出提示
		if(menuService.getChildCount(menuId).intValue()>0){
			resultMap.put("result", "此菜单下面还有子菜单,请确定删除所有的子菜单后再进行此操作");			 	
			return resultMap;
		}
		try{
			if(menuService.delMenu(menuId)){
				resultMap.put("result", "删除菜单信息成功");
				logger.info("删除["+menuId+"]菜单成功");
			}		 	
		}catch(Exception e){
			resultMap.put("result", "删除["+menuId+"]菜单信息失败");
			logger.error("删除["+menuId+"]菜单信息失败",e);
		}
 		return resultMap;
	}
	
}
