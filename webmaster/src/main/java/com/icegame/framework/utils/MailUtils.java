package com.icegame.framework.utils;

import java.util.Properties;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 发送邮件工具类
 */
public class MailUtils {

    private static Logger logger = Logger.getLogger(MailUtils.class);

    // 发件人昵称展示
    private static final String FROM = "雪糕游戏-吴志坚";

    // 163企业邮箱smtp
    private static final String HOST = "smtphz.qiye.163.com";

    // 企业邮箱
    private static final String USERNAME = "wuzhijian@xuegaogame.com";

    // 企业邮箱密码
    private static final String PASSWORD = "fEH25H2Pr8fcLJGR";


    public static void sendHtmlMail(String[] to, String subject, String text) {

        try{

            if(to.length == 0){
                logger.info("收件人列表为空...不执行任何操作");
                return;
            }
    
            //设置服务器验证信息
            Properties prop = new Properties();

            prop.setProperty("mail.smtp.auth", "true");

            // 加密端口(ssl)  可通过 https://qiye.163.com/help/client-profile.html 进行查询
            prop.setProperty("mail.smtp.timeout", "994");

            // SSL加密
            MailSSLSocketFactory sf = new MailSSLSocketFactory();

            // 设置信任所有的主机
            sf.setTrustAllHosts(true);

            prop.put("mail.smtp.ssl.enable", "true");

            prop.put("mail.smtp.ssl.socketFactory", sf);
            
            //设置邮件内容
            JavaMailSenderImpl javaMailSend = new JavaMailSenderImpl();

            MimeMessage message = javaMailSend.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");

            //设置昵称
            String nick = MimeUtility.encodeText(FROM);

            // 邮件发送者
            messageHelper.setFrom(new InternetAddress(nick + " <"+USERNAME+">"));

            messageHelper.setTo(to);

            messageHelper.setSubject(subject);

            messageHelper.setText(text, true);
    
            //设置邮件服务器登录信息
            javaMailSend.setHost(HOST);

            javaMailSend.setUsername(USERNAME);

            javaMailSend.setPassword(PASSWORD);

            javaMailSend.setJavaMailProperties(prop);

            //logger.info("maillText：" + text);

            javaMailSend.send(message);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        String[] to ={"515895621@qq.com","chesterccw@163.com"};
        sendHtmlMail(to,"自动开服","<h1>IceGame</h1>");
    }
    
}