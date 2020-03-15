package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Target;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSen(){
        String text = "张弛赌￥博";
        text=sensitiveFilter.filter(text);
        System.out.println(text);

    }
}
