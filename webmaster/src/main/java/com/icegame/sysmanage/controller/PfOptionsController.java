package com.icegame.sysmanage.controller;


import java.util.*;

import cn.hutool.core.codec.Base64;
import com.icegame.framework.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.HttpUtils;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.sysmanage.entity.PfOptions;
import com.icegame.sysmanage.entity.ServerNodes;
import com.icegame.sysmanage.entity.SyncStatus;
import com.icegame.sysmanage.service.IPfOptionsService;
import com.icegame.sysmanage.service.IServerNodesService;
import com.icegame.sysmanage.service.ISyncStatusService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/pfoptions")
public class PfOptionsController {

    private static Logger logger = Logger.getLogger(PfOptionsController.class);

    @Autowired
    private IPfOptionsService pfOptionsService;

    @Autowired
    private IServerNodesService serverNodesService;

    @Autowired
    private ISyncStatusService syncStatusService;


    @RequestMapping("gotoPfOptions")
    public String gotoPfOptions(){
        return "sysmanage/pfoptions/pfoptions";
    }

    @RequestMapping("getPfOptionsList")
    @ResponseBody
    public String getPfOptionsList(String key,int pageNo,int pageSize){
        //获取分页条件
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        //获取分页数据总和
        List<PfOptions> pfOptionsList = new ArrayList<PfOptions>();
        PfOptions pfOptions = new PfOptions();pfOptions.setKey(key);
        PageInfo<PfOptions> pageInfo = this.pfOptionsService.getPfOptionsList(pfOptions, pageParam);
        pfOptionsList = pageInfo.getList();
        JSONArray jsonArr = JSONArray.fromObject(pfOptionsList);
        //获取分页条
        String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
        jsonArr.add(0, pageStr);
        return jsonArr.toString();
    }

    @RequestMapping("/gotoPfOptionsEdit")
    @ResponseBody
    public String gotoPfOptionsEdit(@ModelAttribute("editFlag") int editFlag,
                                    Long id,Model model){
        JSONObject jsonObj = new JSONObject();
        PfOptions pfOptions = new PfOptions();
        if(editFlag == 2){
            pfOptions = pfOptionsService.getPfOptionsById(id);
            jsonObj = JSONObject.fromObject(pfOptions);
        }
        return jsonObj.toString();
    }

    @RequestMapping("/checkExistPfOptions")
    public @ResponseBody String checkExistPfOptions(String key){
        PfOptions pfOptions = new PfOptions();
        pfOptions.setKey(key);
        List<PfOptions> pfOptionsList = pfOptionsService.checkExistPfOptions(pfOptions);
        if(pfOptionsList.size() > 0) {
            return "1";
        }else {
            return "0";
        }
    }

