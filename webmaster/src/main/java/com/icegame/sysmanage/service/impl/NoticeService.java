package com.icegame.sysmanage.service.impl;

import java.util.List;

import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.JActivityGiftKey;
import com.icegame.sysmanage.entity.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Notice;
import com.icegame.sysmanage.mapper.NoticeMapper;
import com.icegame.sysmanage.service.INoticeService;

@Service
public class NoticeService implements INoticeService {
	
	@Autowired
	private NoticeMapper noticeMapper;

	@Autowired
	private LogService logService;

	Log log = new Log();

	public PageInfo<Notice> getNoticeList(Notice notice, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		if(notice.getChannel().equals("选择渠道"))notice.setChannel("");
		List<Notice> noticeList = noticeMapper.getNoticeList(notice);
		if(noticeList.size() > 0){
			for(Notice notice1 : noticeList){
				notice1.setStartTime(TimeUtils.stampToDateWithMill(notice1.getStartTime()));
				notice1.setEndTime(TimeUtils.stampToDateWithMill(notice1.getEndTime()));
			}
		}
		PageInfo<Notice> pageInfo = new PageInfo<Notice>(noticeList);
		return pageInfo;
	}

	public Notice getNoticeById(Long id) {
		return noticeMapper.getNoticeById(id);
	}

	public List<Notice> getNoticeByChannel(String channel,String curTime) {
		return noticeMapper.getNoticeByChannel(channel,curTime);
	}
	
	public List<Notice> getChannelList() {
		return noticeMapper.getChannelList();
	}

	public List<Notice> checkExistNotice(Notice notice) {
		return noticeMapper.checkExistNotice(notice);
	}

	public boolean addNotice(Notice notice) {
		log = UserUtils.recording("添加公告，channel ：" + notice.getChannel() + "，内容："+notice.getContext()+"", Type.ADD.getName());
		logService.addLog(log);
		return noticeMapper.addNotice(notice);
	}

	public boolean delNotice(Long id) {
		log = UserUtils.recording("删除公告", Type.DELETE.getName());
		logService.addLog(log);
		return noticeMapper.delNotice(id);
	}

	public boolean updateNotice(Notice notice) {
		log = UserUtils.recording("修改公告", Type.UPDATE.getName());
		logService.addLog(log);
		return noticeMapper.updateNotice(notice);
	}

	@Override
	public List<Notice> getNoticeLists() {
		return noticeMapper.getNoticeLists();
	}

}
