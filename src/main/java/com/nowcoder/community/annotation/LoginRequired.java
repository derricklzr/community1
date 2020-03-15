package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//这个注解用来表示某个方法的访问是否需要登录
@Target(ElementType.METHOD)//这个注解写在方法上
@Retention(RetentionPolicy.RUNTIME)//程序运行有效
public @interface LoginRequired {


}
