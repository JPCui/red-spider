package cn.cjp.spider.core.processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.pipeline.FilePipeline;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONObject;

import cn.cjp.spider.core.model.PageModel;
import cn.cjp.utils.JacksonUtil;
import cn.cjp.utils.Logger;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisPriorityScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

public class SimpleProcessorTest {

    private static final Logger LOGGER = Logger.getLogger(SimpleProcessorTest.class);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private JedisPool jedisPool = new JedisPool("localhost");

    private Scheduler scheduler = new RedisPriorityScheduler(jedisPool);

    public SimpleProcessorTest() throws IOException {
        final Map<String, PageModel> map = SpiderConfig.PAGE_RULES;

        map.forEach((siteName, siteModel) -> {
            SimpleProcessor simpleProcessor = new SimpleProcessor();
            simpleProcessor.setPageModel(siteModel);

            Site site = new Site();
            site.setDomain(siteModel.getSiteName());
            simpleProcessor.setSite(site);

            Spider spider = Spider.create(simpleProcessor).setScheduler(scheduler).addPipeline(new ConsolePipeline())
                    .addPipeline(new FilePipeline("D:/spider/")).addUrl(siteModel.getUrl());

//            executorService.execute(spider);
            spider.run();
            LOGGER.error(spider.getSite().getDomain() + " running.");
        });


    }

    public static void main(String[] args) throws IOException {
        SimpleProcessorTest test = new SimpleProcessorTest();
    }

}
