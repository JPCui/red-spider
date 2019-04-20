package cn.cjp.spider.manage;

import cn.cjp.spider.core.MyRedisSchedulerSpider;
import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.core.processor.SimpleProcessor;
import cn.cjp.spider.dto.ProcessorProperties;
import cn.cjp.spider.exception.ServiceException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
@AllArgsConstructor
public class SpiderManager {

    final ExecutorService executorService = Executors.newCachedThreadPool();

    final Map<String, Spider> runningSpiders = new HashMap<>();

    final ReadWriteLock lock = new ReentrantReadWriteLock();

    Pipeline pipeline;

    Scheduler scheduler;

    SiteManager siteManager;

    public Collection<String> runningList() {
        return Collections.unmodifiableCollection(runningSpiders.keySet());
    }

    public void initSpiders() {
        ProcessorProperties props = new ProcessorProperties();

        SpiderConfig.PAGE_RULES.forEach((siteName, rule) -> {
            MyRedisSchedulerSpider spider = initProcessor(rule, props);
            runningSpiders.put(rule.getSiteName(), spider);
            log.info("spider[%s] start.", siteName);
            spider.start();
        });
    }

    public void start(String siteName) {
        try {
            this.lock.writeLock().lock();
            if (!runningSpiders.containsKey(siteName)) {
                MyRedisSchedulerSpider spider = initProcessor(SpiderConfig.PAGE_RULES.get(siteName), new ProcessorProperties());
                runningSpiders.put(siteName, spider);
                spider.start();
                log.info("spider[%s] start.", siteName);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void stop(String siteName) {
        try {
            this.lock.writeLock().lock();
            if (runningSpiders.containsKey(siteName)) {
                runningSpiders.get(siteName).stop();
                runningSpiders.remove(siteName);
            }
            log.info("spider[%s] stop.", siteName);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void config(String siteName, ProcessorProperties props) {
        if (runningSpiders.containsKey(siteName)) {
            Spider spider = runningSpiders.get(siteName);
            Site   site   = spider.getSite();
            site.setSleepTime(props.getSleepTime()); // 每次抓取完畢，休息
            site.setRetrySleepTime(props.getRetrySleepTime()); // 重试休息时间：30s
            site.setRetryTimes(props.getRetryTimes()); // 重试 10次
            site.setTimeOut(props.getTimeout()); // 超时时间 30s
        }
    }

    private MyRedisSchedulerSpider initProcessor(SiteModel siteModel, ProcessorProperties props) {
        SimpleProcessor simpleProcessor = new SimpleProcessor();
        simpleProcessor.setSiteModel(siteModel);

        Site site = new Site();
        site.addHeader("User-Agent", UserAgents.get());
        site.setDomain(siteModel.getSiteName());
        site.setSleepTime(props.getSleepTime()); // 每次抓取完畢，休息
        site.setRetrySleepTime(props.getRetrySleepTime()); // 重试休息时间：30s
        site.setRetryTimes(props.getRetryTimes()); // 重试 10次
        site.setTimeOut(props.getTimeout()); // 超时时间 30s
        simpleProcessor.setSite(site);

        MyRedisSchedulerSpider spider = new MyRedisSchedulerSpider(simpleProcessor);
        spider.setScheduler(scheduler).addPipeline(pipeline)
            .addUrl(siteModel.getUrl()).thread(executorService, props.getThreadNum());
        // 结束不自动关闭，默认 true
        spider.setExitWhenComplete(false);

        return spider;
    }

    public void run(String url) {
        Optional<SiteModel> siteModelOptional = siteManager.getSiteModel(url);

        siteModelOptional.ifPresent(siteModel -> {
            siteModel.setSeedDiscoveries(Collections.emptyList());
            siteModel.setUrl(url);

            SimpleProcessor simpleProcessor = new SimpleProcessor();
            simpleProcessor.setSiteModel(siteModel);

            Site site = new Site();
            site.addHeader("User-Agent", UserAgents.get());
            site.setDomain(siteModel.getSiteName());
            simpleProcessor.setSite(site);

            MyRedisSchedulerSpider spider = new MyRedisSchedulerSpider(simpleProcessor);
            spider.setExitWhenComplete(true);
            spider.setScheduler(new QueueScheduler()).addPipeline(pipeline)
                .addUrl(url).thread(executorService, 1);
            spider.run();

            log.error(spider.getSite().getDomain() + " running.");
        });

        if (!siteModelOptional.isPresent()) {
            throw new ServiceException("无对应的爬取模板");
        }
    }


}
