package com.icegame.gm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.gm.entity.JPreinstallMessage;
import com.icegame.gm.entity.MainTainDescTime;
import com.icegame.gm.mapper.JPreinstallMessageMapper;
import com.icegame.gm.mapper.MainTainDescTimeMapper;
import com.icegame.gm.service.IJPreinstallMessageService;
import com.icegame.gm.service.IMainTainDescTimeService;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.service.impl.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MainTainDescTimeService implements IMainTainDescTimeService {
	
	@Autowired
	private MainTainDescTimeMapper mainTainDescTimeMapper;

	@Autowired
	private LogService logService;

	Log log = new Log();

	/**
	 *
	 * @param mtdt
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 11:47:02
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@Override
	public PageInfo<MainTainDescTime> getMainTainDescTimeList(MainTainDescTime mtdt, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<MainTainDescTime> mainTainDescTimeList = mainTainDescTimeMapper.getMainTainDescTimeList(mtdt);
		for(MainTainDescTime mainTainDescTime : mainTainDescTimeList){
			if(System.currentTimeMillis() >= Long.valueOf(mainTainDescTime.getExpectStartTime())){
				mainTainDescTime.setResidueTime(String.valueOf(Long.valueOf(mainTainDescTime.getExpectEndTime()) - System.currentTimeMillis()) + "ms");
			}else{
				mainTainDescTime.setResidueTime("-1ms");
			}
			mainTainDescTime.setExpectStartTime(TimeUtils.stampToDateWithMill(mainTainDescTime.getExpectStartTime()));
			mainTainDescTime.setExpectEndTime(TimeUtils.stampToDateWithMill(mainTainDescTime.getExpectEndTime()));
		}
		PageInfo<MainTainDescTime> pageInfo = new PageInfo<MainTainDescTime>(mainTainDescTimeList);
		return pageInfo;
	}

	@Override
	public List<MainTainDescTime> getMainTainDescTimeList(MainTainDescTime mtdt) {
		List<MainTainDescTime> mainTainDescTimeList = mainTainDescTimeMapper.getMainTainDescTimeList(mtdt);
		if(mainTainDescTimeList.size() >= 1){
			for(MainTainDescTime mainTainDescTime : mainTainDescTimeList){
				if(System.currentTimeMillis() >= Long.valueOf(mainTainDescTime.getExpectStartTime())){
					mainTainDescTime.setResidueTime(String.valueOf(Long.valueOf(mainTainDescTime.getExpectEndTime()) - System.currentTimeMillis()) + "ms");
				}else{
					mainTainDescTime.setResidueTime("-1ms");
				}
			}
		}else{
			MainTainDescTime mainTainDescTime = new MainTainDescTime();
			mainTainDescTime.setExpectEndTime(String.valueOf(System.currentTimeMillis()));
			mainTainDescTime.setResidueTime("0ms");
			mainTainDescTimeList.add(mainTainDescTime);
		}

		return mainTainDescTimeList;
	}

	@Override
	public MainTainDescTime getMainTainDescTimeById(Long id) {
		return mainTainDescTimeMapper.getMainTainDescTimeById(id);
	}

	@Override
	public boolean addMainTainDescTime(MainTainDescTime mtdt) {
		log = UserUtils.recording("添加维护时间，：["+ mtdt.getExpectEndTime() +"]", Type.ADD.getName());
		logService.addLog(log);
		return mainTainDescTimeMapper.addMainTainDescTime(mtdt);
	}

	@Override
	public boolean updateMainTainDescTime(MainTainDescTime mtdt) {
		log = UserUtils.recording("修改维护时间，内容：["+ mtdt.getExpectEndTime() +"]", Type.UPDATE.getName());
		logService.addLog(log);
		return mainTainDescTimeMapper.updateMainTainDescTime(mtdt);
	}

	@Override
	public boolean delMainTainDescTime(Long id) {
		log = UserUtils.recording("删除维护时间，记录id：[" +  id+ "]", Type.DELETE.getName());
		logService.addLog(log);
		return mainTainDescTimeMapper.delMainTainDescTime(id);
	}
}
