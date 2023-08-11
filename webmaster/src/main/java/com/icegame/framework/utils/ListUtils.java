package com.icegame.framework.utils;

import java.util.ArrayList;
import java.util.List;

import com.icegame.sysmanage.entity.Group;

/**
 * 接受一个List<bean> 对象，返回他其中的一个字段的属性
 * @Description: TODO
 * @Package com.icegame.framework.utils
 * @author chesterccw
 * @date 2018年1月20日
 */
public class ListUtils {
	
	public static List<String> getGroupNameList(List<Group> groupList){
		List<String> groupNameList = new ArrayList<String>();
		for(int i = 0 ; i < groupList.size() ; i++ ){
			groupNameList.add(groupList.get(i).getGroupName());
		}
		return groupNameList;
	}
	
	
	public static List<Long> getGroupIdList(List<Group> groupList){
		List<Long> groupIdList = new ArrayList<Long>();
		for(int i = 0 ; i < groupList.size() ; i++ ){
			groupIdList.add(groupList.get(i).getGroupId());
		}
		return groupIdList;
	}
	
}
