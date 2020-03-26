package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;

import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//用于帖子分数的刷新
public class PostScoreRefreshJob implements Job, CommunityConstant {
    public static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    private static final Date epoch;

    static{
        try {
            //设置成立时间
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化网站起始时加你失败,e");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //得到key
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //把key的数据存到operations
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size()==0){
            logger.info("{任务取消}没有需要刷新的帖子!");
        }
        logger.info("{任务开始}正在刷新帖子分数,需要刷新的帖子数为:"+operations.size());
        while (operations.size()>0){
            //刷新帖子分数
            this.refresh((Integer)operations.pop());
        }
        logger.info("{任务完成}帖子分数刷新完成");
    }
    //用于刷新帖子分数
    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post==null){
            logger.error("该帖子不存在,id="+postId);
            return;
        }
        //是否精华
        boolean wonderful = post.getStatus()==1;
        //评论数量
        int commentCount = post.getCommentCount();
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,postId);
        //计算权重
        double w = (wonderful?75:0) + commentCount*10+likeCount*2;
        //分数=点赞评论精华权重+发布日期差
        double score = Math.log10(Math.max(w,1)) + (post.getCreateTime().getTime()-epoch.getTime())/(1000*3600*24);
        discussPostService.updateScore(postId,score);
        //同步搜索的数据到Es
        elasticsearchService.saveDiscussPost(post);
    }
}
