package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    //开启日志
    public static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    @Autowired
    private JavaMailSender mailSender;

    //A发邮件，B收邮件，邮件标题和内容


    //A发邮箱
    @Value("${spring.mail.username}")
    private String from;

    //发邮箱方法
    //发给谁，邮件标题，邮件内容
    public void sendMail(String to,String subject,String content)  {

        try {
            //helper是用于帮助构建message中内容的类
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);//设置发件人
            helper.setTo(to);//设置收件人
            helper.setSubject(subject);//设置邮件标题
            helper.setText(content,true);//设置邮件内容，支持发送html
            mailSender.send(helper.getMimeMessage());//发送邮件
        } catch (MessagingException e) {
            logger.error("发送邮件失败"+e.getMessage());
        }
    }
}
