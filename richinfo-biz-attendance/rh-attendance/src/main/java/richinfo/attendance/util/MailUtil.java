/**
* 文件名：MailUtil.java
* 创建日期： 2017年7月26日
* 作者：     yylchhy
* Copyright (c) 2009-2011 邮箱产品开发室
* All rights reserved.
 
* 修改记录：
* 	1.修改时间：2017年7月26日
*   修改人：yylchhy
*   修改内容：
*/
package richinfo.attendance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 功能描述：
 * 邮件功能工具类
 */
public class MailUtil
{
    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
    private static volatile MailUtil instance;
    
    /**
     * 单例模式
     */
    public static MailUtil getInstance()
    {
        if (null == instance)
        {
            synchronized (MailUtil.class)
            {
                if (null == instance)
                {
                    instance = new MailUtil();
                    return instance;
                }
            }
        }
        return instance;
    }

    
    /**
      * SMTP发送邮件
      * @param subject 标题
      * @param content 邮件内容
      * @param to 收件人邮箱
      * @param path 附件路径
      */
    public boolean send(String subject, String content, String to, String path)
    {
        // smtp服务地址，默认使用139邮箱公网smtp服务smtp.139.com
        String smtp = AttendanceConfig.getInstance()
            .getProperty("attend.smtp.server", "smtp.139.com");
        // 发件人
        String from = AttendanceConfig.getInstance()
            .getProperty("attend.sendMail.from", "rcs_kq@139.com");
        // smtp发送邮件用户名
        final String user = AttendanceConfig.getInstance()
            .getProperty("attend.sendMail.user", "rcs_kq@139.com");
        // smtp发送邮件用户密码
        final String password = AttendanceConfig.getInstance()
            .getProperty("attend.sendMail.pwd", "1q2w3e4r5t");
        try
        {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtp);
            props.put("mail.smtp.auth", "true");
            // smtp用户会话
            Session ssn = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(user, password);
                }
            });
            MimeMessage message = new MimeMessage(ssn);
            // 由邮件会话新建一个消息对象
            InternetAddress fromAddress = new InternetAddress(from);
            // 发件人的邮件地址
            message.setFrom(fromAddress);
            // 设置发件人
            List list = new ArrayList();// 不能使用string类型的类型，这样只能发送一个收件人
            String[] median = to.split(",");// 对输入的多个邮件进行逗号分割
            for (int i = 0; i < median.length; i++)
            {
                list.add(new InternetAddress(median[i]));
            }
            InternetAddress[] toAddress = (InternetAddress[]) list
                .toArray(new InternetAddress[list.size()]);
            // 收件人的邮件地址
            message.addRecipients(Message.RecipientType.TO, toAddress);
            // 设置标题
            message.setSubject(subject);
            // 设置文本邮件内容
            // message.setText(content);
            // 设置发送时间
            message.setSentDate(new Date());
            // 设置html邮件内容
            // 创建一个包含HTML内容的MimeBodyPart
            MimeBodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(content, "text/html; charset=utf-8");
            // 设置附件
            MimeBodyPart body = new MimeBodyPart(); // 附件1
            body.setDataHandler(new DataHandler(new FileDataSource(path)));// ./代表项目根目录下
            // 从path获取文件名
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            body.setFileName(MimeUtility.encodeText(fileName));// 中文附件名，解决乱码
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            MimeMultipart mm = new MimeMultipart();
            mm.addBodyPart(html);
            mm.addBodyPart(body);
            message.setContent(mm);
          //  logger.info("==========smtp={},from={},user={},password={},to={}",smtp,from,user,password,to);
            // 设置发信时间
            Transport transport = ssn.getTransport("smtp");
            transport.connect(smtp, user, password);
            transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
            // transport.send(message);
            transport.close();
            logger.info("邮件发送成功,recvEmail={}", to);
            return true;
        }
        catch (Exception e)
        {
            logger.error("邮件发送失败,recvEmail={},Exception",to, e);
            return false;
        }
    }
    
    /**
     * 检查是否是邮件地址
     * @param recvEmail
     * @return
     */
    public boolean checkEmail(String recvEmail)
    {
        if(AssertUtil.isEmpty(recvEmail)){
            return false;
        }
        String [] emails = recvEmail.split(",");
        for(String email: emails){
            if(!AssertUtil.checkEmail(email)){
                return false;
            }
        }
        return true;
    }
 }
