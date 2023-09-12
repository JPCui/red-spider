package cn.cjp.spider.core.processor;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.core.pipeline.FilePipeline;
import cn.cjp.spider.core.pipeline.mongo.JsonPipeline;
import cn.cjp.spider.core.processor.html.SimpleProcessor;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import cn.cjp.spider.core.spider.MyRedisSchedulerSpider;
import com.google.common.collect.Lists;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

@Slf4j
public class HtmlProcessorTest {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private static final int threadNum = Runtime.getRuntime().availableProcessors() * 2;

    private Scheduler     scheduler;
    private MongoTemplate mongoTemplate;

    public void run() {
        final Map<String, SiteModel> map = SpiderConfig.PAGE_RULES;

        map.forEach((siteName, siteModel) -> {
            runSpider(siteModel);
        });
    }

    private void runSpider(SiteModel siteModel) {
        SimpleProcessor simpleProcessor = new SimpleProcessor(siteModel);

        try {
            MyRedisSchedulerSpider spider = new MyRedisSchedulerSpider(simpleProcessor, (MyRedisScheduler) scheduler);
            spider.setScheduler(scheduler).addPipeline(getJsonPipeline()).addPipeline(new FilePipeline("~/tmp/spider/"))
                .addUrl(siteModel.getOriginUrls().toArray(new String[0])).thread(executorService, threadNum);
            // 结束不自动关闭，默认 true
            spider.setExitWhenComplete(false);
            spider.run();

            log.error(spider.getSite().getDomain() + " running.");
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }

    }

    public JsonPipeline getJsonPipeline() throws UnknownHostException {
        JsonPipeline jsonPipeline = new JsonPipeline(mongoTemplate);
        return jsonPipeline;
    }

    @Test
    public void runTest() {
        HtmlProcessorTest test = new HtmlProcessorTest();
        test.scheduler = new MyRedisScheduler(new JedisPool());
        test.runSpider(SpiderConfig.PAGE_RULES.get("douban.movie"));
    }

    @Test
    public void runOneUrl() {

        SiteModel siteModel = SpiderConfig.PAGE_RULES.get("douban.movie");
        siteModel.setSeedDiscoveries(Collections.emptyList());
        siteModel.setOriginUrls(Lists.newArrayList("https://movie.douban.com/subject/27624661/"));
        HtmlProcessorTest test = new HtmlProcessorTest();
        test.scheduler = new QueueScheduler();
        test.runSpider(siteModel);
    }

}
