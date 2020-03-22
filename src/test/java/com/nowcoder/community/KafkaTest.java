package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendM("test","caoni");
        kafkaProducer.sendM("test","caoni2");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
@Component
class KafkaProducer{
//发指定消息到指定主题
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendM(String topic,String content){
        kafkaTemplate.send(topic,content);
    }


}
@Component
class KafkaConsumer{
//监听名字为test的主题
    @KafkaListener(topics = {"test"})
    public void handleM(ConsumerRecord record){
        System.out.println(record.value());
    }


}
