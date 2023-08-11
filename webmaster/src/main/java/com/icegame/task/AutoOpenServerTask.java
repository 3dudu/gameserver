package com.icegame.task;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.controller.ServerListController;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.service.*;
import com.icegame.sysmanage.service.impl.AutoOpenServerService;
import com.icegame.sysmanage.service.impl.LogService;
import net.sf.json.JSONObject;
import net.sf.jsqlparser.schema.Server;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 注解属性说明:
 * 1.initialDelay 		 :	初次执行任务之前需要等待的时间
 * 2.fixedDelay   		 :	每次执行任务之后间隔多久再次执行该任务
 * 3.fixedRate    		 :	执行频率，每隔多少时间就启动任务，不管该任务是否启动完成
 * 4.cron="0 0 0 4 * * ?" :	每天早上凌晨四点执行方法中的任务
 * 5.cron="0 0/10 * * * ?:  每10分钟执行一次
 */

/**
 *
 * @Description: TODO
 * @Package com.icegame.sysmanage.task
 * @author chesterccw
 * @date 2019年5月16日
 */
@Component
public class AutoOpenServerTask {

	private static final Logger logger = Logger.getLogger(AutoOpenServerTask.class);

	@Autowired
    private IAutoOpenServerService autoOpenServerService;

	@Autowired
    private IServerListService serverListService;

    @Autowired
    private LogService logService;

    @Autowired
    private ISlaveNodesService slaveNodesService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IPropertiesService propertiesService;


    List<Email> emailList = new ArrayList<>();

    String subject = "";

    String[] to = null;


