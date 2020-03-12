package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

        //生成随机字符串
    public static String generateUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //密码加密
    //密码+salt盐值+MD5加密 对提交的密码加密
    public static String md5(String key){
        if(StringUtils.isBlank(key))//判断密码是否为空
        return null;
        //不为空md5加密
        return DigestUtils.md5DigestAsHex(key.getBytes());

    }



}
