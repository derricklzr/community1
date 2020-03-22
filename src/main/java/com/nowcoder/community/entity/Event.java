package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

//消息队列事件实体类
public class Event {
    //主题
    private String topic;
    //事件触发人
    private int userId;
    //事件发生的实体类型
    private int entityType;
    //事件发生的实体ID
    private int entityId;
    //实体的所属作者
    private int entityUserId;
    //额外的数据
    private Map<String,Object> data = new HashMap<>();

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

        public Event setData(String key,Object value) {
        this.data.put(key,value);
        return this;
    }
}
