package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    //查询某一页数据
    public List<Comment> findCommentByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentByEntity(entityType,entityId,offset,limit);
    }
    public int findCountByEntity(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }
    //给帖子增加评论.并把给帖子增加评论+更新帖子评论数打包成一个事务
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        //铭感词，标签过滤
        if(comment==null){
            throw new IllegalArgumentException();
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //插入帖子的评论
        int rows = commentMapper.insertComment(comment);
        //更新帖子的评论数量
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);

        }
        return rows;

    }


}
