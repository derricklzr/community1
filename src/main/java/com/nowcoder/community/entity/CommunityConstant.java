package com.nowcoder.community.entity;

//常量接口
public interface CommunityConstant {
    int ACTIVATION_SUCCESS=0;
    int ACTIVATION_REPEAT=1;
    int ACTIVATION_FAILURE =2;

    //默认状态的登陆凭证持续时间
    int DEFAULT_EXPIRED_SECONDS=3600*12;
    //按了”记住我“的登陆凭证持续时间
    int REMEMBER_EXPIRED_SECONDS=3600*24*100;

    //实体类型：帖子
    int ENTITY_TYPE_POST=1;
    //实体类型：评论
    int ENTITY_TYPE_COMMENT=2;
    //实体类型：用户
    //实体类型：评论
    int ENTITY_TYPE_USER=3;

    //主题的评论
    String TOPIC_COMMENT = "comment";
    //主题的赞
    String TOPIC_LIKE = "like";
    //主题的关注
    String TOPIC_FOLLOW = "follow";
    //系统用户id
    int SYSTEM_USER_ID = 1;
    //ES发帖
    String TOPIC_PUBLISH = "publish";
    //ES删帖
    String TOPIC_DELETE = "delete";
    //普通用户
    String AUTHORITY_USER = "user";
    //管理员
    String AUTHORITY_ADMIN = "admin";
    //版主
    String AUTHORITY_MODERATOR = "moderator";
}
