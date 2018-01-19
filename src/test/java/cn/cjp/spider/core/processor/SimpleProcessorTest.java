package cn.cjp.spider.core.processor;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClientURI;

import cn.cjp.spider.core.MyRedisSchedulerSpider;
import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.PageModel;
import cn.cjp.spider.core.pipeline.FilePipeline;
import cn.cjp.spider.core.pipeline.mongo.JsonPipeline;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import cn.cjp.utils.Logger;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Site;

public class SimpleProcessorTest {

	private static final Logger LOGGER = Logger.getLogger(SimpleProcessorTest.class);

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private JedisPool jedisPool = new JedisPool("localhost");

	private MyRedisScheduler scheduler = new MyRedisScheduler(jedisPool);

	public SimpleProcessorTest() throws Exception {
		final Map<String, PageModel> map = SpiderConfig.PAGE_RULES;

		map.forEach((siteName, siteModel) -> {
			SimpleProcessor simpleProcessor = new SimpleProcessor();
			simpleProcessor.setPageModel(siteModel);

			Site site = new Site();
			site.addHeader("User-Agent", UserAgents.get());
			site.setDomain(siteModel.getSiteName());
			site.setSleepTime(3000);
			site.setRetrySleepTime(30_000); // 重试休息时间：10s
			site.setRetryTimes(5); // 重试 10次
			site.setTimeOut(30_000); // 超时时间 30s
			simpleProcessor.setSite(site);

			try {
				MyRedisSchedulerSpider spider = new MyRedisSchedulerSpider(simpleProcessor);
				spider.setScheduler(scheduler).addPipeline(getJsonPipeline())
						.addPipeline(new FilePipeline("D:/spider/")).addUrl(siteModel.getUrl())
						.thread(executorService, 5);
				// 结束不自动关闭，默认 true
				spider.setExitWhenComplete(false);
				spider.start();

				LOGGER.error(spider.getSite().getDomain() + " running.");
			} catch (UnknownHostException e) {
				LOGGER.error(e.getMessage(), e);
			}

		});

	}

	public JsonPipeline getJsonPipeline() throws UnknownHostException {
		SimpleMongoDbFactory factory = new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost:27017/test"));
		MongoTemplate mongoTemplate = new MongoTemplate(factory);
		JsonPipeline jsonPipeline = new JsonPipeline(mongoTemplate);
		return jsonPipeline;

	}

	public static void main(String[] args) throws Exception {
		new SimpleProcessorTest();
	}

}
