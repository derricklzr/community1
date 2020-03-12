package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    //处理访问注册页面的请求
    @RequestMapping(path = "/register",method = RequestMethod.GET)
        public String getRegisterPage(){ //获取注册页面
            return "/site/register";
        }

        //处理注册请求,有提交数据使用POST

    //这里形参用Model类和User类即可，SpringMVC会把传入内容按照User属性填入user
    @RequestMapping(path="/register",method = RequestMethod.POST)
    public String register(Model model, User user){

        Map<String,Object> map  = userService.register(user);
        //如果map为空，注册成功。提示注册成功，跳到首页激活
        if(map==null||map.isEmpty()){
            //传给templates注册成功信息
            model.addAttribute("msg","注册成功，请到您的邮箱进行激活");
            //跳到主页
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            //失败了传失败信息，跳到到原来的页面
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }


}
