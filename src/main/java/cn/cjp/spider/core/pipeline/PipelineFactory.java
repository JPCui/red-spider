package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.Constants;
import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.domain.CredentialDomain;
import cn.cjp.spider.domain.CredentialRepo;
import cn.cjp.spider.domain.PipelineDomain;
import cn.cjp.spider.domain.PipelineDomain.EmailParams;
import cn.cjp.spider.domain.PipelineDomain.Params;
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

    final JedisPool      jedisPool;
    final CredentialRepo credentialRepo;

    public AbstractPipeline get(PipelineDomain pipelineDomain) {
        Params params = pipelineDomain.getParams();
        if (params instanceof EmailParams) {
            CredentialDomain credential = credentialRepo.getOrDefaultByUserId(
                Constants.DEFAULT_USER_ID,
                ((EmailParams) params).getCredentialId());
            return new EmailPipeline(pipelineDomain, credential, jedisPool);
        } else {
            return new SimplePipeline(pipelineDomain);
        }

    }

}