    @RequestMapping("/savePfOptions")
    public @ResponseBody Map<String,Object> savePfOptions(@RequestBody PfOptions pfOptions){
        String operCmd = "";
        String table = "pf_options";
        Long id = null;
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try{
            /**----------------------------------------------------------------------------------------------------------------**/
            /**----------------------------------------------  先进行修改  在进行数据同步    ----------------------------------------------**/
            /**----------------------------------------------------------------------------------------------------------------**/
            if(pfOptions != null && pfOptions.getId() != null){
                pfOptionsService.updatePfOptions(pfOptions);
                resultMap.put("result", "修改记录信息成功");
                operCmd = "update";
                id = pfOptions.getId();
                logger.info("修改配置");
            }else{//增加
                id = pfOptionsService.addPfOptionsReturnId(pfOptions);
                resultMap.put("result", "增加记录信息成功");
                operCmd = "insert";
                logger.info("增加配置");
            }
            pfOptions.setValue(Base64.encodeUrlSafe(StringUtils.jsonFormat(pfOptions.getValue())));
            JSONObject jsonObj = JSONObject.fromObject(pfOptions);
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("operCmd", operCmd);
            map.put("table", table);
            map.put("id", pfOptions.getId());
            map.put("value", jsonObj);
            JSONObject syncDate = JSONObject.fromObject(map);
            //1.获取所有的节点列表
            List<ServerNodes> serverNodesList = serverNodesService.getServerNodesSync();
            //筛选出登录服节点列表
            List<ServerNodes> loginNodesList = new ArrayList<ServerNodes>();
            //筛选出支付服节点列表
            List<ServerNodes> payNodesList = new ArrayList<ServerNodes>();
            for(int i = 0 ; i < serverNodesList.size() ; i++ ){
                if(serverNodesList.get(i).getDiff() == 1){
                    loginNodesList.add(serverNodesList.get(i));
                }else if(serverNodesList.get(i).getDiff() == 2){
                    payNodesList.add(serverNodesList.get(i));
                }
            }

            logger.info("- --- |【新增】准备向登录服同步配置数据| --- -");

            /**----------------------------------------------------------------------------------------------------------------**/
            /**----------------------------------------------保存之后           向登录服同步数据 ----------------------------------------------**/
            /**----------------------------------------------------------------------------------------------------------------**/
            for(int i = 0 ; i < loginNodesList.size() ; i++){
                ServerNodes sn = loginNodesList.get(i);
                String syncHost = sn.getProtocol()+sn.getIp()+(StringUtils.isNull(sn.getPort())?"":":"+sn.getPort())+sn.getInterfaceName();
                logger.info("向第" + i + "个登录服[" + syncHost + "]同步数据,内容：" + syncDate.toString());
                String result = HttpUtils.jsonPost(syncHost,syncDate.toString());
                JSONObject jsonRet = JSONObject.fromObject(result);
                logger.info("["+ syncHost + "]接口返回：" + jsonRet.toString());
                SyncStatus syncStatus = new SyncStatus(TimeUtils.getDateDetail(),sn.getName(),sn.getIp(),"[添加/修改]["+sn.getName()+"]配置数据----->[同步]",jsonRet.toString());
                syncStatusService.addSyncStatus(syncStatus);
                if(jsonRet.get("ret").equals(0) && jsonRet.get("msg").equals("success")){
                    //说明支付服第一个节点同步成功，所以不再对其他节点进行同步，现在是需要对所有节点请求重载数据的接口
                    for(int j = 0 ; j < loginNodesList.size() ; j++){
                        ServerNodes snj = loginNodesList.get(j);
                        String reloadHost = snj.getProtocol()+snj.getIp()+ (StringUtils.isNull(snj.getPort())?"":":"+snj.getPort()) +snj.getReInterfaceName()+"pf_options";
                        logger.info("请求第" + j + "个登录服[" + reloadHost + "]重载数据");
                        String resultj = HttpUtils.get(reloadHost);
                        logger.info("["+reloadHost + "]接口返回：" + JSONObject.fromObject(resultj).toString());
                        syncStatus = new SyncStatus(TimeUtils.getDateDetail(),snj.getName(),snj.getIp(),"[添加/修改]["+snj.getName()+"]配置数据----->[重载]",JSONObject.fromObject(resultj).toString());
                        syncStatusService.addSyncStatus(syncStatus);
                    }
                    break;
                }
            }

            logger.info("- --- |【新增】向登录服同步完成| --- -");


            logger.info("- --- |【新增】准备向支付服同步配置数据| --- -");

            /**----------------------------------------------------------------------------------------------------------------**/
            /**----------------------------------------------保存之后           向支付服同步数据 ----------------------------------------------**/
            /**----------------------------------------------------------------------------------------------------------------**/
            for(int i = 0 ; i < payNodesList.size() ; i++){
                ServerNodes sn = payNodesList.get(i);
                String syncHost = sn.getProtocol()+sn.getIp()+(StringUtils.isNull(sn.getPort())?"":":"+sn.getPort())+sn.getInterfaceName();
                logger.info("向第" + i + "个支付服[" + syncHost + "]同步数据,内容：" + syncDate.toString());
                String result = HttpUtils.jsonPost(syncHost,syncDate.toString());
                JSONObject jsonRet = JSONObject.fromObject(result);
                logger.info("["+ syncHost + "]接口返回：" + jsonRet.toString());
                SyncStatus syncStatus = new SyncStatus(TimeUtils.getDateDetail(),sn.getName(),sn.getIp(),"[添加/修改]["+sn.getName()+"]配置数据----->[同步]",jsonRet.toString());
                syncStatusService.addSyncStatus(syncStatus);
                if(jsonRet.get("ret").equals(0) && jsonRet.get("msg").equals("success")){
                    //说明支付服第一个节点同步成功，所以不再对其他节点进行同步，现在是需要对所有节点请求重载数据的接口
                    for(int j = 0 ; j < payNodesList.size() ; j++){
                        ServerNodes snj = payNodesList.get(j);
                        String reloadHost = snj.getProtocol()+snj.getIp()+(StringUtils.isNull(snj.getPort())?"":":"+snj.getPort())+snj.getReInterfaceName()+"pf_options";
                        logger.info("请求第" + j + "个支付服[" + reloadHost + "]重载数据");
                        String resultj = HttpUtils.get(reloadHost);
                        logger.info("["+reloadHost + "]接口返回：" + JSONObject.fromObject(resultj).toString());
                        syncStatus = new SyncStatus(TimeUtils.getDateDetail(),snj.getName(),snj.getIp(),"[添加/修改]["+snj.getName()+"]配置数据----->[重载]",JSONObject.fromObject(resultj).toString());
                        syncStatusService.addSyncStatus(syncStatus);
                    }
                    break;
                }
            }

            logger.info("- --- |【新增】向支付服同步完成| --- -");
        }catch(Exception e){
            resultMap.put("result", "操作记录失败");
            logger.error("操作失败",e);
        }
        return resultMap;
    }