    /**
     * 1. 查询最新区服的注册人数，每隔开 5 分钟检测一次，如果大于1000 并且存在下一个区服的时候，则打开下一个区服
     * 2. 判断设定的slave的区服数量，并且还要有一个阈值，加入slave的区服数量是100，加入已经在这个slave开了80个区服
     *    还剩20个的时候就要告警，此时阈值就 = 20
     */
    @Scheduled(cron="0 0/1 * * * ?")
    public void autoOpenServer(){
        logger.info("================ 进入自动开服定时器... =============");
        List<AutoOpenServer> aoServerList = autoOpenServerService.getAutoOpenServerList();

        if(aoServerList.size() <= 0){
            logger.info("没有检测到自动开服的配置...退出定时器...");
            return;
        }

        // 如果有一个区服正在维护中，那么停止自动开服操作
        if(serverListService.existsMainTainServer()){
            logger.info("当前有区服正在进行维护...退出定时器...");
            return;
        }

        emailList = emailService.getAutoOpenServerEmailList(new Email());

        // 主题
        subject = propertiesService.getProjectName() + "开服情况统计";
        // 收件人
        to = getEmailArray(emailList);
        //内容
        StringBuffer sb = new StringBuffer();

        boolean canSendMail = false;

        for(AutoOpenServer autoOpenServer : aoServerList){

            StringBuffer tempSb = new StringBuffer();

            try {

                String channel = autoOpenServer.getKey();

                boolean isEnableAutoOpenServer = autoOpenServer.getStatus().equals("1") ? true : false;

                String currentChannel = "当前channel[" + channel + "]";

                // 判断开关是否打开
                if(!isEnableAutoOpenServer){
                    logger.info(currentChannel + "没有开启自动开服...continue...");
                    continue;
                }

                logger.info("执行" + currentChannel + "自动开服操作...");

                // 判断当前channel 是否存在于 serverlist中
                if(!serverListService.existsChannel(channel)){
                    logger.info(currentChannel + "在serverlist中不存在...continue...");
                    continue;
                }

                logger.info("开始检测渠道：" + channel);
                if(channel.length() <= 0){
                    logger.info(currentChannel + "不符合规范...continue...");
                    continue;
                }

                Long currentTime = System.currentTimeMillis();

                AutoOpenServer aos = new AutoOpenServer(channel,currentTime);


                if(aos == null){
                    logger.info(currentChannel + "无客户端可见区服，要使用自动开服，当前渠道必须要有客户端可以看到的区服...continue...");
                    continue;
                }


                List<ServerList> openServerList;

                /**
                 * 这边的查询条件是：
                 * 1 开服时间大于当前时间 渠道等于传入渠道 （还没有开服的区服）
                 *    or
                 * 2 开服时间小于当前时间 同步失败 渠道等于传入渠道（开服失败的区）
                 */


                openServerList = serverListService.getServerByChannelAndCreateTime(new ServerList(channel,String.valueOf(currentTime)));

                if(openServerList.size() <= 0){
                    logger.info(currentChannel + "的待开启区服列表为空...continue...");
                    continue;
                }


                // 查询符合当前渠道 并且 开服时间小于当前服务器时间 并且 同步成功(is_success = 1)的区服 的最大的serverId
                // 也就是客户端可见的最大serverId
                Long serverId = autoOpenServerService.getMaxId(aos);

                if(serverId == null){
                    logger.info(currentChannel + "的最大的serverId等于null...continue...");
                    continue;
                }

                logger.info(currentChannel + "的客户端可见的最大区服id是：" + serverId);

                // 当前区服的玩家数量
                int playerCount = autoOpenServerService.getPlayerCount(new AutoOpenServer(serverId));

                logger.info("此区服[serverId=" + serverId + "]的当前玩家数量：" + playerCount);

                // 预设开新服玩家数
                int preValue = Integer.valueOf(autoOpenServer.getValue());

                // 如果大于预设的人数 并且 开关打开 就 查找下一个区服 自动开服
                if(playerCount >= preValue){

                    // 能走到这边，说明可以发送邮件
                    canSendMail = true;

                    ServerList openServer  = openServerList.get(0);

                    if(openServer == null){
                        logger.info(currentChannel + "的[openServer]对象为空...continue...");
                        continue;
                    }

                    // 这一步很重要，必须要设置开服时间为当前时间，否则是无法开服的
                    openServer.setBeginTime(TimeUtils.stampToDateWithMill(currentTime.toString()));
                    // 还需要设置一下结束时间，因为手动添加的的时间是 日期格式的，可以添加，但此处是从数据库读取的数据，格式是时间戳，再次转化的时候就有问题了
                    openServer.setPassTime(TimeUtils.stampToDateWithMill(openServer.getPassTime()));
                    logger.info("设置区服开服时间为当前服务器时间... 完成");


                    openServer.setIsAuto(1);
                    logger.info("设置类型为自动开服... 完成");


                    // 自动开服操作 操作之前先判断是否可以访问接口
                    SlaveNodes slaveNodes = slaveNodesService.getSlaveNodesById(openServer.getSlaveId());

                    String hosts = "http://"+ ( StringUtils.isNull(slaveNodes.getNip())?slaveNodes.getIp():slaveNodes.getNip() )+":" + slaveNodes.getPort() + "/sync/addserver";

                    logger.info("开始检测同步接口：" + hosts);

                    String request = HttpUtils.get(hosts);

                    JSONObject response = JSONObject.fromObject(request);

                    logger.info("同步接口响应：" + response.toString());

                    if(request == null || response.size() == 0){
                        logger.info("请求失败：" + hosts + "，跳过" + currentChannel + "自动开服");
                        subject = getSubject("[自动开服][Error]");
                        sb.append(getOpenServerContent(tempSb, openServer, "<span style='color:#ff3300;'>请求" + hosts + "失败，跳过" + currentChannel + "自动开服</span>", channel));
                        continue;
                    }

                    if(response.optString("ret","").equals("-1")){

                        logger.info("接口响应正常，执行开服同步操作...");

                        logger.info(" 开始同步 | 同步区服信息：" + JSON.toJSON(openServer).toString());

                        Map<String,Object> result =  serverListService.syncServer(openServer);

                        logger.info(" 开服结果：" + JSON.toJSON(result).toString());

                        String openServerResult = JSONObject.fromObject(result).optString("result","").equals("操作成功") ? "自动开服成功" : "<span style='color:#ff3300;'>自动开服失败</span>";

                        subject = getSubject("[自动开服][Success]");

                        sb.append(getOpenServerContent(tempSb, openServer, openServerResult, channel));

                        logger.info("================ 自动开服 执行完成，退出定时器...============= ");
                    } else {
                        subject = getSubject("[自动开服][Error]");
                        sb.append(getOpenServerContent(tempSb, openServer, "<span style='color:#ff3300;'>自动开服失败，同步接口访问失败</span>", channel));
                        logger.info("接口访问失败...continue...");
                    }
                }else{
                    logger.info("当前玩家数：" + playerCount + "，小于预设的开新服玩家数：" + preValue + "，不执行开新服操作，退出当前定时器...");
                }
            }catch (Exception e){
                logger.error("自动开服异常",e);
                MailUtils.sendHtmlMail(to, "[Error]自动开服异常", "<span style='color:#ff3300;'>自动开服异常,请立即处理！</span>");
            }

        }

        if(canSendMail){
            sendMail(sb);
        }

    }

