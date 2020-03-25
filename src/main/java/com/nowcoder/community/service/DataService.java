package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    //日期
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //uv统计ip
    public void recordUV(String ip){
        //key为时间
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        //value存id
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }
    //统计指定日期的uv
    public long calculateUV(Date start,Date end){
        if (start==null||end==null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        //整理日期范围内的key(进行or操作)
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        //calendar里面装start
        calendar.setTime(start);
        //calendar小于等于end，开始循环把key-value存到list
        while(!calendar.getTime().after(end)){
           //得到某一天的key,因为每一天的不同ip的key相同，value不同
            String key =  RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            //日期+1
            calendar.add(Calendar.DATE,1);

        }

        //合并value
        String redisKey = RedisKeyUtil.getUVKey(df.format(start),df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        //返回统计结果
        return  redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //将指定用户记入dau
    public void recordDAU(int userId){
        //key为时间
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        //用setBit方法，在代表某一日的key的usrId位记录
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    //统计指定日期的uv
    public long calculateDAU(Date start,Date end){
        if (start==null||end==null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        //整理日期范围内的key(进行or操作)
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        //calendar里面装start
        calendar.setTime(start);
        //calendar小于等于end，开始循环把key-value存到list
        while(!calendar.getTime().after(end)){
            //得到某一天的key,因为每一天的不同ip的key相同，value不同
            String key =  RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
           //key转成byte数组
            keyList.add(key.getBytes());
            //日期+1
            calendar.add(Calendar.DATE,1);

        }

        //合并value，or运算
        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start),df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }

}
