package com.icegame.sysmanage.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.utils.RequestUtil;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.gm.util.Misc;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.Notice;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.UploadQueue;
import com.icegame.sysmanage.service.INoticeService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.IUploadQueueService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 注解属性说明:
 * 1.initialDelay 		 :	初次执行任务之前需要等待的时间
 * 2.fixedDelay   		 :	每次执行任务之后间隔多久再次执行该任务
 * 3.fixedRate    		 :	执行频率，每隔多少时间就启动任务，不管该任务是否启动完成
 * 4.cron="0 0 0 4 * * ?" :	每天早上凌晨四点执行方法中的任务
 * 5.cron="0 0/10 * * * ?:  每10分钟执行一次
 * @Description: 定时统计前一天玩家的充值数额  
 * @author chesterccw
 * @date 2018年1月22日
 */

/**
 * Task定时器  检测上传任务
 *
 * @Description: TODO
 * @Package com.icegame.sysmanage.task
 * @author chesterccw
 * @date 2018年1月22日
 */
@Component
public class TaskUtils {
	
	private static final Logger logger = Logger.getLogger(TaskUtils.class);

    @Autowired
    private IUploadQueueService uploadQueueService;

    @Autowired
    private ISlaveNodesService slaveNodesService;

    private static final Map<String,Object> retMap = new HashMap<String,Object>();

    @Scheduled(cron="0 0/5 * * * ?")
    public void uploadXls() throws IOException {

        UploadQueue uploadQueue = null;
        //1.先检查是否有进行中的任务
        List<UploadQueue> processList = uploadQueueService.getInProcess(new UploadQueue());
        if(processList.size() == 0){

            List<UploadQueue> waitList = uploadQueueService.getNextProcess(new UploadQueue());
            if(waitList.size() > 0){
                uploadQueue = waitList.get(0);
                Long id = uploadQueue.getId();

                if(System.currentTimeMillis() >= Long.valueOf(uploadQueue.getUploadTime())){

                    //在上传之前首先要把 当前id的状态修改为进行中
                    uploadQueueService.changeToUploading(new UploadQueue(id));

                    String ip = uploadQueue.getTargetServer();
                    String language = uploadQueue.getLanguage();
                    boolean openMd5 = openMd5(uploadQueue.getOpenMd5());

                    String filePath = uploadQueue.getFile();
                    List<String> resultList = null;
                    String host = "";
                    StringBuffer sb = new StringBuffer();
                    JSONObject retObj = new JSONObject();
                    String result = null;
                    boolean success = false;

                    List<File> uploadFileList = new ArrayList<File>();
                    uploadFileList.add(new File(filePath));

                    if(ip.equals("allserver")){
                        int i = 0;
                        List<SlaveNodes> snList = slaveNodesService.getSlaveNodesListNoPage();
                        for(SlaveNodes sn :snList){
                            i++;

                            host = "http://" + (StringUtils.isNull(sn.getNip())?sn.getIp():sn.getIp()) + ":" +sn.getPort() + "/platform/xlsUpload";
                            resultList = RequestUtil.uploadFiles(host,uploadFileList,language, Misc.getMD5(uploadFileList.get(0)),openMd5);
                            retObj = JSONObject.fromObject(resultList.get(0));
                            if(snList.size() == i){
                                sb.append(sn.getName() + "[" + retObj.getString("msg") + "]");
                            }else{
                                sb.append(sn.getName() + "[" + retObj.getString("msg") + "]").append(",").append("\r\n");
                            }

                            success = retObj.getInt("ret") == 1?true:false;

                        }
                        result = success?"1":"0";
                        retMap.put("msg",sb.toString());
                    }else{
                        SlaveNodes sn = slaveNodesService.getSlaveNodesById(Long.valueOf(ip));
                        host = "http://" + (StringUtils.isNull(sn.getNip())?sn.getIp():sn.getIp()) + ":" +sn.getPort() + "/platform/xlsUpload";
                        resultList = RequestUtil.uploadFiles(host,uploadFileList,language,Misc.getMD5(uploadFileList.get(0)),openMd5);
                        retObj = JSONObject.fromObject(resultList.get(0));
                        sb.append("服务器 " + sn.getName() + " [" + retObj.getString("msg") + "]");

                        result = retObj.getInt("ret") == 1?"1":"0";

                        retMap.put("msg",sb.toString());
                    }
                    //完成之后不管结果如何 当前id的状态修改为完成
                    uploadQueueService.changeToFinished(new UploadQueue(id,result,sb.toString()));

                }
            }

        }

	}

	public boolean openMd5(String openMd5){
        boolean flag = true;
        if(Integer.valueOf(openMd5) == 0){
            return false;
        }
        return flag;
    }


}
