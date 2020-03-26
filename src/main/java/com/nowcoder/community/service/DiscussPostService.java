package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    public static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);
    //暂时service和dao层功能一样
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //caffeine核心接口：Cache,LoadingCache同步缓存,AsyncLoadingCache

    //帖子列表的缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;
    //帖子总数的缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子列表的缓存，帖子总数的缓存
        postListCache= Caffeine.newBuilder()
                .maximumSize(maxSize)//缓存最大数据量
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)//缓存存在时间
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override   //load方法用于线程在当前缓存找不到数据时，接下来进行的操作
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        //查数据库数据
                        if (key==null||key.length()==0){
                            throw new IllegalArgumentException("参数错误！");
                        }
                        //解析key
                        String[] params = key.split(":");
                        if (params==null||params.length!=2){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        //这里可以插入二级缓存

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        logger.debug("load post list from DB");
                        return discussPostMapper.selectDiscussPost(0,offset,limit,1);
                    }
                });
        postRowsCache= Caffeine.newBuilder()
                .maximumSize(maxSize)//缓存最大数据量
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)//缓存存在时间
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows list from DB");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPost(int userId,int offset,int limit,int orderMode){

        //缓存热帖
        if (userId==0&&orderMode==1){
            return postListCache.get(offset+":"+limit);
        }
        logger.debug("load post list from DB");

        return discussPostMapper.selectDiscussPost(userId,offset,limit,orderMode);
    }
    public int findDiscussPostRows(int userId){

        if (userId==0){
            return postRowsCache.get(userId);
        }
        logger.debug("load post rows list from DB");

        return discussPostMapper.selectDiscussPostRows(userId);
    }

    //插入敏感词处理后的帖子
    public  int addDiscussPost(DiscussPost post){
        if (post==null){
            //帖子有数据为空
            throw new IllegalArgumentException("参数不能为空");
        }
        //对帖子post的涉及文字的变量title content进行铭感词过滤

        //把Html标签去掉
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        //插入数据
        return discussPostMapper.insertDiscussPost(post);
    }

    //根据帖子id查帖子
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }
    //根据帖子id查询其中的评论数
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    public int updateType(int id,int type){
        return discussPostMapper.updateType(id,type);
    }

    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }
    //更新帖子分数
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }
}
