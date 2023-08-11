package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.ExportTable;
import com.icegame.sysmanage.dto.PayListDto;
import com.icegame.sysmanage.entity.JKeyUse;
import com.icegame.sysmanage.entity.JUser;
import com.icegame.sysmanage.entity.PayList;

import java.io.InputStream;

public interface IPayListService {

	public PageInfo<PayList> getPayList(PayList payList, PageParam pageParam);

	public PageInfo<PayListDto> getPayListResetPwd(PayList payList, PageParam pageParam);

	public boolean resetJUserPwd(Long id,String username,String passwd);

	public InputStream getInputStreamRetention(ExportTable et) throws Exception;

	public InputStream exportOneMonth(PayList payList) throws Exception;

}

