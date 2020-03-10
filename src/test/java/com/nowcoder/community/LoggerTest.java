package com.nowcoder.community;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {
    //实例化logger接口,构成的实参是    本类.class,Logger选sl44j
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);
    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        //记日志
        logger.debug("debuglog");
        logger.info("infolog");
        logger.warn("warnlog");
        logger.error("errorlog");
        //然后去Application。pro声明启用哪个级别的日志

    }
}
