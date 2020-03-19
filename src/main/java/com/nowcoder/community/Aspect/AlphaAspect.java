/*
package com.nowcoder.community.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect //切面组件要用此注解
public class AlphaAspect {

    //pointcut切点，切入的位置
    //第一个*代表要切入的方法的返回值类型不限
    //第二段：这个包下的所有类的所有方法，..代表所有参数
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }
    //在连接点之前
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    //在连接点之后
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    //在连接点所处方法有返回值后
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    //在抛出异常时
    @AfterThrowing("pointcut()")
    public void AfterThrowing(){
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint)throws Throwable{
       System.out.println("around之前");
        //调用目标主键的方法
        Object obj = joinPoint.proceed();
        System.out.println("around之后");
        return obj;
    }



}
*/