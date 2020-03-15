package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //用于查当前页面用到的所有帖子的信息，查到的是多条数据，返回list，输入userId查个人的所有帖子，offset起始行行号，limit为每一页显示多少条数据
    List<DiscussPost> selectDiscussPost(int userId,int offset,int limit);
    //查询某个userId发了几个帖子，不输入则查总帖子
    //如果方法只有一个参数，并且在<if>里使用，则必须使用别名
    int selectDiscussPostRows(@Param("userId") int userId);
   //增加帖子
    int insertDiscussPost(DiscussPost discussPost);

    //根据帖子id显示帖子的详情
    DiscussPost selectDiscussPostById(int id);


}
