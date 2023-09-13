package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.model.PipelineDomain;
import cn.cjp.spider.core.model.PipelineDomain.EmailParams;
import cn.cjp.spider.core.model.PipelineDomain.Params;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
@RequiredArgsConstructor
public class PipelineFactory {

    final JedisPool jedisPool;

    public AbstractPipeline get(PipelineDomain pipelineDomain) {
        Params params = pipelineDomain.getParams();
        if (params instanceof EmailParams) {
            return new EmailPipeline(pipelineDomain, SpiderConfig.mockEmailCredential(), jedisPool);
        } else {
            return new SimplePipeline(pipelineDomain);
        }

    }

}
