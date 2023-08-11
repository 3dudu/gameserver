package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.XQMail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XQMailMapper {

	public List<XQMail> findAll(XQMail queryParams);

	public XQMail findById(@Param("id") Long id);

	public boolean addXQMail(XQMail roleMail);

	public boolean refreshStatus(XQMail roleMail);

	public List<XQMail> hasSameIssueOrderId(@Param("issueOrderId") String issueOrderId);
}
