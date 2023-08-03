package cn.cjp.spider.manage;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.core.processor.SimpleProcessor;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import cn.cjp.spider.core.spider.AbstractSpider;
import cn.cjp.spider.core.spider.MyRedisSchedulerSpider;
import cn.cjp.spider.dto.ProcessorProperties;
import cn.cjp.spider.exception.ServiceException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
@RequiredArgsConstructor
public class SpiderManager {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    final ExecutorService executorService = Executors.newCachedThreadPool();

    final Map<String, Spider> runningSpiders = new HashMap<>();

    /**
     * TODO 现状：所有task都共用该参数，改为：由各自的配置文件指定；
     */
    final List<Pipeline> pipelines;

    final Scheduler scheduler;

    final SiteManager siteManager;

    public Collection<String> runningList() {
        return Collections.unmodifiableCollection(runningSpiders.keySet());
    }

    public void initSpiders() {
        ProcessorProperties props = new ProcessorProperties();

        SpiderConfig.PAGE_RULES.forEach((siteName, rule) -> {
            Spider spider = initProcessor(rule, props);
            runningSpiders.put(rule.getSiteName(), spider);
            log.info(String.format("spider[%s] start.", siteName));
            spider.start();
        });
    }

    public void start(String siteName) {
        try {
            this.lock.writeLock().lock();
            if (!runningSpiders.containsKey(siteName)) {
                Spider spider = initProcessor(SpiderConfig.PAGE_RULES.get(siteName), new ProcessorProperties());
                runningSpiders.put(siteName, spider);
                spider.start();
                log.info(String.format("spider[%s] start.", siteName));
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
            log.info(String.format("spider[%s] stop.", siteName));
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

    private Spider initProcessor(SiteModel siteModel, ProcessorProperties props) {
        Spider spider = buildSpider(siteModel, props);
        // 结束不自动关闭，默认 true
        spider.setExitWhenComplete(false);

        return spider;
    }

    public void run(String url) {
        Optional<SiteModel> siteModelOptional = siteManager.getSiteModel(url);

        siteModelOptional.ifPresent(siteModel -> {
            Spider spider = buildSpider(siteModel, new ProcessorProperties());
            spider.setExitWhenComplete(true);
            if (siteModel.isAutoStartup()) {
                spider.run();
                log.error(spider.getSite().getDomain() + " is ready and running.");
            } else {
                log.error(spider.getSite().getDomain() + " is ready.");
            }
        });

        if (!siteModelOptional.isPresent()) {
            throw new ServiceException("无对应的爬取模板");
        }
    }

    private Spider buildSpider(SiteModel siteModel, ProcessorProperties props) {
        return this.buildSpider(siteModel, props, true);
    }

    private Spider buildSpider(SiteModel siteModel, ProcessorProperties props, boolean onceOnly) {
        SimpleProcessor simpleProcessor = new SimpleProcessor(siteModel);
        simpleProcessor.setOnceOnly(onceOnly);

        // FIXME 应该在 SimpleProcessor 构造函数中实现这段逻辑
        Site site = simpleProcessor.getSite();
        site.addHeader("User-Agent", UserAgents.get());
        site.setDomain(siteModel.getSiteName());
        site.setSleepTime(props.getSleepTime()); // 每次抓取完畢，休息
        site.setRetrySleepTime(props.getRetrySleepTime()); // 重试休息时间：30s
        site.setRetryTimes(props.getRetryTimes()); // 重试 10次
        site.setTimeOut(props.getTimeout()); // 超时时间 30s

        AbstractSpider spider = new MyRedisSchedulerSpider(simpleProcessor, (MyRedisScheduler) scheduler);
        pipelines.forEach(spider::addPipeline);
        spider.addUrl(siteModel.getUrl()).thread(executorService, props.getThreadNum());
        // 结束不自动关闭，默认 true
        spider.setExitWhenComplete(false);

        return spider;
    }


}
