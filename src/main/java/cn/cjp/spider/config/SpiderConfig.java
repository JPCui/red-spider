package cn.cjp.spider.config;

import cn.cjp.spider.core.pipeline.mongo.MongoPipeline;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

@Configuration
@RequiredArgsConstructor
public class SpiderConfig {

    private final RedisProperties properties;

    /**
     * 消费通道
     */
    @Bean("mongoPipeline")
    Pipeline pipeline(@Autowired MongoTemplate mongoTemplate) {
        return new MongoPipeline(mongoTemplate);
    }

    @Bean
    JedisPool jedisPool() {
        return new JedisPool(properties.getHost(),
                             properties.getPort(),
                             properties.getUsername(),
                             properties.getPassword());
    }

    /**
     * 调度器（种子队列）
     */
    @Bean("myRedisScheduler")
    Scheduler scheduler(JedisPool jedisPool) {
        return new MyRedisScheduler(jedisPool);
    }

}