    public void sendMail(StringBuffer sb){
        // 如果开关打开，就发送邮件进行提醒
        if(autoOpenServerService.isTipsStatusOpen(new AutoOpenServer())){
            MailUtils.sendHtmlMail(to,subject,sb.toString());
        }
    }

    public String getOpenServerContent(StringBuffer sb, ServerList openServer, String result, String channel){
        sb.append("<span style='font-size:24px;color:#000;font-weight:bold;'>[channel: " + channel + "]</span>").append("<br />");
        sb.append("============= 新开区服信息 =============").append("<br />");
        sb.append("区服ID : ").append(openServer.getServerId()).append("<br />");
        sb.append("区服名称 : ").append(openServer.getName()).append("<br />");
        sb.append("区服状态 : ").append(result).append("<br />");
        sb.append("<br />");
        sb.append(getSlaveInfoContext(channel));
        return sb.toString();
    }

    public String getSubject(String flag){
        return flag + propertiesService.getProjectName() + "开服情况统计";
    }

    public String[] getEmailArray(List<Email> emailList){
        String[] emailArray = new String[emailList.size()];
        for(int i = 0 ; i < emailList.size() ; i++){
            emailArray[i] = emailList.get(i).getEmail();
        }
        return emailArray;
    }

    /**
     * 每隔两小时检测一次slave情况
     *
     * -------------------------------------
     * @date 2019-06-11 11:27:46
     * @author  wuzhijian
     * 新需求：考虑到邮件数量会太多，暂时去掉两小时一次的salve检测
     * -------------------------------------
     */
//    @Scheduled(cron="0 0 0/2 * * ?")
//    public void checkServer(){
//        logger.info("开始统计Slave区服数量...");
//
//        emailList = emailService.getAutoOpenServerEmailList(new Email());
//
//        // 主题
//        subject = getSubject("[Info]");
//
//        // 收件人
//        to = getEmailArray(emailList);
//
//        StringBuffer sb = getSlaveInfoContext();
//
//        // 如果开关打开，就发送邮件进行提醒
//        if(autoOpenServerService.isTipsStatusOpen(new AutoOpenServer())){
//            MailUtils.sendHtmlMail(to,subject,sb.toString());
//            logger.info("邮件发送成功...");
//        }else{
//            logger.info("发送邮件开关关闭...不进行发送操作...退出定时器...");
//        }
//
//    }

    /**
     * 每隔一小时检测一次推荐服情况
     * @date 2019-07-22 20:13:30
     * @author  wuzhijian
     */
    @Scheduled(cron="0 0 0/1 * * ?")
    public void checkSuggest(){
        logger.info("开始检查推荐区服情况...");

        emailList = emailService.getAutoOpenServerEmailList(new Email());

        // 主题
        subject = getSuggestSubject("[推荐服][Warning]");

        // 收件人
        to = getEmailArray(emailList);

        StringBuffer sb = new StringBuffer();

        List<ServerList> serverList = serverListService.getAllChannelList();

        for(ServerList server : serverList){

            String channel = server.getChannel();

            List<ServerList> serverList1 = serverListService.getSuggestServerInChannel(channel);
            if(serverList1.size() > 0){
                sb.append("<span style='font-size:24px;color:#000;font-weight:bold;'>[channel: " + channel + "]</span>").append("<br />");
                sb.append("推荐服个数：" + serverList1.size()).append("<br />");
                sb.append("详细信息：").append("<br />");
                for(ServerList server1 : serverList1){
                    sb.append("区服ID：").append(server1.getServerId()).append("，").append("区服名称：").append(server1.getName()).append("<br />");
                }
                sb.append("<br />").append("<br />");
            }

        }

        if(StringUtils.isNotNull(sb.toString())){
            String currentTime = TimeUtils.stampToDateWithMill(String.valueOf(System.currentTimeMillis()));
            sb.append("<br />");
            sb.append("当前查询时间 : ").append(currentTime).append("<br />").append("<br />").append("<br />").append("<br />");
            MailUtils.sendHtmlMail(to,subject,sb.toString());
        }else{
            logger.info("没有推荐区服...continue...");
        }

    }

