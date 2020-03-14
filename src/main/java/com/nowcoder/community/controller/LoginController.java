package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private Producer kaptchaProducer;

    //项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;
    //处理访问注册页面的请求
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() { //获取注册页面
        return "/site/register";
    }

    //处理访问登录页面的请求
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() { //获取注册页面
        return "/site/login";
    }

    //注册提交数据功能。处理注册请求,有提交数据使用POST
    //这里形参用Model类和User类即可，SpringMVC会把传入内容按照User属性填入user
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {

        Map<String, Object> map = userService.register(user);
        //如果map为空，注册成功。提示注册成功，跳到首页激活
        if (map == null || map.isEmpty()) {
            //传给templates注册成功信息
            model.addAttribute("msg", "注册成功，请到您的邮箱进行激活");
            //跳到主页
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            //失败了传失败信息，跳到到原来的页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    //激活页面
    //  http://localhost:8004/4399/activation/用户ID/激活码
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {

        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            //成功了，跳到登录页面
            //传给templates激活成功信息
            model.addAttribute("msg", "账号激活成功");
            //跳到登录页面
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {

            //传给templates激活重复信息
            model.addAttribute("msg", "该账户已经激活过了！");
            //跳到登录页面
            model.addAttribute("target", "/login");
        } else {
            //传给templates激活重复信息
            model.addAttribute("msg", "激活码错误！");
            //跳到登录页面
            model.addAttribute("target", "/register");

        }

        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成验证码text
        String text = kaptchaProducer.createText();
        //生成图片
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码存入session
        session.setAttribute("kaptcha", text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme, Model model, HttpSession session, HttpServletResponse response) {
        //传入的数据有用户名，密码，验证码，记住我，model，存在session的正确验证码,用于传给cookie的登录凭证

        //验证码对吗
        String kaptcha = (String) session.getAttribute("kaptcha");
        //如果所取验证码或者真正验证码有问题或者两者不相等
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg","验证码不正确!");
            return "/site/login";
        }
        //检查账号，密码
        int expiredSeconds = rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        //如果map里面有ticket项，说明登录成功拉
        if(map.containsKey("ticket")){
            //登陆成功，把成功凭证发给浏览器cookie
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);//cookie生效的地方
            cookie.setMaxAge(expiredSeconds);//生存时间
            response.addCookie(cookie);
            return "redirect:/index";//登录成功，重定向回到index页面
        }
        else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));//发错误信息给templates
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";  //回到登录页面
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        //得到浏览器的cookie
        userService.logout(ticket);
        return "redirect:/login";
    }

}