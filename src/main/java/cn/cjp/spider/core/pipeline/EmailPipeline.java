package cn.cjp.spider.core.pipeline;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class EmailPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<JSONObject> jsons = resultItems.get("jsons");

        jsons.forEach(System.out::println);

    }
}
