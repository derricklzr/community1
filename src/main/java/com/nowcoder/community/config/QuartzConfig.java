package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//第一次读取有用此工具类会将配置弄到数据库中
//以后调用会到数据库调用
// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.
    //模板
    // 配置JobDetail
   //  @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);//管理的bean的类型
        factoryBean.setName("alphaJob");//这个job的名字
        factoryBean.setGroup("alphaJobGroup");//这个任务所处的组
        factoryBean.setDurability(true);//任务是持久保存
        factoryBean.setRequestsRecovery(true);//任务可恢复
        return factoryBean;
    }
    //模板
    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
   // @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);//注入jobDetail
        factoryBean.setName("alphaTrigger");//trigger名
        factoryBean.setGroup("alphaTriggerGroup");//trigger组
        factoryBean.setRepeatInterval(3000);//频率
        factoryBean.setJobDataMap(new JobDataMap());//用什么对象存job状态
        return factoryBean;
    }

    //刷新帖子分数
    // 配置JobDetail
      @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);//管理的bean的类型
        factoryBean.setName("postScoreRefreshJob");//这个job的名字
        factoryBean.setGroup("communityJobGroup");//这个任务所处的组
        factoryBean.setDurability(true);//任务是持久保存
        factoryBean.setRequestsRecovery(true);//任务可恢复
        return factoryBean;
    }
    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     @Bean
    public SimpleTriggerFactoryBean PostScoreRefreshJobTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);//注入jobDetail
        factoryBean.setName("postScoreRefreshTrigger");//trigger名
        factoryBean.setGroup("communityTriggerGroup");//trigger组
        factoryBean.setRepeatInterval(1000*60*5);//5分钟刷新一次
        factoryBean.setJobDataMap(new JobDataMap());//用什么对象存job状态
        return factoryBean;
    }

}
