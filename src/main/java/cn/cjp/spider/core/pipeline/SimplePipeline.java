package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.model.PipelineDomain;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

@Slf4j
public class SimplePipeline extends AbstractPipeline {

    public SimplePipeline(PipelineDomain pipelineDomain) {
        super(pipelineDomain);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.get("json") != null) {
            log.info("json: " + resultItems.get("json").toString());
        } else if (resultItems.get("jsons") != null) {
            log.info("jsons: " + resultItems.get("jsons").toString());
        }
    }

}
