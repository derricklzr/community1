package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//用于表示本类是Controller的全局配置类,annotations表示只扫描带有@controller注解的类
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(Exception.class);

    //该方法会在controller出现异常后被调用，用于处理异常.括号内可设置异常类
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常"+e.getMessage());
        for (StackTraceElement element:e.getStackTrace()){
            //遍历异常的栈的信息
            logger.error(element.toString());
        }
        //区分浏览器的请求方式(同步，异步)，从请求的消息头获取请求方式
        String xRequestedWith =   request.getHeader("x-requested-with");
        if(xRequestedWith.equals("XMLHttpRequest")){
            //异步请求
            response.setContentType("application/plain;charset=utf-8");//响应一个字符串，设定返回类型和编码类型
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!"));//输出流输出错误信息给网页

        }
        else {
                response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
