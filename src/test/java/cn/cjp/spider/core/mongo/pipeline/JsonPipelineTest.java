package cn.cjp.spider.core.mongo.pipeline;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONObject;

import cn.cjp.spider.SpiderAppApplication;
import cn.cjp.spider.core.config.SpiderConst;
import cn.cjp.spider.core.pipeline.mongo.JsonPipeline;
import us.codecraft.webmagic.ResultItems;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpiderAppApplication.class)
@WebAppConfiguration
public class JsonPipelineTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void test() {
        JsonPipeline jsonPipeline = new JsonPipeline(mongoTemplate);

        JSONObject json = new JSONObject();
        json.put(SpiderConst.KEY_TABLE_NAME, "test");
        json.put(SpiderConst.KEY_UNIQUE, "id");
        json.put("id", 1515924802512L);
        json.put("name", "jack");

        ResultItems resultItems = new ResultItems();
        resultItems.put("json", json);

        jsonPipeline.process(resultItems, null);

        json.put("id", 1515924802512L);
        json.put("name", "jack2");
        jsonPipeline.process(resultItems, null);
    }

}
