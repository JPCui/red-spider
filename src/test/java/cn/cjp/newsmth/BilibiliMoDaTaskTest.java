package cn.cjp.newsmth;

import cn.cjp.spider.task.BilibiliMoDaTask;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class BilibiliMoDaTaskTest {

    public static void main(String[] args) throws InterruptedException {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setPassword("123456");
        factory.afterPropertiesSet();

        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.afterPropertiesSet();

        new BilibiliMoDaTask(redisTemplate, null).execute();

    }

}
