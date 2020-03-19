package com.nowcoder.community.controller;

import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;



    //下面为处理请求的方法，首页为路径为/index，这个方法响应的是网页不用写ResponseBody
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page)//通过model携带数据给模板，返回视图的名字
    {
        //方法调用前，model和page对象生成，并且page注入model中
        //因此不用在model中add page，就可以在thymeleaf直接访问page的数据
        //总行数
        page.setRows(discussPostService.findDiscussPostRows(0));
        //当前页面访问路径
        page.setPath("/index");



        //查前十条数据
        List<DiscussPost> list= discussPostService.findDiscussPost(0,page.getOffset(),page.getLimit());
        //把每个DiscussPost的用户名通过UseId查出来
        List<Map<String,Object>> discussPosts = new ArrayList<>();//用于封装DiscussPost和User类的对象
        if(list!=null)//遍历
            for(DiscussPost post:list){
                Map<String,Object> map = new HashMap<>();//装入Map
                map.put("post",post);//装入post
                User user = userService.findUserById(post.getUserId());//找出帖子对应的User信息
                map.put("user",user);

                //查帖子的赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        model.addAttribute("discussPosts",discussPosts);//把给页面展现的结果装到model
        return "/index";//返回的是模板的路径
    }

    //服务器发生错误异常对应模板和页面
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}
