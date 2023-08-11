package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Email;
import com.icegame.sysmanage.mapper.EmailMapper;
import com.icegame.sysmanage.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService implements IEmailService {
	
	@Autowired
	private EmailMapper emailMapper;

	@Override
	public PageInfo<Email> getEmailList(Email email, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<Email> emailList = emailMapper.getEmailList(email);
		PageInfo<Email> pageInfo = new PageInfo<Email>(emailList);
		return pageInfo;
	}

	@Override
	public List<Email> getAutoOpenServerEmailList(Email email) {
		return emailMapper.getAutoOpenServerEmailList(email);
	}

	@Override
	public List<Email> existEmailObject(Email email) {
		return emailMapper.existEmail(email);
	}

	@Override
	public boolean existEmail(Email email) {
		List<Email> emailList = emailMapper.existEmail(email);
		return emailList.size() > 0 ? true : false;
	}

	@Override
	public Email getEmailById(Long id) {
		return emailMapper.getEmailById(id);
	}

	@Override
	public boolean addEmail(Email email) {
		return emailMapper.addEmail(email);
	}

	@Override
	public boolean delEmail(Long id) {
		return emailMapper.delEmail(id);
	}

	@Override
	public boolean updateEmail(Email email) {
		return emailMapper.updateEmail(email);
	}
}
