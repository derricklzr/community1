package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommunityUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    //用于自定义暴露数据库的端点
    public static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);
    //通过连接池访问数据库
    @Autowired
    private DataSource dataSource;

    @ReadOperation//表示这个请求通过GET请求访问，POST请求为WriteOperation
    public String checkConnection(){
        try (
                Connection connection = dataSource.getConnection();
                ){
            return CommunityUtil.getJSONString(0,"获取数据库连接成功");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("获取数据库连接失败"+e.getMessage());
            return CommunityUtil.getJSONString(0,"获取数据库连接成功");
        }
    }
}
