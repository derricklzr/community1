package com.nowcoder.community.controller;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    //增加帖子功能
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        //查看登录没
        if(user==null){
            return CommunityUtil.getJSONString(403,"请先登录再进行此项操作！");
        }
            DiscussPost post = new DiscussPost();
            //插入帖子
            post.setUserId(user.getId());
            post.setTitle(title);
            post.setContent(content);
            post.setCreateTime(new Date());
            discussPostService.addDiscussPost(post);
            //报错的情况将来统一处理
            return CommunityUtil.getJSONString(0,"发布帖子成功:)");

    }

    //查询帖子功能
    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //通过userId找name
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        int likeStatus =hostHolder.getUser()==null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);


        //评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());
        //评论：给帖子的评论
        //回复：给评论的评论
        //下面得到帖子的评论
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());
        //评论的vo列表(原始评论+用户信息)
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList!=null){
            for (Comment comment:commentList){
                //遍历，每个Map为一个真正的评论(比初始评论多查了个了用户)
                Map<String,Object> commentVo = new HashMap<>();
                //评论信息
                commentVo.put("comment",comment);
                //评论所属作者信息

                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
               commentVo.put("likeCount",likeCount);
                //点赞状态
                likeStatus =hostHolder.getUser()==null ? 0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);


                //回复的列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复的vo列表(原始回复+用户信息)
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList!=null){
                    for (Comment reply:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复信息
                        replyVo.put("reply",reply);
                        //回复所属用户信息
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复针对的目标
                        User target = reply.getTargetId()==0?null:userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        //点赞状态
                        likeStatus =hostHolder.getUser()==null ? 0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);



                        //装好某个评论的所有回复
                        replyVoList.add(replyVo);
                    }
                }
                //装好某个帖子的所有评论的所有回复
                commentVo.put("replys",replyVoList);
                //所有的评论的回复数量
                int replyCount = commentService.findCountByEntity(ENTITY_TYPE_COMMENT,comment.getId());
                //装好某个帖子的所有评论的所有回复数量
                commentVo.put("replyCount",replyCount);
                //装好一个帖子的所需的评论和回复
                commentVoList.add(commentVo);

            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
        //帖子回复功能还没搞
    }


}
