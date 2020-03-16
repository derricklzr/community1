package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private MessageMapper messageMapper;
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }
    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }
    //用于添加一条私信
    public int addMessage(Message message){
        //过滤铭感词和前端标签
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    //把消息变成已读
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }


}
