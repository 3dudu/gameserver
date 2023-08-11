package com.icegame.sysmanage.controller;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.*;
import com.icegame.gm.util.Misc;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.UploadQueue;
import com.icegame.sysmanage.service.IPropertiesService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.IUploadQueueService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * 菜单管理的controller
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/sysmgr/xls")
public class XlsController {

	@Autowired
	private ISlaveNodesService slaveNodesService;

	@Autowired
	private IPropertiesService propertiesService;

	private static Logger logger = Logger.getLogger(XlsController.class);

	private static final Map<String,Object> retMap = new HashMap<String,Object>();

	public String XLS_BASH_DIR;

	private static String XLS_THIS_DIR;

	private static String upload_path ;

	@Autowired
	private IUploadQueueService uploadQueueService;
	//进入菜单查询列表功能
	@RequestMapping("/gotoUploadXls")
	public String gotoUploadXls(Model model){
		return "sysmanage/xls/xls";
	}

	@RequestMapping(value = "/xlsUpload", produces = "application/json;charset=UTF-8")
	public @ResponseBody String uploadFiles(@RequestParam("xlsUpload[]") MultipartFile[] file,String language,String array,String ip,boolean openMd5,String uploadTimeStamp) throws IOException {
		StringBuffer sb = new StringBuffer();
		boolean _thisStatus = false;

		JSONObject retObj = new JSONObject();

		JSONArray md5Arr = JSONArray.fromObject(array);
		JSONObject md5Obj = new JSONObject();

		Map<String,MultipartFile> map = new HashMap<String, MultipartFile>();
		Map<String,Object> logMap = new HashMap<String, Object>();


		for(int i = 0;i < file.length ; i++){
			map.put(file[i].getOriginalFilename(), file[i]);
		}

		// 初始化文件夹
		//XLS_BASH_DIR = "G:" + File.separator;
		XLS_BASH_DIR = propertiesService.getBaseDir();
		String only__dir_id = TimeUtils.getDate();
		XLS_THIS_DIR = XLS_BASH_DIR + language + File.separator + "upload" + File.separator + only__dir_id + "-" + UUID.randomUUID();
		File tempFile = new File(XLS_THIS_DIR);
		if(!tempFile.exists()){
			tempFile.mkdirs();
		}else{
			logger.info("dir exists...");
		}

		// 开始验证表
		logger.info("- --- | upload xls | --- -> ");

		for(int i = 0 ; i < md5Arr.size() ; i++){
			md5Obj = (JSONObject) md5Arr.get(i);
			String filename = md5Obj.getString("name");
			String md5 = md5Obj.getString("md5");

			//如果包含包含file文件，就对比md5
			if(map.containsKey(filename)){

				//把 MultipartFile 转为 File对象 计算md5.计算完成之后删除
				MultipartFile _thisMultFile = map.get(filename);
				File f = new File(XLS_BASH_DIR + only__dir_id);
				FileUtils.copyInputStreamToFile(_thisMultFile.getInputStream(), f);
				String masterMd5 = Misc.getMD5(f);
				String suffix = filename.substring(filename.lastIndexOf(".") + 1);

				f.delete();
				if(suffix.equals("xls")){
					if(md5.equals(masterMd5)){
						try {
							_thisMultFile.transferTo(new File(XLS_THIS_DIR + File.separator + filename));
							_thisStatus = true;
						}catch (Exception e) {
							logger.error(e.toString());
						}
					}
				}

				logMap.put("filename",filename);
				logMap.put("clientMd5",md5);
				logMap.put("masterMd5",masterMd5);
				logMap.put("status",_thisStatus?"success":"failed");
				logger.info("- --- | validating | --- ->" + JSONObject.fromObject(logMap));
			}
		}

		upload_path = XLS_THIS_DIR + File.separator + "xls.zip";

		//上完成后，所有的文件都已经写入了XLS_THIS_DIR，然后把当前目录的所有文件打成一个压缩包，存放在当前目录
		ZipUtil.zipFile(upload_path,ZipUtil.getFiles(XLS_THIS_DIR));
		String serverName = "";
		if(ip.equals("allserver")){
			serverName = "所有服务器";
		}else{
			SlaveNodes sn = slaveNodesService.getSlaveNodesById(Long.valueOf(ip));
			serverName = sn.getName();
		}

		//向任务对列添加一条新的任务
		UploadQueue uploadQueue = new UploadQueue(UserUtils.getCurrrentUserId(),UserUtils.getCurrrentUserName(),String.valueOf(System.currentTimeMillis()),uploadTimeStamp,ip,serverName,language,openMd5?"1":"0","0",upload_path);
		uploadQueueService.addProcess(uploadQueue);

		retMap.put("ret",1);
		retMap.put("msg","添加上传任务成功");
		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("getAllProcessList")
	@ResponseBody
	public String getAllProcessList(int pageNo,int pageSize,String state,String result,String startTime,String endTime){

		/*if(startTime.equals("") && endTime.equals("")){
			startTime = TimeUtils.dateToStampWithDetail(TimeUtils.getStartTime());
			endTime = TimeUtils.dateToStampWithDetail(TimeUtils.getEndTime());
		}else{
			startTime = TimeUtils.checkDate(startTime, endTime).get("startDate");
			endTime = TimeUtils.checkDate(startTime, endTime).get("endDate");
		}*/

		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<UploadQueue> processList = new ArrayList<UploadQueue>();
		UploadQueue uploadQueue = new UploadQueue();uploadQueue.setStartTime(startTime);uploadQueue.setEndTime(endTime);
		if(!state.equals("")){
			uploadQueue.setState(state);
		}
		if(!result.equals("")){
			uploadQueue.setResult(result);
		}
		PageInfo<UploadQueue> pageInfo = uploadQueueService.getAllProcess(uploadQueue,pageParam);
		processList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(processList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("downloadZip")
	public void downLoadFiles(Long processId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			UploadQueue uploadQueue = uploadQueueService.getProcessById(processId);
			if(null == uploadQueue || null == uploadQueue.getFile() || uploadQueue.getFile().equals("")){
				return;
			}
			File file = new File(uploadQueue.getFile());
			if(!file.exists()){
				return;
			}
			response.reset();
			download(file, response);
		} catch (Exception e) {
			logger.error("--- - | UploadQueue download zip error |- --- :" + e.getMessage());
		}
	}

	@RequestMapping("checkDetail")
	public @ResponseBody String checkDetail(Long id){
		retMap.clear();
		UploadQueue uploadQueue = uploadQueueService.getProcessById(id);
		if(null != uploadQueue){
			retMap.put("ret",-1);
			retMap.put("msg","找不到此记录");
		}
		retMap.put("ret",1);
		retMap.put("msg",uploadQueue.getDetail());
		return JSONObject.fromObject(retMap).toString();
	}


	@RequestMapping("deleteProcess")
	public @ResponseBody String deleteProcess(Long id){
		retMap.clear();
		String result = "";
		boolean isInWaiting = uploadQueueService.isInWaiting(new UploadQueue(id));
		if(isInWaiting){
			try{
				UploadQueue uploadQueue = uploadQueueService.getProcessById(id);

				boolean flag = uploadQueueService.delProcess(id);
				//删除任务的同时，还需要删除此次上传的文件
				FileUtils.deleteDirectory(new File(getRealPath(uploadQueue.getFile())));

				if(flag){
					result = "删除成功";
				}else{
					result = "删除失败";
				}
				retMap.put("ret",1);
				retMap.put("msg",result);
			}catch (Exception e){
				logger.error("- --- |deleteProcess failed| --- -" + e.getMessage());
				retMap.put("ret",-1);
				retMap.put("msg","删除失败");
			}
		}else{
			retMap.put("ret",-2);
			retMap.put("msg","无法删除正在执行中和已完成的任务");
		}
		return JSONObject.fromObject(retMap).toString();
	}


	/**
	 * 以流的形式下载文件
	 *
	 * @param file
	 * @param response
	 * @return
	 */
	public static void download(File file, HttpServletResponse response) {
		try {
			ServletOutputStream output = response.getOutputStream();
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);

			// 清空response
			response.reset();

			String downloadFileName =  StringUtils.getDownloadZipName(file.getPath());

			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			//如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downloadFileName, "UTF-8"));
			IOUtils.copy(fis,toClient);
			fis.close();
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			logger.error("--- - | UploadQueue download zip error |- --- :" + e.getMessage());
		}
	}

	public static String getRealPath(String path){
		return path.substring(0,path.lastIndexOf(File.separator));
	}

}
