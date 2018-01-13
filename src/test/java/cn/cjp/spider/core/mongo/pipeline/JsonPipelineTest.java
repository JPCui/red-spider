package cn.cjp.spider.core.mongo.pipeline;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cn.cjp.spider.Application;
import cn.cjp.spider.core.pipeline.mongo.JsonPipeline;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class JsonPipelineTest {

	@Autowired
	MongoTemplate mongoTemplate;

	@Test
	public void test() {
		JsonPipeline jsonPipeline = new JsonPipeline(mongoTemplate);
		System.out.println(jsonPipeline);
	}

}
