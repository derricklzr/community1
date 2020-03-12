package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
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
public class UserService {
    @Autowired
    public UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("$(community.path.domain)")
    private String domain;  //注入域名
    @Value("$(server.servlet.context-path)")
    private String contextPath;//注入项目名


    public User findUserById(int id){
        return userMapper.selectByID(id);


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
        //设置激活的url,服务器用这个url处理激活请求。http://localhost:8004/4399/activation/用户ID/激活码
        String url = domain + contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode(); //domain域名+contextPath项目名+固定字符串+用户id+激活码
        context.setVariable("url",url);
        //利用模板引擎生成HTML邮件内容
        String content = templateEngine.process("/mail/activation",context);
        //发送邮件

            mailClient.sendMail(user.getEmail(),"激活账户",content);



        return map;
    }


}

