package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {


	private ApplicationContext applicationContext;//用这个成员变量指向传入的Spring容器

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;//得到容器
	}

	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);
//		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);//方式一，找接口的Primary实现类
//		System.out.println(alphaDao.select());
//		AlphaDaoHibernatetmp alphaDao = applicationContext.getBean(AlphaDaoHibernatetmp.class);//方式二，直接类的类型
//		System.out.println(alphaDao.select());
		AlphaDao alphaDao = applicationContext.getBean("alphaDaoHibernatetmp",AlphaDao.class);//方式三，找类的名字
		System.out.println(alphaDao.select());
	}


	@Test
	public void testBeanManager(){
		AlphaService alphaservice = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaservice);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}
	@Autowired
	@Qualifier("alphaDaoHibernatetmp")
	public AlphaDao a;
	@Autowired
	public AlphaService s;
	@Test
	public void testDI(){
		System.out.println(a);
		System.out.println(s);
	}


}