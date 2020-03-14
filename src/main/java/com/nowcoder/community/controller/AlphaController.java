package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService s;
    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return s.find();
    }


    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "张弛傻逼";
    }
    //获得请求对象和获取响应对象
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
            //获取请求request数据
        System.out.println(request.getMethod());//获取请求方式
        System.out.println(request.getServletPath());//获取请求路径
        Enumeration<String> enumeration = request.getHeaderNames();//请求行的迭代器，里面有String型的key
        while(enumeration.hasMoreElements()){//这个函数用于打印请求行的key
            String name = enumeration.nextElement();
            String value =request.getHeader(name);//
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));//得到参数
        //返回响应response数据
        response.setContentType("text/html;charset=utf-8");//返回网页类型文本，并且网页支持中文
        try (
                PrintWriter writer = response.getWriter();//输出流
                )
        {

            writer.write("<hl>牛客网</hl>");
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    //GET请求，一般用于获取某些数据。默认为GET请求
    //GET请求 的 参数放在路径的？后面
    @RequestMapping(path="/student",method= RequestMethod.GET)//路径为/student？参数1=...&参数二=...,只处理GET请求
    @ResponseBody
    public String getStudent(
            @RequestParam(name = "cur",required = false,defaultValue = "1") int cur,//在形参前面加上注解，可设置没有传入实参时的初始值
            @RequestParam(name = "limit",required = false,defaultValue = "10")int limit){
        System.out.println(cur+" "+limit);
        return "some student";
    }

    //GET请求 的 把参数直接加在路径中
    @RequestMapping(path="/student/{id}",method= RequestMethod.GET)//路径为/student/参数，只处理GET请求
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //POST请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)//POST请求只要path包括了html表单路径，就可以随便调html的数据
    @ResponseBody
    public String saveStudent(String name,int age){//获取POST请求中的参数的方法：方法的形参和表单中数据的name相同就可以从html传参到方法
       System.out.println(name+" "+age);
        return "成功";//保存成功返回“成功”
    }

    //响应html数据，返回网页,方式一。响应html数据意思就是根据浏览器传入值后，把对应数据显示在浏览器
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){//前端控制器会调用这个方法得到Model和View数据，加到模板引擎后渲染生成动态HTML
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张弛");//模板里需要几个变量，这里addObject几次，这里是写死了
        mav.addObject("age","20");
        mav.setViewName("/demo/view");//设置对象对应的模板的路径和名字，格式为templates的 : /下级目录名/下级文件名
        return mav;
    }
    //不加@ResponseBody，默认返回HTML


    //    响应html数据，返回网页，方式二
    @RequestMapping(path = "/school",method = RequestMethod.GET)//网页路径和请求类型
    public String getSchool(Model model){//固定模板
        model.addAttribute("name","广东工业大学");//模板中需要获取的变量，这里后面加了值就被固定了
        model.addAttribute("age","75");
        return "/demo/view";//return的是对象对应的templates模板的路径和名字
    }
    //响应HTML和响应JSON数据区别：前者需要刷新网页才能得到响应，后者不需要
    //响应JSON数据。
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){//前端控制器发现加了@ResponseBody,会自动把返回的东西转成JSON给浏览器
        Map<String,Object> emp = new HashMap<>() ;
        emp.put("name","张三");
        emp.put("age","18");
        return emp;
    }
    //响应时返回一堆对象
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){//前端控制器发现加了@ResponseBody,会自动把返回的东西转成JSON给浏览器
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>() ;
        emp.put("name","张三");
        emp.put("height","18");
        list.add(emp);
        return list;
    }
    @RequestMapping(path="/cookie/set",method=RequestMethod.GET)
    @ResponseBody//下面参数为httpServletResponse，由于响应要带上cookie
    public String setCookie(HttpServletResponse response){
        //新建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUID());
        //cookie有效的范围
        cookie.setPath("/4399/alpha");
        //设置cookie生存时间
        cookie.setMaxAge(60*10);
        //把cookie搞进response
        response.addCookie(cookie);
        return "你cookie有了";
    }
    @RequestMapping(path="/cookie/get",method=RequestMethod.GET)
    @ResponseBody//下面参数为httpServletResponse，由于响应要带上cookie
    public String getCookie(@CookieValue("code") String code){
       System.out.println(code);
        return "getCookie";
    }
    //session
    @RequestMapping(path="/session/set",method=RequestMethod.GET)
    @ResponseBody//下面参数为httpServletResponse，由于响应要带上cookie
    public String setSession(HttpSession session){
            session.setAttribute("id",1);
            session.setAttribute("name","Test");
            return "Set_Session";
    }

    @RequestMapping(path="/session/get",method=RequestMethod.GET)
    @ResponseBody//下面参数为httpServletResponse，由于响应要带上cookie
    public String getSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "get_Session";
    }
}
