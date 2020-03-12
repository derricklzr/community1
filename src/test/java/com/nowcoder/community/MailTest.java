package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {

            mailClient.sendMail("1021848661@qq.com", "jay", "hello");

    }

    @Test
    public void testHtmlMail() {

            Context context = new Context();
            context.setVariable("username","张弛");

            String content = templateEngine.process("/mail/demo",context);
            System.out.println(content);

            mailClient.sendMail("1021848661@qq.com","李宇航给你的一封信",content);


    }
}
