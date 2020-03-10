package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service

public class AlphaService {

    @Autowired
    private AlphaDao dao;

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

}
