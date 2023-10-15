package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.domain.CredentialDomain;
import cn.cjp.spider.domain.PipelineDomain;
import cn.cjp.spider.domain.PipelineDomain.EmailParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

        Stream<JSONObject> filterJsons = jsons.stream().filter(json -> {
            String id = json.getString("id");
            try (Jedis jedis = pool.getResource()) {
                String  taskId = task.getUUID();
                Long    r      = jedis.sadd("task:notify:" + taskId, id);
                boolean f      = Long.valueOf(1L).equals(r);
                if (f) {
                    log.info("新的通知：" + json);
                } else {
                    log.info("跳过重复通知：" + json);
                }
                return f;
            }
        });

        MailAccount account = new MailAccount();
        account.setHost(credential.getHost());
        account.setPort(credential.getPort());
        account.setAuth(credential.getAuth());
        account.setFrom(credential.getFrom());
        account.setUser(credential.getUser());
        account.setPass(credential.getPass());
        account.setSslEnable(credential.getSslEnable());

        filterJsons.forEach(json -> {
            try {
                EmailParams params = (EmailParams) getPipelineDomain().getParams();
                String      title  = getValue(params.getTitle(), json);
                String       content = getValue(params.getContent(), json);
                List<String> toUsers = params.getTo().stream().map(to -> getValue(to, json)).collect(Collectors.toList());

                for (String e : toUsers) {
                    try {
                        MailUtil.send(account, CollUtil.newArrayList(e), title, content, true);
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            } catch (RuntimeException e) {
                String id = json.getString("id");
                try (Jedis jedis = pool.getResource()) {
                    String taskId = task.getUUID();
                    jedis.srem("task:notify:" + taskId, id);
                    log.info("通知失败：" + e.getMessage() + "\r\n" + json, e);
                }
            }

        });

    }
}
