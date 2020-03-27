package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    //用于测试四个注解beforeClass，afterClass，before，after
    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    //类加载之前执行，只执行一次
    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }
    //类销毁之后执行，只执行一次

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }

    //类加载之前执行，每次调用测试方法都会执行
    @Before
    public void before() {
        System.out.println("before");

        // 初始化测试数据
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test Title");
        data.setContent("Test Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    //类销毁之后执行，每次调用测试方法都会执行
    @After
    public void after() {
        System.out.println("after");

        // 删除测试数据
        discussPostService.updateStatus(data.getId(), 2);
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void testFindById() {
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        //Assert(断言类)判断调用方法查到的结果和初始化数据是否相符合
        //输入参数是否为空
        Assert.assertNotNull(post);
        //两个参数是否相同
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
    }

    @Test
    public void testUpdateScore() {
        int rows = discussPostService.updateScore(data.getId(), 2000.00);
        //判断调用方法查到的结果和初始化数据是否相符合

        Assert.assertEquals(1, rows);

        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertEquals(2000.00, post.getScore(), 2);
    }

}
