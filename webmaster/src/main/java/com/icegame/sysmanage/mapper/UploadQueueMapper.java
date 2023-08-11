package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.UploadQueue;

import java.util.List;

public interface UploadQueueMapper {
		
	public List<UploadQueue> getAllProcess(UploadQueue uploadQueue);

	public List<UploadQueue> getInProcess(UploadQueue uploadQueue);

	public List<UploadQueue> getNextProcess(UploadQueue uploadQueue);

	public UploadQueue getProcessById(Long id);
	
	public boolean addProcess(UploadQueue uploadQueue);
	
	public boolean delProcess(Long id);

	public boolean changeToUploading(UploadQueue uploadQueue);

	public boolean changeToFinished(UploadQueue uploadQueue);

}
