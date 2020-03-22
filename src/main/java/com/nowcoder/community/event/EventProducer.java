package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件(发送消息到消息队列)
    public void fireEvent(Event event){
        //将事件以JSON格式发布到指定主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

    }
}
