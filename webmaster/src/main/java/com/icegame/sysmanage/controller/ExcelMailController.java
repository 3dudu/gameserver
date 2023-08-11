package com.icegame.sysmanage.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.entity.ExcelMail;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.service.IExcelMailService;
import com.icegame.sysmanage.service.impl.LogService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sysmgr/excelmail")
public class ExcelMailController {

    private static Logger logger = Logger.getLogger(ExcelMailController.class);

    @Autowired
    private IExcelMailService excelMailService;

    @Autowired
    private AuthorizeUtil authorizeUtil;

    @Autowired
    private LogService logService;

    Log log = new Log();

    @RequestMapping("gotoExcelMail")
    public String gotoExcelMail() {
        return "sysmanage/mail/excelmail";
    }

    @RequestMapping("gotoUploadExcelMail")
    public String gotoUploadExcelMail() {
        return "sysmanage/mail/uploadexcelmail";
    }

    /**
     *  保存所有邮件信息
     * @param jsonObject
     * @return
     */
    @RequestMapping("/saveExcelMail")
    public @ResponseBody JSON saveExcelMail(@RequestBody JSONObject jsonObject){
        JSONObject resultJson = new JSONObject();
        boolean hasAuthorize = authorizeUtil.hasAuthorize("邮件管理","配置表邮件上传");
        if(!hasAuthorize){
            resultJson.put("ret","201");
            resultJson.put("msg","没有权限操作");
            return resultJson;
        }
        List<ExcelMail> excelMailList = new ArrayList<>();
        JSONArray excelData = jsonObject.getJSONArray("excelData");
        if (excelData == null){
            resultJson.put("ret","202");
            resultJson.put("msg","上传表格数据配置错误！");
            logger.info("上传表格数据配置错误！");
            return resultJson;
        }
        log = UserUtils.recording("上传邮件配置表,邮件数量：["+excelData.size()+"]", Type.EXCEL_MAIL_UPLOAD.getName());
        logService.addLog(log);
        String userId = String.valueOf(UserUtils.getCurrrentUserId());
        for(int i = 0 ,size = excelData.size(); i < size ; i++){
            JSONObject o = excelData.getJSONObject(i);
            ExcelMail excelMail = JSONObject.toJavaObject(o, ExcelMail.class);
            excelMail.setCreateTime(String.valueOf(System.currentTimeMillis()));
            excelMail.setStatus(ExcelMail.syncStatus.NEW.getValue());
            excelMail.setUserId(userId);
            excelMailService.addExcelMail(excelMail);
            excelMail.setCreateTime(TimeUtils.stampToDateWithMill(excelMail.getCreateTime()));
            excelMailList.add(excelMail);
        }
        resultJson.put("ret","200");
        resultJson.put("msg","200");
        resultJson.put("data",excelMailList);
        return resultJson;
    }

    @RequestMapping("/sendExcelMail")
    @ResponseBody
    public JSON sendExcelMail(Long mailId){
        JSONObject resultJson = new JSONObject();
        boolean hasAuthorize = authorizeUtil.hasAuthorize("邮件管理","配置表邮件上传");
        if(!hasAuthorize){
            resultJson.put("ret","201");
            resultJson.put("msg","没有权限操作");
            return resultJson;
        }
        JSONObject result = excelMailService.syncExcelMail(mailId);
        return result;
    }


    @RequestMapping("getExcelMailList")
    @ResponseBody
    public JSON getExcelMailList(String startDate,String endDate,String serverId,String playerId,
                                   String subject,String awardStr,String status,int pageNo,int pageSize){
        JSONObject resultJson = new JSONObject();
        Map<String,String> timeMap = TimeUtils.checkDateDetail(startDate, endDate);
        startDate = TimeUtils.dateToStampWithDetail(timeMap.get("startDate"));
        endDate = TimeUtils.dateToStampWithDetail(timeMap.get("endDate"));
        //获取分页条件
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        //获取分页数据总和
        List<ExcelMail> excelMailList;
        ExcelMail excelMail = new ExcelMail();
        excelMail.setServerId(StrUtil.isEmpty(serverId)? 0L :StringUtils.getNumInStr(serverId.trim()));
        excelMail.setPlayerId(StrUtil.isEmpty(playerId)? 0L :StringUtils.getNumInStr(playerId.trim()));
        excelMail.setSubject(subject.trim());
        excelMail.setAwardStr(awardStr.trim());
        excelMail.setStatus(status.trim());
        excelMail.setStartDate(startDate);
        excelMail.setEndDate(endDate);
        PageInfo<ExcelMail> pageInfo = this.excelMailService.getExcelMailList(excelMail, pageParam);
        excelMailList = pageInfo.getList();
        JSONArray jsonArr = JSONArray.parseArray(JSON.toJSONString(excelMailList));
        //获取分页条
        String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
        jsonArr.add(0, pageStr);
        resultJson.put("data",jsonArr);
        return resultJson;
    }

}
