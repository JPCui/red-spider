package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.model.PipelineDomain;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailPipeline extends AbstractPipeline {

    final JedisPool pool;

    public EmailPipeline(PipelineDomain pipelineDomain, JedisPool jedisPool) {
        super(pipelineDomain);
        this.pool = jedisPool;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<JSONObject> jsons = resultItems.get("jsons");
        if(jsons == null) {
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
            System.out.println(json);
        });

    }
}
