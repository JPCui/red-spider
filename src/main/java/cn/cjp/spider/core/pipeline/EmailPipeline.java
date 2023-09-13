package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.model.CredentialDomain;
import cn.cjp.spider.core.model.PipelineDomain;
import cn.cjp.spider.core.model.PipelineDomain.EmailParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailPipeline extends AbstractPipeline {

    final CredentialDomain credential;
    final JedisPool        pool;

    public EmailPipeline(PipelineDomain pipelineDomain, CredentialDomain credential, JedisPool jedisPool) {
        super(pipelineDomain);
        this.credential = credential;
        this.pool = jedisPool;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<JSONObject> jsons = resultItems.get("jsons");
        if (jsons == null) {
            return;
        }

        jsons.stream().filter(json -> {
            String id = json.getString("id");
            try (Jedis jedis = pool.getResource()) {
                String taskId = task.getUUID();
                Long   r      = jedis.sadd("task:notify:" + taskId, id);
                return Long.valueOf(1L).equals(r);
            }
        }).forEach(json -> {
            EmailParams  params  = (EmailParams) getPipelineDomain().getParams();
            String       title   = getValue(params.getTitle(), json);
            String       content = getValue(params.getContent(), json);
            List<String> toUsers = params.getTo().stream().map(to -> getValue(to, json)).collect(Collectors.toList());

            MailAccount account = new MailAccount();
            account.setHost(credential.getHost());
            account.setPort(credential.getPort());
            account.setAuth(credential.getAuth());
            account.setFrom(credential.getFrom());
            account.setUser(credential.getUser());
            account.setPass(credential.getPass());
            account.setSslEnable(credential.getSslEnable());

            for (String e : toUsers) {
                try {
                    MailUtil.send(
                        account,
                        CollUtil.newArrayList(e),
                        title, content, true);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }

        });

    }
}
