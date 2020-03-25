package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    public UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Value("${community.path.domain}")
    private String domain;  //注入域名
    @Value("${server.servlet.context-path}")
    private String contextPath;//注入项目名


    public User findUserById(int id){
 //       return userMapper.selectByID(id);
        User user = getCache(id);
        if (user==null){
           user =  initCache(id);
        }
        return user;
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
            clearCache(userId);
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

        //生成登录凭证，表示在线.实际为生成了一行LoginTicket表数据
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());

        loginTicket.setTicket(CommunityUtil.generateUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
       // loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());

        redisTemplate.opsForValue().set(redisKey,loginTicket);

        //给浏览器发登录凭证，以便给浏览器cookie保存
        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket =(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket,3600,TimeUnit.SECONDS);

        //loginTicketMapper.updateStatus(ticket,1);
    }
    //通过ticket找login ticket
    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }





    //更新用户头像.传入用户ID和头像新的路径，更新
    public int updateHeader(int userId,String headerUrl){

     //   return userMapper.updateHeader(userId,headerUrl);
       int rows = userMapper.updateHeader(userId,headerUrl);
       clearCache(userId);
       return rows;
    }

    //个人设置修改密码功能
    public Map<String,Object> updatePassword(String password,String newPassword,int id){
        Map<String,Object> map =new HashMap<>();
        User user = userMapper.selectByID(id);
        password= CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","输入密码错误！");
            return map;
        }
        else {
            newPassword= CommunityUtil.md5(newPassword+user.getSalt());
            clearCache(user.getId());
            userMapper.updatePassword(id,newPassword);
        }

        return map;
    }
    //通过用户名找user
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    //使用redis缓存User信息步骤
    //1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.缓存中取不到，则在缓存中初始化数据
    private User initCache(int userId){
        User user = userMapper.selectByID(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        //缓存存在一小时
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据变更时清除缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    //用于security查某个user的权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }

            }
        });
        return list;
    }
}

