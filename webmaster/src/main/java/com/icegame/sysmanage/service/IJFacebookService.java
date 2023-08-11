package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.JFacebook;

import java.util.List;

/**
 * facebook点赞<p>
 * Company: 南京雪糕网络科技有限公司<p>
 *
 * @author wzy
 * @since 2019/10/28 20:13
 */
public interface IJFacebookService {

    /**
     * 获取点赞
     * @param jFacebook
     * @return
     */

    public PageInfo<JFacebook> getListPage(JFacebook jFacebook, PageParam pageParam);


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
