package com.nowcoder.community.util;

//用于得到某个实体在Redis的key
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    //各个key的前缀
    private static final String PREFIX_ENTITY_LIKE="like:entity";

    //构建赞的数据结构：set
    //key:某个实体的赞名以 like:entity:entityType:entityId
    //value:对这个赞  点赞了的UserId


    //把帖子和回复都称为实体
    //把实体类型和实体id传入,得到这个赞的key名字
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }



}
