package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.AllSrvMail;
import com.icegame.sysmanage.entity.UploadQueue;

import java.util.List;

public interface IUploadQueueService {

	public PageInfo<UploadQueue> getAllProcess(UploadQueue uploadQueue, PageParam pageParam);

	public List<UploadQueue> getInProcess(UploadQueue uploadQueue);

	public List<UploadQueue> getNextProcess(UploadQueue uploadQueue);

	public UploadQueue getProcessById(Long id);

	public boolean addProcess(UploadQueue uploadQueue);

	public boolean delProcess(Long id);

	public boolean changeToUploading(UploadQueue uploadQueue);

	public boolean changeToFinished(UploadQueue uploadQueue);

	public boolean isInWaiting(UploadQueue uploadQueue);
}