    @RequestMapping("/delPfOptions")
    public @ResponseBody Map<String,Object> delPfOptions(Long id){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String operCmd = "delete";
        String table = "pf_options";
        PfOptions pfOptions = pfOptionsService.getPfOptionsById(id);
        try{
            if(pfOptionsService.delPfOptions(id)){
                pfOptions.setValue(Base64.encodeUrlSafe(StringUtils.jsonFormat(pfOptions.getValue())));
                resultMap.put("result", "删除配置成功");
                logger.info("删除配置"+id);
                /**----------------------------------------------------------------------------------------------------------------**/
                /**----------------------------------------------删除之后           向登录服和支付服同步数据-----------------------------------------**/
                /**----------------------------------------------------------------------------------------------------------------**/
                JSONObject jsonObj = JSONObject.fromObject(pfOptions);
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("operCmd", operCmd);
                map.put("table", table);
                map.put("id", id);
                map.put("value", jsonObj);
                JSONObject syncDate = JSONObject.fromObject(map);
                //1.获取所有的节点列表
                List<ServerNodes> serverNodesList = serverNodesService.getServerNodesSync();
                //筛选出登录服节点列表
                List<ServerNodes> loginNodesList = new ArrayList<ServerNodes>();
                //筛选出支付服节点列表
                List<ServerNodes> payNodesList = new ArrayList<ServerNodes>();
                for(int i = 0 ; i < serverNodesList.size() ; i++ ){
                    if(serverNodesList.get(i).getDiff() == 1){
                        loginNodesList.add(serverNodesList.get(i));
                    }else if(serverNodesList.get(i).getDiff() == 2){
                        payNodesList.add(serverNodesList.get(i));
                    }
                }

                logger.info("- --- |【删除】准备向登录服同步配置数据| --- -");

                /**----------------------------------------------------------------------------------------------------------------**/
                /**----------------------------------------------删除之后           向登录服同步数据 ----------------------------------------------**/
                /**----------------------------------------------------------------------------------------------------------------**/
                for(int i = 0 ; i < loginNodesList.size() ; i++){
                    ServerNodes sn = loginNodesList.get(i);
                    String syncHost = sn.getProtocol()+sn.getIp()+(StringUtils.isNull(sn.getPort())?"":":"+sn.getPort())+sn.getInterfaceName();
                    logger.info("向第" + i + "个登录服[" + syncHost + "]同步数据,内容：" + syncDate.toString());
                    String result = HttpUtils.jsonPost(syncHost,syncDate.toString());
                    JSONObject jsonRet = JSONObject.fromObject(result);
                    logger.info("["+ syncHost + "]接口返回：" + jsonRet.toString());
                    SyncStatus syncStatus = new SyncStatus(TimeUtils.getDateDetail(),sn.getName(),sn.getIp(),"[删除]["+sn.getName()+"]配置数据----->[同步]",jsonRet.toString());
                    syncStatusService.addSyncStatus(syncStatus);
                    if(jsonRet.get("ret").equals(0) && jsonRet.get("msg").equals("success")){
                        //说明支付服第一个节点同步成功，所以不再对其他节点进行同步，现在是需要对所有节点请求重载数据的接口
                        for(int j = 0 ; j < loginNodesList.size() ; j++){
                            ServerNodes snj = loginNodesList.get(j);
                            String reloadHost = snj.getProtocol()+snj.getIp()+(StringUtils.isNull(snj.getPort())?"":":"+snj.getPort())+snj.getReInterfaceName()+"pf_options";
                            logger.info("请求第" + j + "个登录服[" + reloadHost + "]重载数据");
                            String resultj = HttpUtils.get(reloadHost);
                            logger.info("["+reloadHost + "]接口返回：" + JSONObject.fromObject(resultj).toString());
                            syncStatus = new SyncStatus(TimeUtils.getDateDetail(),snj.getName(),snj.getIp(),"[删除]["+snj.getName()+"]配置数据----->[重载]",JSONObject.fromObject(resultj).toString());
                            syncStatusService.addSyncStatus(syncStatus);
                        }
                        break;
                    }
                }

                logger.info("- --- |【删除】向登录服同步完成| --- -");


                logger.info("- --- |【删除】准备向支付服同步配置数据| --- -");

                /**----------------------------------------------------------------------------------------------------------------**/
                /**----------------------------------------------删除之后           向支付服同步数据 ----------------------------------------------**/
                /**----------------------------------------------------------------------------------------------------------------**/
                for(int i = 0 ; i < payNodesList.size() ; i++){
                    ServerNodes sn = payNodesList.get(i);
                    String syncHost = sn.getProtocol()+sn.getIp()+(StringUtils.isNull(sn.getPort())?"":":"+sn.getPort())+sn.getInterfaceName();
                    logger.info("向第" + i + "个支付服[" + syncHost + "]同步数据,内容：" + syncDate.toString());
                    String result = HttpUtils.jsonPost(syncHost,syncDate.toString());
                    JSONObject jsonRet = JSONObject.fromObject(result);
                    logger.info("["+ syncHost + "]接口返回：" + jsonRet.toString());
                    SyncStatus syncStatus = new SyncStatus(TimeUtils.getDateDetail(),sn.getName(),sn.getIp(),"[删除]["+sn.getName()+"]配置数据----->[同步]",jsonRet.toString());
                    syncStatusService.addSyncStatus(syncStatus);
                    if(jsonRet.get("ret").equals(0) && jsonRet.get("msg").equals("success")){
                        //说明支付服第一个节点同步成功，所以不再对其他节点进行同步，现在是需要对所有节点请求重载数据的接口
                        for(int j = 0 ; j < payNodesList.size() ; j++){
                            ServerNodes snj = payNodesList.get(j);
                            String reloadHost = snj.getProtocol()+snj.getIp()+(StringUtils.isNull(snj.getPort())?"":":"+snj.getPort())+snj.getReInterfaceName()+"pf_options";
                            logger.info("请求第" + j + "个支付服[" + reloadHost + "]重载数据");
                            String resultj = HttpUtils.get(reloadHost);
                            logger.info("["+reloadHost + "]接口返回：" + JSONObject.fromObject(resultj).toString());
                            syncStatus = new SyncStatus(TimeUtils.getDateDetail(),snj.getName(),snj.getIp(),"[删除]["+snj.getName()+"]配置数据----->[重载]",JSONObject.fromObject(resultj).toString());
                            syncStatusService.addSyncStatus(syncStatus);
                        }
                        break;
                    }
                }
            }
            logger.info("- --- |【删除】向支付服同步完成| --- -");
        }catch(Exception e){
            logger.error("删除失败",e);
        }
        return resultMap;
    }

}
