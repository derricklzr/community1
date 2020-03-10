package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository//
public class AlphaDaoHibernatetmp implements AlphaDao{
    @Override
    public String select() {
        return "张弛真是个傻逼";
    }
}
