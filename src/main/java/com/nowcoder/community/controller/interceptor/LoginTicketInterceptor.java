package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    //在请求的一开始就获取ticket，用于查找对应user
@Autowired
private UserService userService;

@Autowired
private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从request拿cookie
        String ticket = CookieUtil.getValue(request,"ticket");
        //如果ticket不为null，表示已登录
        if(ticket!=null){
            //查询ticket得到login ticket数据
            LoginTicket loginTicket=userService.findLoginTicket(ticket);
            //检查凭证是否有效.包括不为空，状态为0，还没到过期时间
            if(loginTicket!=null&&loginTicket.getStatus()==0&&loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //得到本次请求中要用到的用户存到hostHolder
                //考虑多个线程隔离,把对象存入ThreadLocalMap,就可以隔离
                hostHolder.setUser(user);

            }
        }

        return true;
    }
    //模板引擎调用前，把要用的对象存进model


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null||modelAndView!=null){
            //如果模板和登录用户不为null，model中装入用户准备给templates
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
