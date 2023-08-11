package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.JFacebook;

import java.util.List;

/**
 * @author wzy
 **/
public interface JFacebookMapper {


	/**************************
	 ******* web 页面 接口  ****
	 **************************/

	/**
	 * 获取点赞
	 * @param jFacebook
	 * @return
	 */

	public List<JFacebook> getFacebooklike(JFacebook jFacebook);


	/**
	 * 增加点赞
	 * @param jFacebook
	 * @return
	 */
	public boolean addFacebooklike(JFacebook jFacebook);


	/**
	 * 修改点赞
	 * @param jFacebook
	 * @return
	 */
	public boolean updateFacebooklike(JFacebook jFacebook);


	/**
	 * 删除点赞
	 * @param id
	 * @return
	 */
	public boolean delFacebooklike(Long id);

	/**
	 * 根据id获取点赞记录
	 * @param id
	 * @return
	 */

	JFacebook getFacebooklikeById(Long id);

	/**
	 * 根据channel获取点赞
	 * @param jFacebook
	 * @return
	 */

	public List<JFacebook> getFacebooklikeByChannel(JFacebook jFacebook);

}
