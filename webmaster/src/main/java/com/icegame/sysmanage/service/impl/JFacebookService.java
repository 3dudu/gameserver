package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.JFacebook;
import com.icegame.sysmanage.entity.JVipqqWxConfig;
import com.icegame.sysmanage.mapper.JFacebookMapper;
import com.icegame.sysmanage.service.IJFacebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * fb点赞配置<p>
 * Company: 南京雪糕网络科技有限公司<p>
 *
 * @author wzy
 * @since 2019/10/28 20:16
 */
@Service
public class JFacebookService implements IJFacebookService {

    @Autowired
    private JFacebookMapper jFacebookMapper;


    @Override
    public PageInfo<JFacebook> getListPage(JFacebook jFacebook, PageParam pageParam) {
        PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
        List<JFacebook> configList = jFacebookMapper.getFacebooklike(jFacebook);
        PageInfo<JFacebook> pageInfo = new PageInfo<JFacebook>(configList);
        return pageInfo;
    }

    @Override
    public boolean addFacebooklike(JFacebook jFacebook) {
        return jFacebookMapper.addFacebooklike(jFacebook);
    }

    @Override
    public boolean updateFacebooklike(JFacebook jFacebook) {
        return jFacebookMapper.updateFacebooklike(jFacebook);
    }

    @Override
    public boolean delFacebooklike(Long id) {
        return jFacebookMapper.delFacebooklike(id);
    }

    @Override
    public JFacebook getFacebooklikeById(Long id) {
        return jFacebookMapper.getFacebooklikeById(id);
    }

    @Override
    public List<JFacebook> getFacebooklikeByChannel(JFacebook jFacebook) {
        return jFacebookMapper.getFacebooklikeByChannel(jFacebook);
    }
}
