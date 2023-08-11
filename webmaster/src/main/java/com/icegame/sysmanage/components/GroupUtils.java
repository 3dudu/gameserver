package com.icegame.sysmanage.components;

import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.Group;
import com.icegame.sysmanage.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wuzhijian
 * @date 2019-07-04
 */
@Component
public class GroupUtils {

    @Autowired
    private IGroupService groupService;

    public String getGroupHasChannel(){
        Long userId = UserUtils.getCurrrentUserId();
        Group group = new Group();
        if(userId != null){
            Group groupId = groupService.getGroupIdByUserId(userId);
            group = groupService.getGroupInfoById(groupId.getGroupId());
        }
        return  group.getHasChannel();
    }

}
