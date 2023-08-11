package com.icegame.sysmanage.controller;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.JFacebook;
import com.icegame.sysmanage.entity.JVipqqWxConfig;
import com.icegame.sysmanage.service.IJFacebookService;
import com.icegame.sysmanage.service.IJVipqqWxConfigService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * facebook点赞数<p>
 * Company: 南京雪糕网络科技有限公司<p>
 *
 * @author wzy
 * @since 2019/10/28 20:21
 */
@Controller
@RequestMapping("/sysmgr/facebook")
public class JFacebookController {

    private static Logger logger = Logger.getLogger(JFacebookController.class);

    @Autowired
    private IJFacebookService jFacebookService;

    @Autowired
    private GroupUtils groupUtils;

    @RequestMapping("/gotoJFacebook")
    public String gotoJVipqqWxConfig(){
        return "sysmanage/vip/facebook";
    }

    @RequestMapping("/getJFacebooklike")
    @ResponseBody
    public String getJFacebooklike(String channel, int pageNo,int pageSize){

        //获取分页条件
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        //获取分页数据总和
        List<JFacebook> configList = new ArrayList<JFacebook>();

        /**
         * 此处添加联运权限验证
         */
        String hasChannel = groupUtils.getGroupHasChannel();
        if(StringUtils.isNotNull(hasChannel)){
            hasChannel = StringUtils.multFormat(hasChannel);
        }

        PageInfo<JFacebook> pageInfo = this.jFacebookService.getListPage(new JFacebook(channel, hasChannel), pageParam);
        configList = pageInfo.getList();
        JSONArray jsonArr = JSONArray.fromObject(configList);
        //获取分页条
        String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
        jsonArr.add(0, pageStr);
        return jsonArr.toString();
    }

    @RequestMapping("/gotoJFacebooklikeEdit")
    @ResponseBody
    public String gotoJFacebooklikeEdit(@ModelAttribute("editFlag") int editFlag,
                                         Long id, Model model){
        JSONObject jsonObj = new JSONObject();
        JFacebook jFacebook = new JFacebook();
        if(editFlag == 2){
            jFacebook = jFacebookService.getFacebooklikeById(id);
            jsonObj = JSONObject.fromObject(jFacebook);
        }
        return jsonObj.toString();
    }

//    @ResponseBody
//    @RequestMapping("/existsJFacebooklike")
//    public String existsCurrentChannelJFacebooklike(String channel){
//        boolean flag = jFacebookService.getJFacebookByChannel(new JVipqqWxConfig(channel));
//        if(flag) {
//            return "1";
//        }else {
//            return "0";
//        }
//    }

    @RequestMapping("/saveJFacebooklike")
    public @ResponseBody
    Map<String,Object> saveJFacebooklike(@RequestBody JFacebook jFacebook){

        Map<String,Object> resultMap = new HashMap<String,Object>();

        try{
            if(jFacebook != null && jFacebook.getId() != null){
                if(jFacebookService.updateFacebooklike(jFacebook)){
                    resultMap.put("result", "修改成功");
                    logger.info("修改FACEBOOK点赞记录成功");
                }
            }else{

                // 如果是增加操作，需要判断是否已经存在
                if(jFacebookService.getFacebooklikeByChannel(jFacebook).size() > 0){
                    resultMap.put("result", "渠道配置已经存在,添加配置失败！");
                    return resultMap;
                }

                if(jFacebookService.addFacebooklike(jFacebook)){
                    resultMap.put("result", "增加记录信息成功");
                    logger.info("增加FACEBOOK点赞配置成功");
                }
            }
        }catch(Exception e){
            resultMap.put("result", "操作FACEBOOK点赞配置失败");
            logger.error("操作FACEBOOK点赞配置失败",e);
        }
        return resultMap;
    }


    @RequestMapping("/delJFacebooklike")
    public @ResponseBody Map<String,Object> delJFacebooklike(Long id){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try{
            if(jFacebookService.delFacebooklike(id)){
                resultMap.put("result", "删除成功");
                logger.info("删除成功");
            }
        }catch(Exception e){
            logger.error("删除失败",e);
            resultMap.put("result", "删除失败");
        }
        return resultMap;
    }

}
