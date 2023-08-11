package com.icegame.sysmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.ExcelMail;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.mapper.ExcelMailMapper;
import com.icegame.sysmanage.service.IExcelMailService;
import com.icegame.sysmanage.service.IServerListService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExcelMailService implements IExcelMailService {

    //超时时间ms
    private static final int REQUEST_TIMEOUT = 3000;

    private static Logger logger = LoggerFactory.getLogger(IExcelMailService.class);

    @Autowired
    private ExcelMailMapper excelMailMapper;

    @Autowired
    private ISlaveNodesService slaveNodesService;

    @Autowired
    private IServerListService serverListService;

    @Autowired
    private LogService logService;

    Log log = new Log();


    /**
     * 保存配置表中邮件内容
     * @param excelMail
     * @return
     */
    @Override
    public Long addExcelMail(ExcelMail excelMail) {
        return excelMailMapper.addExcelMail(excelMail);
    }


    /**
     * 同步配置表中的内容到slave服务器
     * @param mailId
     * @return
     */
    @Override
    public JSONObject syncExcelMail(Long mailId) {

        log = UserUtils.recording("发送配置表邮件：邮件ID=["+ mailId +"]", Type.EXCEL_MAIL_SYNC.getName());
        logService.addLog(log);
        JSONObject result = new JSONObject();
        ExcelMail excelMail = getExcelMailById(mailId);

        if(excelMail == null){
            result.put("ret",1000);
            result.put("status","1");
            result.put("syncTime",TimeUtils.stampToDateWithMill(String.valueOf(System.currentTimeMillis())));
            result.put("msg","邮件ID："+mailId+"，失败原因：找不到该邮件。");
            logger.error("找不到邮件，id={}",mailId);
            return result;
        }
        if (ExcelMail.syncStatus.SUCCESS.getValue().equals(excelMail.getStatus())
                || ExcelMail.syncStatus.RE_SUCCESS.getValue().equals(excelMail.getStatus())){
            result.put("ret",1001);
            result.put("status",ExcelMail.syncStatus.FAIL.getValue());
            result.put("msg","邮件ID："+mailId+"，失败原因：邮件已发送。");
            logger.error("邮件已发送，id={}",mailId);
            return result;
        }
        ServerList serverList = serverListService.getServerById(excelMail.getServerId());
        if(serverList == null){
            excelMail.setStatus(ExcelMail.syncStatus.FAIL.getValue());
            excelMail.setSyncTime(String.valueOf(System.currentTimeMillis()));
            excelMailMapper.updateSuccessMail(excelMail);
            result.put("ret",1002);
            result.put("status",ExcelMail.syncStatus.FAIL.getValue());
            result.put("syncTime",TimeUtils.stampToDateWithMill(excelMail.getSyncTime()));
            result.put("msg","邮件ID："+mailId+"，失败原因：找不到区服（区服ID = " + excelMail.getServerId()+"）");
            logger.error("找不到区服，serverId={}",excelMail.getServerId());
            return result;
        }
        SlaveNodes slaveNodes = slaveNodesService.getSlaveNodesById(serverList.getSlaveId());
        if(slaveNodes == null){
            excelMail.setStatus(ExcelMail.syncStatus.FAIL.getValue());
            excelMail.setSyncTime(String.valueOf(System.currentTimeMillis()));
            excelMailMapper.updateSuccessMail(excelMail);
            result.put("ret",1003);
            result.put("status",ExcelMail.syncStatus.FAIL.getValue());
            result.put("syncTime",TimeUtils.stampToDateWithMill(excelMail.getSyncTime()));
            result.put("msg","邮件ID："+mailId+"，失败原因：找不到slave节点（slaveId = " + serverList.getSlaveId()+"）");
            logger.error("找不到slave节点，slaveId={}",serverList.getSlaveId());
            return result;
        }

        JSONObject syncData = new JSONObject();
        syncData.put("id", excelMail.getId());
        syncData.put("createTime", excelMail.getCreateTime());
        syncData.put("subject", excelMail.getSubject());
        syncData.put("sidPid", excelMail.getServerId()+":"+excelMail.getPlayerId());
        syncData.put("context", excelMail.getContent());
        syncData.put("awardStr", StringUtils.awardStrformart(excelMail.getAwardStr()));

        //向所有的slave节点同步  获取所有slave的节点列表
        String ip = StrUtil.isEmpty(slaveNodes.getNip()) ? slaveNodes.getIp() : slaveNodes.getNip();
        String uri = "http://" + ip +":"+ slaveNodes.getPort() + "/sync/addrolemail";
        String message = "[slaveId=" + slaveNodes.getId() + "]";
        logger.info("{同步配置表邮件/Request}:向" + message + "发送请求,请求地址" + uri + ",发送数据:" + syncData.toString());
        String slaveResult;
        try {
            slaveResult = HttpUtil.post(uri, syncData.toString(),REQUEST_TIMEOUT);
            logger.info("{同步配置表邮件/Response}:接收到" + message + "响应内容:" + slaveResult);
            JSONObject retObj = JSONObject.parseObject(slaveResult);
            Object value = retObj.get("ret");
            if ("0".equals(value)) {
                if (ExcelMail.syncStatus.FAIL.getValue().equals(excelMail.getStatus())){
                    result.put("ret",200);
                    result.put("status",ExcelMail.syncStatus.RE_SUCCESS.getValue());
                    result.put("msg","同步成功, 邮件ID：" + excelMail.getId());
                    excelMail.setStatus(ExcelMail.syncStatus.RE_SUCCESS.getValue());
                }else {
                    result.put("ret",200);
                    result.put("status",ExcelMail.syncStatus.SUCCESS.getValue());
                    result.put("msg","同步成功, 邮件ID：" + excelMail.getId());
                    excelMail.setStatus(ExcelMail.syncStatus.SUCCESS.getValue());
                }
            } else {
                result.put("ret",1006);
                result.put("status",ExcelMail.syncStatus.FAIL.getValue());
                result.put("msg","邮件ID："+mailId+"，失败原因：服务器返回参数错误。");
                excelMail.setStatus(ExcelMail.syncStatus.FAIL.getValue());
            }
        } catch (Exception e) {
            logger.error("同步配置表邮件{}异常，exception：{}",message,e.getMessage());
            result.put("ret",1007);
            result.put("status",ExcelMail.syncStatus.FAIL.getValue());
            result.put("msg","邮件ID："+mailId+"，失败原因：同步配置表邮件异常。");
            excelMail.setStatus(ExcelMail.syncStatus.FAIL.getValue());
        }
        excelMail.setSyncTime(String.valueOf(System.currentTimeMillis()));
        result.put("syncTime", excelMail.getSyncTime());
        excelMailMapper.updateSuccessMail(excelMail);
        return result;
    }

    @Override
    public ExcelMail getExcelMailById(Long id) {
        return excelMailMapper.getExcelMailById(id);
    }

    @Override
    public PageInfo<ExcelMail> getExcelMailList(ExcelMail excelMail, PageParam pageParam) {
        PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());

        if("---选择同步状态---".equals(excelMail.getStatus())){
            excelMail.setStatus("");
        }
        List<ExcelMail> excelMailList = excelMailMapper.getExcelMailList(excelMail);
        return new PageInfo<>(excelMailList);
    }

}
