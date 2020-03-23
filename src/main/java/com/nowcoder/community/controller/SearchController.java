package com.nowcoder.community.controller;

import com.nowcoder.community.entity.CommunityConstant;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    //搜索功能,路径用/search?keyword=....
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
        elasticsearchService.searchDiscussPost(keyword,page.getCurrent()-1,page.getLimit());
        //聚合：帖子结果+用户信息
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (searchResult!=null){
            for (DiscussPost post:searchResult){
                //遍历帖子
                Map<String,Object> map = new HashMap<>();
                //装帖子
                map.put("post",post);
                //装用户
                map.put("user",userService.findUserById(post.getUserId()));
                //装点赞数
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        //分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult==null?0:(int)searchResult.getTotalElements());
        return "/site/search";
    }
}
