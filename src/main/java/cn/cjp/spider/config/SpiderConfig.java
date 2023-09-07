package cn.cjp.spider.config;

import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.scheduler.Scheduler;

@Configuration
@RequiredArgsConstructor
public class SpiderConfig {

    private final RedisProperties properties;

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
