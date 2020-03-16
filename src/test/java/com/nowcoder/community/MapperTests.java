package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void testSelectUser(){
       User user = userMapper.selectByID(101);
   //     User user = userMapper.selectByName("liubei");
        System.out.println(user);
    }
    @Test
    public void testInsetUser(){
        User user = new User();
        user.setUsername("zhangchi");
        user.setPassword("1660206");
        user.setSalt("gdu");
        user.setEmail("1021848661@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void updateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);
    }
    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPost(149, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
    @Test
    public void testInsetLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicker("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",1);
        loginTicket = loginTicketMapper.selectByTicker("abc");
        System.out.println(loginTicket);

    }
    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for (Message message:list){
            System.out.println(message);
        }
        int i  = messageMapper.selectConversationCount(111);
        System.out.println(i);
        list = messageMapper.selectLetters("111_112",0,10);
        for (Message message:list){
            System.out.println(message);
        }
        i = messageMapper.selectLetterCount("111_112");
        System.out.println(i);
        i  = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(i);


    }

}
