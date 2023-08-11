package com.icegame.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icegame.sysmanage.entity.MailMaxId;
import com.icegame.sysmanage.mapper.MailMaxIdMapper;
import com.icegame.sysmanage.service.IMailMaxIdService;

@Service
public class MailMaxIdService implements IMailMaxIdService {
	
	@Autowired
	private MailMaxIdMapper mailMaxIdMapper;

	public Long getMailMaxId() {
		MailMaxId ids =  mailMaxIdMapper.getMailMaxId();
		Long maxId = null;
		if(null != ids) {
			List<Long> idsL = new ArrayList<Long>();
			if(null != ids.getAllSrvMailId() && null != ids.getSrvMailId() && null != ids.getRoleMailId()) {
				idsL.add(ids.getAllSrvMailId());
				idsL.add(ids.getSrvMailId());
				idsL.add(ids.getRoleMailId());
				maxId = Collections.max(idsL);
			}else if (null == ids.getAllSrvMailId() && null == ids.getSrvMailId()) {
				idsL.add(ids.getRoleMailId());
				maxId = Collections.max(idsL);
			}else if (null == ids.getAllSrvMailId() && null == ids.getRoleMailId()) {
				idsL.add(ids.getSrvMailId());
				maxId = Collections.max(idsL);
			}else if (null == ids.getSrvMailId() && null == ids.getRoleMailId()) {
				idsL.add(ids.getAllSrvMailId());
				maxId = Collections.max(idsL);
			}else if (null == ids.getAllSrvMailId()) {
				idsL.add(ids.getSrvMailId());
				idsL.add(ids.getRoleMailId());
				maxId = Collections.max(idsL);
			}else if(null == ids.getSrvMailId()) {
				idsL.add(ids.getAllSrvMailId());
				idsL.add(ids.getRoleMailId());
				maxId = Collections.max(idsL);
			}else if(null == ids.getRoleMailId()) {
				idsL.add(ids.getAllSrvMailId());
				idsL.add(ids.getSrvMailId());
				maxId = Collections.max(idsL);
			}
			
		}else {
			maxId = 0l;
		}
		
		/*返回的值比 三张邮件表的ID大1 保证三张邮件表的ID 不重复*/
		return maxId+1;
	}

}
