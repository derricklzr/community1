package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    //根据ID查询用户
    User selectByID(int id);
    //根据名字查询用户
    User selectByName(String username);
    //Email查询用户
    User selectByEmail(String email);
    //增加用户，返回插入行数
    int insertUser(User user);
    //修改User状态Status，返回修改的条数，修改了几条
    int updateStatus(int id,int status);
    //更新头像路径
    int updateHeader(int id,String headerUrl);
    //更新密码
    int updatePassword(int id,String password);


}
