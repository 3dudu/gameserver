package com.icegame.framework.utils;

import cn.hutool.core.util.StrUtil;
import com.icegame.sysmanage.entity.Menu;
import com.icegame.sysmanage.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chesterccw
 * @date 2020/8/6
 */
@Component
public class AuthorizeUtil {

    @Autowired
    public IMenuService menuService;

    private static final AuthorizeUtil INSTANCE = new AuthorizeUtil();

    private AuthorizeUtil () {

    }

    public static AuthorizeUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 判断某个菜单是否有权限
     * @param parentMenuName 父级菜单名字
     * @param menuName 菜单本身名字
     * @return boolean
     */
    public boolean hasAuthorize(String parentMenuName, String menuName){
        if(StrUtil.isEmpty(parentMenuName) || StrUtil.isEmpty(menuName)){
            return false;
        }
        Long curId = UserUtils.getCurrrentUserId();
        List<Menu> menuListByUserId = menuService.getMenuListByUserId(curId);
        for(Menu menu : menuListByUserId){
            if(parentMenuName.equals(menu.getParentName()) &&  menuName.equals(menu.getName())){
                return true;
            }
        }
        return false;
    }

}
