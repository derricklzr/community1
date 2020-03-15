package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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
    //把服务器要给给浏览器返回的东西弄成JSON类型
    //一般有编码，提示信息，业务信息
    public static  String getJSONString(int code, String msg, Map<String,Object> map){
        //JSON为String,code为编码，msg为提示信息，map为业务数据
        //先把信息封装到JSON对象
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if (map!=null){
            //遍历key
            for (String key:map.keySet()){
                json.put(key,map.get(key));//不为空就以key,value形式装入JSON
            }
        }
        return json.toJSONString();
    }
    //要传给浏览器的数据只有code和msg
    public static  String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }
    //要传给浏览器的数据只有code
    public static  String getJSONString(int code){
        return getJSONString(code,null,null);
    }
/*
    public static void main(String[] args){
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangchi");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }
*/
}
