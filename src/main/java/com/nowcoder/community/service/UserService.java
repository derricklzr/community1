package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    public UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Value("${community.path.domain}")
    private String domain;  //注入域名
    @Value("${server.servlet.context-path}")
    private String contextPath;//注入项目名


    public User findUserById(int id){
        return userMapper.selectByID(id);


    }

    //个人设置修改密码功能
    public void updatePassword(User user){
        Map<String,Object> map =new HashMap<>();

    }



    //注册功能包括了空账号异常，
    //以及提交表单时用户名，密码，邮箱为空时用map对象打包错误信息返回提示浏览器
    //如果提交数据无错误并且用户名，邮箱未有注册，map将为null。设置新用户相关参数并且发送激活邮件
    public Map<String,Object> register(User user){
        Map<String,Object> map =new HashMap<>();
        //空值处理
        if(user==null)
            throw new IllegalArgumentException("参数不能为空");
        //输入表单的用户名，密码，邮箱是否为空，为空出问题
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //判断用户名或者邮箱是否已存在
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }
        //上述验证都通过，可以进行用户注册
        //生成五位的随机salt
        user.setSalt(CommunityUtil.generateUID().substring(0,5));
        //对密码+salt
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //用户都是普通用户，0
        user.setType(0);
        //状态默认0，未激活
        user.setStatus(0);
        //发送随机激活码
        user.setActivationCode(CommunityUtil.generateUID());
        //设置随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        //用户创建时间
        user.setCreateTime(new Date());
        //用户添加到库中
        userMapper.insertUser(user);
        //定义邮件对象context,往里面设置邮件内容+激活路径
        Context context = new Context();
        //在context中注入了要发送给用户的邮件并命名为email
        context.setVariable("email",user.getEmail());
        //设置激活的url,服务器用这个url处理激活请求。http:localhost:8004/4399/用户ID/激活码
        String url = domain + contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode(); //domain域名+contextPath项目名+固定字符串+用户id+激活码
        context.setVariable("url",url);
        //利用模板引擎生成HTML邮件内容
        String content = templateEngine.process("/mail/activation",context);
        //发送邮件

            mailClient.sendMail(user.getEmail(),"激活账户",content);



        return map;
    }

    //激活方法，包括判断了是否激活，重复激活，激活失败。并在为激活+激活码正确情况下激活用户
    //返回激活状态
    public int activation(int userId,String code){
        User user=userMapper.selectByID(userId);
        if(user.getStatus()==1)//用户已激活过了
        {
            return ACTIVATION_REPEAT;
        }
        //如果验证码确认无误
        else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        else{
            return ACTIVATION_FAILURE;
        }
    }

    //由于登录的时候，失败的原因有多个：
    //因此设置map对象打包错误信息返回给controller给浏览器
    public Map<String,Object> login(String username,String password,int expiredSeconds)
    {//返回类型map带错误信息，形参为用户名，密码和登录凭证还有几秒过期
        Map<String,Object> map = new HashMap<>();
        //用户名和密码为空判断
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码为空！");
            return map;
        }
        //验证用户名是否存在
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","账号不存在！");
            return map;
        }
        //验证账户是否激活
        if(user.getStatus()==0){
            map.put("usernameMsg","账号未激活！");
            return map;
        }
        //验证密码
        password= CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","输入密码错误！");
            return map;
        }
        //执行到此，登录成功

        //生成登录凭证，表示在线.实际为生成了一行LoginTicket表数据
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        //给服务器发登录凭证，以便给浏览器cookie保存
        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }
    //通过ticket找login ticket
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicker(ticket);
    }

    //更新用户头像.传入用户ID和头像新的路径，更新
    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }
}

