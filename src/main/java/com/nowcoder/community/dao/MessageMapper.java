package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //conversation代表会话，即一个对话框
    //letter代表会话里的一条私信信息

    //查询本用户当前页会话数据，针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId,int offset,int limit);
    //查询本用户会话总数量
    int selectConversationCount(int userId);
    //查看某个会话所包含的私信信息列表
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询某个会话所包含的私信信息数量
    int selectLetterCount(String conversationId);
    //某用户总未读私信消息+对于各用户的未读消息
    int selectLetterUnreadCount(int userId,String conversationId);

    //增加一条私信
    int insertMessage(Message message);

    //修改消息的状态，例如未读消息设为已读，删除消息
    int updateStatus(List<Integer> ids,int status);

}
