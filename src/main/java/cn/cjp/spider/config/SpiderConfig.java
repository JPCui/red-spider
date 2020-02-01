package cn.cjp.spider.config;

import cn.cjp.spider.core.pipeline.mongo.MongoPipeline;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

@Configuration
public class SpiderConfig {

    /**
     * 消费通道
     * @param mongoTemplate
     * @return
     */
    @Bean("mongoPipeline")
    Pipeline pipeline(@Autowired MongoTemplate mongoTemplate) {
        return new MongoPipeline(mongoTemplate);
    }

    /**
     * 调度器（种子队列）
     * @return
     */
    @Bean("myRedisScheduler")
    Scheduler scheduler() {
        return new MyRedisScheduler(new JedisPool());
    }

}
