package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    private final static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    //获取三种主题的JSON消息
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){

        if (record==null||record.value()==null){
            logger.error("消息内容为空");
            return;
        }
        //JSON恢复成Event对象
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            logger.error("消息格式错误");
            return;
        }
        //发站内信
        // from_id永远为1，conversation_id为主题，content为拼出通知的变量
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        //主题
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        //存content
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if (!event.getData().isEmpty())
        for (Map.Entry<String,Object> entry:event.getData().entrySet()){
            content.put(entry.getKey(),entry.getValue());
        }
        //把content以JSON格式存到message.content
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //消费发帖事件，把发帖和给帖子的回复传给ES
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){

        if (record==null||record.value()==null){
            logger.error("消息内容为空");
            return;
        }
        //JSON恢复成Event对象
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            logger.error("消息格式错误");
            return;
        }
        //消息没问题，从事件得到帖子ID并查到帖子，存到ES
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
}
