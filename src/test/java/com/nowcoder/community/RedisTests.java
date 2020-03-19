package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    //通用方法：size()

    @Test
    public void testString(){
        //String类型  opsForValue
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));


    }
    @Test
    public void testHashes(){
        //String类型  opsForValue
        String redisKey = "test:hash";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));


    }
    @Test
    public void testList(){
        //String类型  opsForValue
        String redisKey = "test:list";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        System.out.println(redisTemplate.opsForList().range(redisKey,0,1));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));

    }
    @Test
    public void testSet(){
        //String类型  opsForValue
        String redisKey = "test:set";
        redisTemplate.opsForSet().add(redisKey,"张弛","张宇峰","李宇航");
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));//查看集合元素情况

    }
    @Test
    public void testSortedSet(){
        //String类型  opsForValue
        String redisKey = "test:sortedSet";
        redisTemplate.opsForZSet().add(redisKey,"张弛",20);
        redisTemplate.opsForZSet().add(redisKey,"张宇峰",50);
        redisTemplate.opsForZSet().add(redisKey,"李宇航",21);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));//统计有几个元素
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"张弛"));//查看key的分数
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"李宇航")); //从小到大查看某key排名
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,1)); //从小到大查看某排名范围内的所有key
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

    }
    @Test
    public void testKeys(){
        //String类型  opsForValue
        redisTemplate.delete("test:sortedSet");//删除某个key
        redisTemplate.expire("test:set",10, TimeUnit.SECONDS);//设置key过期剩余时间

    }
    @Test
    public  void testT(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //redis事务语句
                return redisOperations.exec();
            }
        });

    }
}
