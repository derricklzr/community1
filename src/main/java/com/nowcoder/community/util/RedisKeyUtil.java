package com.nowcoder.community.util;

//用于得到某个实体在Redis的key
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    //各个key的前缀
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    //用户的赞开头
    private static final String PREFIX_USER_LIKE="like:user";

    //定义关注者和被关注者前缀。A关注B时，A以followee为key把自己存入；B以follower为key把A存入
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST="post";

    //构建赞的数据结构：set
    //key:某个实体的赞名以 like:entity:entityType:entityId
    //value:对这个赞  点赞了的UserId

    //得到赞的实体在redis的key
    //把帖子和回复都称为实体
    //把实体类型和实体id传入,得到这个赞的key名字
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //某个用户获得的赞在redis的key
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    //Followee在redis的key
    //某个用户关注的东西（用户，帖子等）的结构:zset结果
    //key       followee:userId:entityType
    //value     (entityId,nowDate)
    public static String getFolloweeKey(int userId,int entityType){
     //需要用户的id和关注的东西的类型
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //Follower在redis的key
    //某个东西拥有的粉丝的结构：zset
    //key   follower:entityType
    //value (entityId,nowDate)
    public static String getFollowerKey(int entityType,int entityId){
        //需要本实体的类型以及关注此实体的用户Id
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //拼验证码的key
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;

    }

    //拼登录凭证的key
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;

    }

    //拼用户的key
    public static String getUserKey(int userId){
        return PREFIX_TICKET+SPLIT+userId;

    }

    //返回单日uv
    public static String getUVKey(String date){
        return PREFIX_UV+SPLIT+date;
    }
    //返回某段日期的的uv
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV+SPLIT+startDate+SPLIT+endDate;
    }
    //返回某一天的dau
    public static String getDAUKey(String date){
        return PREFIX_DAU+SPLIT+date;
    }
    //返回某段日期的dau
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU+SPLIT+startDate+SPLIT+endDate;
    }
    //统计帖子分数的key
    public static String getPostScoreKey(){
        return PREFIX_POST+SPLIT+"score";
    }


}
