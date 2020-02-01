package cn.cjp.spider.core.processor;

import cn.cjp.spider.core.spider.MyRedisSchedulerSpider;
import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.core.pipeline.FilePipeline;
import cn.cjp.spider.core.pipeline.mongo.JsonPipeline;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import cn.cjp.utils.Logger;
import com.mongodb.MongoClientURI;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

public class SimpleProcessorTest {

    private static final Logger LOGGER = Logger.getLogger(SimpleProcessorTest.class);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private static final int threadNum = Runtime.getRuntime().availableProcessors() * 2;

    private Scheduler scheduler;

    public void run() {
        final Map<String, SiteModel> map = SpiderConfig.PAGE_RULES;

        map.forEach((siteName, siteModel) -> {
            runSpider(siteModel);
        });
    }

    private void runSpider(SiteModel siteModel) {
        SimpleProcessor simpleProcessor = new SimpleProcessor();
        simpleProcessor.setSiteModel(siteModel);

        Site site = new Site();
        site.addHeader("User-Agent", UserAgents.get());
        site.setDomain(siteModel.getSiteName());
        site.setSleepTime(1000); // 每次抓取完畢，休息
        site.setRetrySleepTime(30_000); // 重试休息时间：30s
        site.setRetryTimes(5); // 重试 10次
        site.setTimeOut(30_000); // 超时时间 30s
        simpleProcessor.setSite(site);

        try {
            MyRedisSchedulerSpider spider = new MyRedisSchedulerSpider(simpleProcessor, (MyRedisScheduler) scheduler);
            spider.setScheduler(scheduler).addPipeline(getJsonPipeline()).addPipeline(new FilePipeline("~/tmp/spider/"))
                .addUrl(siteModel.getUrl()).thread(executorService, threadNum);
            // 结束不自动关闭，默认 true
            spider.setExitWhenComplete(false);
            spider.run();

            LOGGER.error(spider.getSite().getDomain() + " running.");
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public JsonPipeline getJsonPipeline() throws UnknownHostException {
        SimpleMongoDbFactory factory       = new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost:27017/test"));
        MongoTemplate        mongoTemplate = new MongoTemplate(factory);
        JsonPipeline         jsonPipeline  = new JsonPipeline(mongoTemplate);
        return jsonPipeline;
    }

    @Test
    public void runTest() {
        SimpleProcessorTest test = new SimpleProcessorTest();
        test.scheduler = new MyRedisScheduler(new JedisPool());
        test.runSpider(SpiderConfig.PAGE_RULES.get("douban.movie"));
    }

    @Test
    public void runOneUrl() {

        SiteModel siteModel = SpiderConfig.PAGE_RULES.get("douban.movie");
        siteModel.setSeedDiscoveries(Collections.emptyList());
        siteModel.setUrl("https://movie.douban.com/subject/27624661/");
        SimpleProcessorTest test = new SimpleProcessorTest();
        test.scheduler = new QueueScheduler();
        test.runSpider(siteModel);
    }

}
