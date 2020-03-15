package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service

public class AlphaService {

    @Autowired
    private AlphaDao dao;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public String find(){
        return dao.select();
    }

    public AlphaService(){
        System.out.println("构造器AlphaService ");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService ");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService ");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save(){
        //新增用户再发帖子，一体
        User user = new User();
        user.setUsername("zc");
        user.setSalt(CommunityUtil.generateUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("email@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("新人报到标题");
        post.setContent("新人报道内容");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);
        Integer.valueOf("abc");//错误
        return "ok";
    }
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //事务
                return null;
            }
        });
    }
}
