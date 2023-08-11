package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.XQMail;

public interface IXQMailService {

	PageInfo<XQMail> findAll(XQMail roleMail, PageParam pageParam);

	XQMail findById(Long id);

	boolean addXQMail(XQMail mail);

	boolean refreshStatus(XQMail roleMail);

	String syncXQMail(Long id);

	/**
	 * 判断是否存在时间戳相同的订单ID，存在，则不进行发送
	 * @param issueOrderId 要发送的邮件的时间戳
	 * @param maxValue 最大值
	 * @return true 存在 ，false 不存在
	 */
	public boolean hasSameIssueOrderId(String issueOrderId, Integer maxValue);

}

