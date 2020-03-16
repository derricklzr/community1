package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    //处理私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        //设置分页信息
        page.setLimit(5);
        //分页路径
        page.setPath("/letter/list");
        //页面数量
        page.setRows(messageService.findConversationCount(user.getId()));
        //得到一串会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        //还差几个信息，继续封装
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {//循环每一页
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                //装入会话
                map.put("conversation", message);
                //装入每个会话的私信数量
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                //装入每个会话的未读消息数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));

                //装入对话的对象的头像.看看当前用户id是不是数据库表里的fromId，是就getToId()，否则getFromId()
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        //把全部会话打包给templates
        model.addAttribute("conversations",conversations);
        //查询未读消息总数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        //装入本用户总的未读消息总数
        model.addAttribute("letterUnreadCount",letterUnreadCount);


        return "/site/letter";
    }

    //用来标记未读的消息
    private List<Integer> getLetterId(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList!=null){
            for (Message message:letterList){
                if (hostHolder.getUser().getId()==message.getToId()&&message.getStatus()==0){
                        ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //封装一个会话中一页的私信列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!=null){//循环每一页
            for (Message message:letterList){
                Map<String,Object> map = new HashMap<>();
                //得到私信内容
                map.put("letter",message);
                //得到对话对象信息
                map.put("fromUser",userService.findUserById(message.getFromId()));
               //打包
                letters.add(map);

            }
        }
        model.addAttribute("letters",letters);
        //查询私信的目标
        model.addAttribute("target",getLetterTarget(conversationId));
        //标记已读
        List<Integer> ids = getLetterId(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }


        return "/site/letter-detail";

    }
    //用于查找私信的对象
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0=Integer.parseInt(ids[0]);
        int id1=Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId()==id0){
            return userService.findUserById(id1);
        }
        else{
            return userService.findUserById(id0);
        }
    }

    //用于发送私信
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);
        if (target==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }
        //准备插入私信数据
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }
        else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }


}
