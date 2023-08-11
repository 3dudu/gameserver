package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Notice;

import java.util.List;

public interface INoticeService {
	
	public PageInfo<Notice> getNoticeList(Notice notice,PageParam pageParam);
	
	public Notice getNoticeById(Long id);
	
	public List<Notice> getNoticeByChannel(String channel,String curTime);
	
	public List<Notice> getChannelList();
	
	public List<Notice> checkExistNotice(Notice notice);
	
	public boolean addNotice(Notice notice);
	
	public boolean delNotice(Long id);

	public boolean updateNotice(Notice notice);

	public List<Notice> getNoticeLists();

}

