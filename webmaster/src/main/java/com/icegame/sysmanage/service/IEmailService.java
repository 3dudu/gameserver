package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Email;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IEmailService {


	/**
	 * 查询所有邮箱列表
	 * @param email
	 * @return
	 */
	public PageInfo<Email> getEmailList(Email email, PageParam pageParam);


	/**
	 * 查询自动开服的邮箱列表
	 * @param email
	 * @return
	 */
	public List<Email> getAutoOpenServerEmailList(Email email);


	/**
	 * 查询是否存在重复的email
	 * @param email
	 * @return
	 */
	public List<Email> existEmailObject(Email email);


	/**
	 * 查询是否存在重复的email
	 * @param email
	 * @return
	 */
	public boolean existEmail(Email email);


	/**
	 * 根据id查询邮箱
	 * @param id
	 * @return
	 */
	public Email getEmailById(@Param("id") Long id);


	/**
	 * 添加邮箱
	 * @param email
	 * @return
	 */
	public boolean addEmail(Email email);


	/**
	 * 删除邮箱
	 * @param id
	 * @return
	 */
	public boolean delEmail(@Param("id") Long id);


	/**
	 * 修改邮箱
	 * @param email
	 * @return
	 */
	public boolean updateEmail(Email email);
}

