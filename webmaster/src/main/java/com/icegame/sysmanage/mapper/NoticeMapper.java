package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.Notice;

import java.util.List;

public interface NoticeMapper {
		
	public List<Notice> getNoticeList(Notice notice);
			
	public Notice getNoticeById(Long id);
	
	public List<Notice> getNoticeByChannel(String channel,String curTime);
	
	public List<Notice> getChannelList();
	
	public List<Notice> checkExistNotice(Notice notice);
	
	public boolean addNotice(Notice notice);
	
	public boolean delNotice(Long id);

	public boolean updateNotice(Notice notice);

	public List<Notice> getNoticeLists();

}
