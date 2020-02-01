package cn.cjp.spider.core.pipeline;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Slf4j
public class SimplePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (log.isDebugEnabled()) {
            log.debug(JSON.toJSONString(resultItems));
        }
    }

}
