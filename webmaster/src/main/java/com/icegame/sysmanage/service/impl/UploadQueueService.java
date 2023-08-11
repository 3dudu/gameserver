package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.UploadQueue;
import com.icegame.sysmanage.mapper.UploadQueueMapper;
import com.icegame.sysmanage.service.IUploadQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UploadQueueService implements IUploadQueueService{
	
	@Autowired
	private UploadQueueMapper uploadQueueMapper;
	
	@Autowired
	private LogService logService;
	
	Log log = new Log();

	/**
	 *
	 * @param uploadQueue
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 10:28:05
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@Override
	public PageInfo<UploadQueue> getAllProcess(UploadQueue uploadQueue, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<UploadQueue> allProcess = uploadQueueMapper.getAllProcess(uploadQueue);
		if(allProcess.size() > 0){
			for(UploadQueue uq : allProcess){
				uq.setCreateTime(TimeUtils.stampToDateWithMill(uq.getCreateTime()));
				uq.setUploadTime(TimeUtils.stampToDateWithMill(uq.getUploadTime()));
			}
		}
		PageInfo<UploadQueue> pageInfo = new PageInfo<UploadQueue>(allProcess);
		return pageInfo;
	}

	@Override
	public List<UploadQueue> getInProcess(UploadQueue uploadQueue) {
		return uploadQueueMapper.getInProcess(uploadQueue);
	}

	@Override
	public List<UploadQueue> getNextProcess(UploadQueue uploadQueue) {
		return uploadQueueMapper.getNextProcess(uploadQueue);
	}

	@Override
	public UploadQueue getProcessById(Long id) {
		return uploadQueueMapper.getProcessById(id);
	}

	@Override
	public boolean addProcess(UploadQueue uploadQueue) {
		log = UserUtils.recording("添加上传文件任务，文件位置：" + uploadQueue.getFile(),Type.ADD.getName());
		logService.addLog(log);
		return uploadQueueMapper.addProcess(uploadQueue);
	}

	@Override
	public boolean delProcess(Long id) {
		UploadQueue uploadQueue = uploadQueueMapper.getProcessById(id);
		log = UserUtils.recording("删除上传文件任务，文件位置：" + uploadQueue.getFile(),Type.DELETE.getName());
		logService.addLog(log);
		return uploadQueueMapper.delProcess(id);
	}

	@Override
	public boolean changeToUploading(UploadQueue uploadQueue) {
		return uploadQueueMapper.changeToUploading(uploadQueue);
	}

	@Override
	public boolean changeToFinished(UploadQueue uploadQueue) {
		return uploadQueueMapper.changeToFinished(uploadQueue);
	}

	@Override
	public boolean isInWaiting(UploadQueue uploadQueue){
		boolean flag = false;
		UploadQueue uq = uploadQueueMapper.getProcessById(uploadQueue.getId());
		if(null != uq && Integer.valueOf(uq.getState()) == 0){
			flag = true;
		}
		return flag;
	}
}