    public String getSuggestSubject(String flag){
        return flag + propertiesService.getProjectName() + "推荐服配置情况";
    }

    /**
     * 获取发送邮件的text
     * @return
     */
    public StringBuffer getSlaveInfoContext(String channel){

        StringBuffer sb = new StringBuffer();

        try{
            String text = "";

            boolean isLastTd = false;

            boolean isWarning = false;

            StringBuffer warnText = new StringBuffer();

            List<String> trList = new ArrayList<>();

            List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();

            // 已开的列表
            List<ServerList> openServerList = null;

            // 待开服列表
            List<ServerList> closeServerList = null;

            AutoOpenServer slimit = autoOpenServerService.getAutoOpenServerTipsSlimit(new AutoOpenServer());

            // 设定的告警数
            int setValue = Integer.valueOf(slimit.getValue());

            Long curTime = System.currentTimeMillis();

            List<ServerList> serverListList = new ArrayList<>();

            for(int i = 0 ; i < slaveNodesList.size() ; i++){

                SlaveNodes sn = slaveNodesList.get(i);

                serverListList = serverListService.getServerBySlaveId(sn.getId());

                if(i == slaveNodesList.size()-1){
                    isLastTd = true;
                }

                closeServerList = getServer(serverListList,true, curTime, channel);

                openServerList = getServer(serverListList,false, curTime, channel);

                // 如果待开启的区服列表数量小于设定值，就需要 重点 显示
                if(closeServerList.size() < setValue){
                    warnText.append(sn.getName()).append("的<span style='color:#ff3300'>").append(" 预备服数[" + closeServerList.size()  + "]</span>，小于设定值[" + setValue + "]").append("，请及时添加新区服！").append("<br />");
                    isWarning = true;
                    subject = getSubject("[自动开服][Warning]");
                } else {
                    isWarning = false;
                }

                String tr = WebUtils.drawTr(sn.getName(),openServerList.size(),closeServerList.size(), serverListList.size(), sn.getLimit(),i+1, isLastTd, isWarning);
                trList.add(tr);
            }

            text = WebUtils.draw(trList);
            sb.append("============= Slave节点信息 =============");
            sb.append(text).append("<br />");
            sb.append(warnText).append("<br />");
            String currentTime = TimeUtils.stampToDateWithMill(String.valueOf(System.currentTimeMillis()));

            sb.append("当前查询时间 : ").append(currentTime).append("<br />").append("<br />").append("<br />").append("<br />");

            return sb;
        }catch (Exception e){
            logger.error("getSlaveInfoContext异常", e);
        }
        return sb;
    }

    /**
     *
     * @param serverList
     * @param flag true 待开服的 false已经开服的
     * @return
     */
    public List<ServerList> getServer(List<ServerList> serverList, boolean flag, Long currentTime, String channel){

        List<ServerList> serverLists = new ArrayList<>();

        for(ServerList server : serverList){
            if(flag){
                if(Long.valueOf(server.getBeginTime()) > currentTime && server.getChannel().equals(channel)){
                    serverLists.add(server);
                }
            }else{
                if(Long.valueOf(server.getBeginTime()) <= currentTime && server.getChannel().equals(channel)){
                    serverLists.add(server);
                }
            }
        }

        return  serverLists;
    }

}
