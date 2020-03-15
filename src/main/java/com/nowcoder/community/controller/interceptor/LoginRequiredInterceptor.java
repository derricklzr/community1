package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

//未登录状态拦截
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    //在请求最初判断有无登录
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            //如果拦截到的是方法,从这个方法的对象取注解
            HandlerMethod handlerMethod =(HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired=method.getAnnotation(LoginRequired.class);
            if(loginRequired!=null&&hostHolder.getUser()==null){
                //如果这个方法是登录才能访问，但是user没登录
                //强制到重定向到首页
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
